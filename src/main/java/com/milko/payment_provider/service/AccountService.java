package com.milko.payment_provider.service;

import com.milko.payment_provider.dto.AccountDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<AccountDto> getAccountByCurrency(String currency, Integer merchantId);

    Flux<AccountDto> getAllAccountsByMerchantId(Integer merchantId);
}
