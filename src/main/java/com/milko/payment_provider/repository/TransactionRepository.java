package com.milko.payment_provider.repository;

import com.milko.payment_provider.model.Transaction;
import com.milko.payment_provider.model.TransactionType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

public interface TransactionRepository extends R2dbcRepository<Transaction, UUID> {
    @Query("SELECT * FROM transactions WHERE merchant_id = :merchantId " +
            "AND type = :type " +
            "AND created_at::date BETWEEN :start AND :end")
    Flux<Transaction> getAllBetweenDates(LocalDate start, LocalDate end, Integer merchantId, TransactionType type);

    @Query("SELECT * FROM transactions WHERE merchant_id = :merchantId " +
            "AND type = :type " +
            "AND created_at::date >= :start")
    Flux<Transaction> getAllFromDate(LocalDate start, Integer merchantId, TransactionType type);

    @Query("SELECT * FROM transactions WHERE merchant_id = :merchantId " +
            "AND type = :type " +
            "AND created_at::date <= :end")
    Flux<Transaction> getAllBeforeDay(LocalDate end, Integer merchantId, TransactionType type);

    @Query("SELECT * FROM transactions WHERE merchant_id = :merchantId " +
            "AND type = :type " +
            "AND created_at::date = CURRENT_DATE")
    Flux<Transaction> getAllForToday(Integer merchantId, TransactionType type);

    Mono<Transaction> findByIdAndMerchantIdAndType(UUID id, Integer merchantId, TransactionType type);
}
