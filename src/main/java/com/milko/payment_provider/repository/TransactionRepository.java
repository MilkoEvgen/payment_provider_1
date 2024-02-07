package com.milko.payment_provider.repository;

import com.milko.payment_provider.model.Transaction;
import com.milko.payment_provider.model.TransactionStatus;
import com.milko.payment_provider.model.TransactionType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

public interface TransactionRepository extends R2dbcRepository<Transaction, UUID> {

    Flux<Transaction> findAllByStatus(TransactionStatus status);

    @Query("SELECT * FROM transactions WHERE merchant_id = :merchantId " +
            "AND type = :type " +
            "AND created_at::date BETWEEN :start AND :end")
    Flux<Transaction> getAllBetweenDates(LocalDate start, LocalDate end, UUID merchantId, TransactionType type);

    @Query("SELECT * FROM transactions WHERE merchant_id = :merchantId " +
            "AND type = :type " +
            "AND created_at::date >= :start")
    Flux<Transaction> getAllFromDate(LocalDate start, UUID merchantId, TransactionType type);

    @Query("SELECT * FROM transactions WHERE merchant_id = :merchantId " +
            "AND type = :type " +
            "AND created_at::date <= :end")
    Flux<Transaction> getAllBeforeDay(LocalDate end, UUID merchantId, TransactionType type);

    @Query("SELECT * FROM transactions WHERE merchant_id = :merchantId " +
            "AND type = :type " +
            "AND created_at::date = CURRENT_DATE")
    Flux<Transaction> getAllForToday(UUID merchantId, TransactionType type);

    Mono<Transaction> findByIdAndMerchantIdAndType(UUID id, UUID merchantId, TransactionType type);
}
