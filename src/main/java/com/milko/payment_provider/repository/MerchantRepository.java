package com.milko.payment_provider.repository;

import com.milko.payment_provider.model.Merchant;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface MerchantRepository extends R2dbcRepository<Merchant, Integer> {
    Mono<Merchant> findByUsername(String username);
}
