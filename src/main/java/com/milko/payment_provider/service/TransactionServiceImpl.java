package com.milko.payment_provider.service;

import com.milko.payment_provider.dto.CustomerDto;
import com.milko.payment_provider.dto.TransactionDto;
import com.milko.payment_provider.mapper.CardDataMapper;
import com.milko.payment_provider.mapper.CustomerMapper;
import com.milko.payment_provider.mapper.TransactionMapper;
import com.milko.payment_provider.model.*;
import com.milko.payment_provider.repository.CardDataRepository;
import com.milko.payment_provider.repository.CustomerRepository;
import com.milko.payment_provider.repository.TransactionRepository;
import com.milko.payment_provider.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final ReactiveTransactionManager transactionManager;
    private final CardDataRepository cardRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final CardDataMapper cardDataMapper;
    private final CustomerMapper customerMapper;
    private final TransactionMapper transactionMapper;
    private final WebhookService webhookService;

    public Mono<TransactionDto> createTransaction(TransactionDto dto, UUID merchantId, TransactionType type) {
        log.info("IN TransactionServiceImpl createTransaction. Type: {}, merchantId: {}, amount: {}", type, merchantId, dto.getAmount());
        CardData cardData = cardDataMapper.map(dto.getCardData());
        Customer customer = customerMapper.map(dto.getCustomer());
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        dto.setType(type);
        dto.setStatus(TransactionStatus.IN_PROGRESS);

        Transaction transaction = transactionMapper.map(dto);
        transaction.setMerchantId(merchantId);

        Mono<Void> balanceCheck = findWalletAndCheckBalance(merchantId, dto);

        TransactionalOperator operator = TransactionalOperator.create(transactionManager);
        return operator.transactional(
                balanceCheck.then(
                                saveCustomerAndCardIfNotExistAndGetId(customer, cardData)
                                        .flatMap(savedCustomerId -> {
                                            transaction.setCustomerId(savedCustomerId);
                                            return walletRepository.getFirstByCurrencyAndMerchantId(transaction.getCurrency(), merchantId);
                                        })
                                        .flatMap(wallet -> {
                                            transaction.setWalletId(wallet.getId());
                                            return transactionRepository.save(transaction)
                                                    .flatMap(savedTransaction ->
                                                            webhookService.sendWebHook(savedTransaction)
                                                                    .thenReturn(savedTransaction)
                                                    );
                                        })
                                        .map(transactionMapper::map))
                        .doOnError(throwable -> {
                            dto.setStatus(TransactionStatus.FAILED);
                        }));
    }

    public Flux<TransactionDto> getAllTransactions(Long startDate, Long endDate, UUID merchantId, TransactionType type) {
        log.info("IN TransactionServiceImpl getAllTransactions. Type: {}, merchantId: {}, startDate: {}, endDate: {}", type, merchantId, startDate, endDate);
        LocalDate start = startDate != null ?
                Instant.ofEpochSecond(startDate).atZone(ZoneId.systemDefault()).toLocalDate() : null;

        LocalDate end = endDate != null ?
                Instant.ofEpochSecond(endDate).atZone(ZoneId.systemDefault()).toLocalDate() : null;

        if (start != null && end != null) {
            return transactionRepository.getAllBetweenDates(start, end, merchantId, type)
                    .flatMap(this::mapTransactionToDto);
        } else if (start != null) {
            return transactionRepository.getAllFromDate(start, merchantId, type)
                    .flatMap(this::mapTransactionToDto);
        } else if (end != null) {
            return transactionRepository.getAllBeforeDay(end, merchantId, type)
                    .flatMap(this::mapTransactionToDto);
        } else {
            return transactionRepository.getAllForToday(merchantId, type)
                    .flatMap(this::mapTransactionToDto);
        }
    }

    public Mono<TransactionDto> findTransactionById(UUID id, UUID merchantId, TransactionType type) {
        log.info("IN TransactionServiceImpl findTransactionById. Type: {}, merchantId: {}, UUID: {}", type, merchantId, id);
        return transactionRepository.findByIdAndMerchantIdAndType(id, merchantId, type)
                .flatMap(this::mapTransactionToDto);
    }

    private Mono<TransactionDto> mapTransactionToDto(Transaction transaction) {
        return Mono.zip(
                        Mono.just(transaction),
                        customerRepository.findById(transaction.getCustomerId()).map(customerMapper::map)
                )
                .map(tuple -> {
                    Transaction trans = tuple.getT1();
                    CustomerDto customerDto = tuple.getT2();
                    return transactionMapper.map(trans, customerDto);
                });
    }

    private Mono<Void> findWalletAndCheckBalance(UUID merchantId, TransactionDto dto) {
        Integer transactionAmount = dto.getAmount();
        return walletRepository.getFirstByCurrencyAndMerchantId(dto.getCurrency(), merchantId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found")))
                .flatMap(wallet -> {
                    if (dto.getType().equals(TransactionType.PAYOUT)){
                        if (transactionAmount > wallet.getAmount()) {
                            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds in wallet"));
                        }
                    }
                    return Mono.empty();
                });
    }

    private Mono<UUID> saveCustomerAndCardIfNotExistAndGetId(Customer customer, CardData cardData) {
        log.info("IN saveCustomerAndCardIfNotExistAndGetId. Customer = {}, CardData = {}", customer, cardData);
        return customerRepository.getByNameAndCardNumber(customer.getFirstName(), customer.getLastName(), cardData.getCardNumber())
                .map(Customer::getId)
                .switchIfEmpty(Mono.defer(
                        () -> customerRepository.save(customer)
                                .flatMap(savedCustomer -> {
                                    cardData.setCustomerId(savedCustomer.getId());
                                    return cardRepository.save(cardData)
                                            .thenReturn(customer.getId());
                                })
                ));
    }

}
