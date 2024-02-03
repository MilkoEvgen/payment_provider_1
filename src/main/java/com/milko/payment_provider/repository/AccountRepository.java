package com.milko.payment_provider.repository;

import com.milko.payment_provider.model.Account;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository extends R2dbcRepository<Account, Integer> {

    @Modifying
    @Query("UPDATE accounts SET amount = amount + :amount " +
            "WHERE merchant_id = :merchantId AND currency = :currency")
    Mono<Void> topUpAccount(Integer merchantId, String currency, Integer amount);

    @Modifying
    @Query("UPDATE accounts SET amount = amount - :amount " +
            "WHERE merchant_id = :merchantId AND currency = :currency")
    Mono<Void> payoutAccount(Integer merchantId, String currency, Integer amount);

    Mono<Account> getFirstByCurrencyAndMerchantId(String currency, Integer merchantId);

    Flux<Account> getAllByMerchantId(Integer merchantId);
}
