package com.milko.payment_provider.service;

import com.milko.payment_provider.dto.CardDataDto;
import com.milko.payment_provider.dto.CustomerDto;
import com.milko.payment_provider.dto.TransactionDto;
import com.milko.payment_provider.mapper.CardDataMapper;
import com.milko.payment_provider.mapper.CustomerMapper;
import com.milko.payment_provider.mapper.TransactionMapper;
import com.milko.payment_provider.model.*;
import com.milko.payment_provider.repository.AccountRepository;
import com.milko.payment_provider.repository.CardDataRepository;
import com.milko.payment_provider.repository.CustomerRepository;
import com.milko.payment_provider.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.client.WebClient;
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
    private final AccountRepository accountRepository;
    private final CardDataMapper cardDataMapper;
    private final CustomerMapper customerMapper;
    private final TransactionMapper transactionMapper;

    public Mono<TransactionDto> createTransaction(TransactionDto dto, Integer merchantId, TransactionType type){
        log.info("IN TransactionServiceImpl createTransaction. Type: {}, merchantId: {}, amount: {}", type, merchantId, dto.getAmount());
        CardData cardData = cardDataMapper.map(dto.getCardData());
        Customer customer = customerMapper.map(dto.getCustomer());
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        dto.setType(type);
        dto.setStatus(TransactionStatus.IN_PROGRESS);

        Transaction transaction  = transactionMapper.map(dto);
        transaction.setMerchantId(merchantId);

        UUID uuid = UUID.randomUUID();
        dto.setId(uuid);

        String notificationUrl = dto.getNotificationUrl();
        sendWebHook(notificationUrl, dto);

        TransactionalOperator operator = TransactionalOperator.create(transactionManager);
        return operator.transactional(
                Mono.zip(
                        cardRepository.save(cardData),
                        customerRepository.save(customer)
                )
                .flatMap(tuple -> {
                    Integer savedCardId = tuple.getT1().getId();
                    Integer savedCustomerId = tuple.getT2().getId();
                    transaction.setCardDataId(savedCardId);
                    transaction.setCustomerId(savedCustomerId);
                    transaction.setId(uuid);
                    transaction.setStatus(TransactionStatus.SUCCESS);

                    if (type.equals(TransactionType.TRANSACTION)){
                        return accountRepository.topUpAccount(merchantId, dto.getCurrency(), dto.getAmount());
                    } else {
                        return accountRepository.payoutAccount(merchantId, dto.getCurrency(), dto.getAmount());
                    }

                })
                .then(transactionRepository.save(transaction))
                .map(transactionMapper::map)
                .doOnSuccess(unused -> {
                    sendWebHook(notificationUrl, dto);
                }).doOnError(throwable -> {
                    dto.setStatus(TransactionStatus.FAILED);
                    sendWebHook(notificationUrl, dto);
                }));
    }

    public Flux<TransactionDto> getAllTransactions(Long startDate, Long endDate, Integer merchantId, TransactionType type){
        log.info("IN TransactionServiceImpl getAllTransactions. Type: {}, merchantId: {}, startDate: {}, endDate: {}", type, merchantId, startDate, endDate);
        LocalDate start = startDate != null ?
                Instant.ofEpochSecond(startDate).atZone(ZoneId.systemDefault()).toLocalDate() : null;

        LocalDate end = endDate != null ?
                Instant.ofEpochSecond(endDate).atZone(ZoneId.systemDefault()).toLocalDate() : null;

        if (start != null && end != null){
            return transactionRepository.getAllBetweenDates(start, end, merchantId, type)
                    .flatMap(this::mapTransactionToDto);
        } else if (start != null){
            return transactionRepository.getAllFromDate(start, merchantId, type)
                    .flatMap(this::mapTransactionToDto);
        } else if (end != null){
            return transactionRepository.getAllBeforeDay(end, merchantId, type)
                    .flatMap(this::mapTransactionToDto);
        } else {
            return transactionRepository.getAllForToday(merchantId, type)
                    .flatMap(this::mapTransactionToDto);
        }
    }

    public Mono<TransactionDto> findTransactionById(UUID id, Integer merchantId, TransactionType type) {
        log.info("IN TransactionServiceImpl findTransactionById. Type: {}, merchantId: {}, UUID: {}", type, merchantId, id);
        return transactionRepository.findByIdAndMerchantIdAndType(id, merchantId, type)
                .flatMap(this::mapTransactionToDto);
    }

    private void sendWebHook(String url, TransactionDto body){
        WebClient webClient = WebClient.create(url);

        webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(body), Transaction.class)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }

    private Mono<TransactionDto> mapTransactionToDto(Transaction transaction) {
        return Mono.zip(
                        Mono.just(transaction),
                        cardRepository.findById(transaction.getCardDataId()).map(cardDataMapper::map),
                        customerRepository.findById(transaction.getCustomerId()).map(customerMapper::map)
                )
                .map(tuple -> {
                    Transaction trans = tuple.getT1();
                    CardDataDto cardDataDto = tuple.getT2();
                    CustomerDto customerDto = tuple.getT3();
                    return transactionMapper.map(trans, cardDataDto, customerDto);
                });
    }

}
