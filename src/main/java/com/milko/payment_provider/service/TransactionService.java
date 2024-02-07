package com.milko.payment_provider.service;

import com.milko.payment_provider.dto.TransactionDto;
import com.milko.payment_provider.model.TransactionType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TransactionService {
    Mono<TransactionDto> createTransaction(TransactionDto dto, UUID merchantId, TransactionType type);

    Flux<TransactionDto> getAllTransactions(Long startDate, Long endDate, UUID merchantId, TransactionType type);

    Mono<TransactionDto> findTransactionById(UUID id, UUID merchantId, TransactionType type);
}
