package com.milko.payment_provider.service;

import com.milko.payment_provider.dto.WalletDto;
import com.milko.payment_provider.mapper.WalletMapper;
import com.milko.payment_provider.model.Transaction;
import com.milko.payment_provider.model.TransactionType;
import com.milko.payment_provider.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final ReactiveTransactionManager transactionManager;
    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    public Mono<WalletDto> getWalletByCurrency(String currency, UUID merchantId){
        log.info("IN WalletServiceImpl getWalletByCurrency. Currency: {}, merchantId: {}", currency, merchantId);
        return walletRepository.getFirstByCurrencyAndMerchantId(currency, merchantId)
                .map(walletMapper::map);
    }

    public Flux<WalletDto> getAllWalletsByMerchantId(UUID merchantId){
        log.info("IN WalletServiceImpl getAllWalletsByMerchantId(). MerchantId: {}", merchantId);
        return walletRepository.getAllByMerchantId(merchantId)
                .map(walletMapper::map);
    }

    public Mono<Void> doTransaction(Transaction transaction){
        TransactionalOperator operator = TransactionalOperator.create(transactionManager);

        return operator.transactional(
                walletRepository.getFirstByCurrencyAndMerchantId(transaction.getCurrency(), transaction.getMerchantId())
                .flatMap(wallet -> {
                    if (transaction.getType().equals(TransactionType.TRANSACTION)){
                        Integer newAmount = wallet.getAmount() + transaction.getAmount();
                        wallet.setAmount(newAmount);
                    } else {
                        int newAmount = wallet.getAmount() - transaction.getAmount();
                        if (newAmount < 0){
                            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds in wallet"));
                        } else  {
                            wallet.setAmount(newAmount);
                        }
                    }
                    return walletRepository.save(wallet).then();
                }));
    }

}
