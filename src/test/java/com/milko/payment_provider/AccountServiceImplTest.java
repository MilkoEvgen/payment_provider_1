package com.milko.payment_provider;

import com.milko.payment_provider.dto.AccountDto;
import com.milko.payment_provider.mapper.AccountMapper;
import com.milko.payment_provider.model.Account;
import com.milko.payment_provider.repository.AccountRepository;
import com.milko.payment_provider.service.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;
    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;
    private AccountDto accountDto;

    @BeforeEach
    public void init(){
        account = Account.builder()
                .id(1)
                .amount(100)
                .currency("USD")
                .merchantId(1)
                .build();
        accountDto = AccountDto.builder()
                .id(1)
                .amount(100)
                .currency("USD")
                .build();
    }

    @Test
    @DisplayName("getAccountByCurrency should return account")
    public void getAccountByCurrencyShouldReturnAccount(){
        Mockito.when(accountRepository.getFirstByCurrencyAndMerchantId(any(), any())).thenReturn(Mono.just(account));
        Mockito.when(accountMapper.map(any(Account.class))).thenReturn(accountDto);

        Mono<AccountDto> result = accountService.getAccountByCurrency("USD", 1);

        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId() == 1 &&
                            resultDto.getAmount() == 100 &&
                            resultDto.getCurrency().equals("USD");
                }).expectComplete()
                .verify();
    }

    @Test
    @DisplayName("getAccountByCurrency should return empty Mono if Account not exists")
    public void getAccountByCurrencyShouldReturnEmptyMono(){
        Mockito.when(accountRepository.getFirstByCurrencyAndMerchantId(any(), any())).thenReturn(Mono.empty());

        Mono<AccountDto> result = accountService.getAccountByCurrency("USD", 1);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("getAllAccountsByMerchantId should return Flux of AccountDto")
    public void getAllAccountsByMerchantIdShouldReturnFlux(){
        Mockito.when(accountRepository.getAllByMerchantId(any())).thenReturn(Flux.just(account));
        Mockito.when(accountMapper.map(any(Account.class))).thenReturn(accountDto);

        Flux<AccountDto> result = accountService.getAllAccountsByMerchantId(1);

        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId() == 1 &&
                            resultDto.getAmount() == 100 &&
                            resultDto.getCurrency().equals("USD");
                }).expectComplete()
                .verify();
    }

    @Test
    @DisplayName("getAllAccountsByMerchantId should return empty Flux if merchant does not have account")
    public void getAllAccountsByMerchantIdShouldReturnEmptyFlux(){
        Mockito.when(accountRepository.getAllByMerchantId(any())).thenReturn(Flux.empty());

        Flux<AccountDto> result = accountService.getAllAccountsByMerchantId(1);

        StepVerifier.create(result)
               .expectComplete()
                .verify();
    }
}
