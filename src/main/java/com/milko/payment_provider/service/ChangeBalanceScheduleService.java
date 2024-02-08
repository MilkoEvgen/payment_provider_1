package com.milko.payment_provider.service;

import com.milko.payment_provider.model.Transaction;
import com.milko.payment_provider.model.TransactionStatus;
import com.milko.payment_provider.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeBalanceScheduleService {
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final WebhookService webhookService;

    @Scheduled(fixedRate = 300000)
    public void processTransactions() {
        log.info("IN ChangeBalanceScheduleService processTransactions()");
        Flux<Transaction> transactions = transactionRepository.findAllByStatus(TransactionStatus.IN_PROGRESS);

        transactions.concatMap(transaction -> {
            if (new Random().nextDouble() < 0.7) {
                transaction.setStatus(TransactionStatus.SUCCESS);
                transaction.setMessage("OK");
                return walletService.doTransaction(transaction)
                        .onErrorResume(error -> {
                            transaction.setStatus(TransactionStatus.FAILED);
                            transaction.setMessage(error.getMessage());
                            return Mono.empty();
                        })
                        .then(transactionRepository.save(transaction))
                        .then(webhookService.sendWebHook(transaction));
            } else {
                transaction.setStatus(TransactionStatus.FAILED);
                transaction.setMessage("FAILED");
                return transactionRepository.save(transaction)
                        .then(webhookService.sendWebHook(transaction));
            }
        }).subscribe();
    }





}
