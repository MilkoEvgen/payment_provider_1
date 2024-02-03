package com.milko.payment_provider.service;

import com.milko.payment_provider.dto.AccountDto;
import com.milko.payment_provider.mapper.AccountMapper;
import com.milko.payment_provider.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public Mono<AccountDto> getAccountByCurrency(String currency, Integer merchantId){
        log.info("IN AccountServiceImpl getAccountByCurrency. Currency: {}, merchantId: {}", currency, merchantId);
        return accountRepository.getFirstByCurrencyAndMerchantId(currency, merchantId)
                .map(accountMapper::map);
    }

    public Flux<AccountDto> getAllAccountsByMerchantId(Integer merchantId){
        log.info("IN AccountServiceImpl getAllAccountsByMerchantId(). MerchantId: {}", merchantId);
        return accountRepository.getAllByMerchantId(merchantId)
                .map(accountMapper::map);
    }
}
