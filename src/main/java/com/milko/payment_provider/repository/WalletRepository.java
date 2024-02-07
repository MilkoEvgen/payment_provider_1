package com.milko.payment_provider.repository;

import com.milko.payment_provider.model.Wallet;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface WalletRepository extends R2dbcRepository<Wallet, UUID> {

    Mono<Wallet> getFirstByCurrencyAndMerchantId(String currency, UUID merchantId);

    Flux<Wallet> getAllByMerchantId(UUID merchantId);
}
