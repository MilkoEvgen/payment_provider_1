package com.milko.payment_provider.service;

import com.milko.payment_provider.dto.WalletDto;
import com.milko.payment_provider.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface WalletService {
    Mono<WalletDto> getWalletByCurrency(String currency, UUID merchantId);

    Flux<WalletDto> getAllWalletsByMerchantId(UUID merchantId);

    Mono<Void> doTransaction(Transaction transaction);
}
