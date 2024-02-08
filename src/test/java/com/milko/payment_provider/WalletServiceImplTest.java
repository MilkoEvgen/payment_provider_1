package com.milko.payment_provider;

import com.milko.payment_provider.dto.WalletDto;
import com.milko.payment_provider.mapper.WalletMapper;
import com.milko.payment_provider.model.Wallet;
import com.milko.payment_provider.repository.WalletRepository;
import com.milko.payment_provider.service.WalletServiceImpl;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class WalletServiceImplTest {
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private WalletMapper walletMapper;
    @InjectMocks
    private WalletServiceImpl accountService;

    private Wallet wallet;
    private WalletDto walletDto;
    private UUID uuid;
    private UUID merchantId;

    @BeforeEach
    public void init(){
        uuid = UUID.fromString("4858f92e-de50-4c67-82d7-d1871efbd22d");
        merchantId = UUID.fromString("0e387cb0-ce64-4ec2-9a5a-9d85b7db33f5");
        wallet = Wallet.builder()
                .id(uuid)
                .amount(100)
                .currency("USD")
                .merchantId(merchantId)
                .build();
        walletDto = WalletDto.builder()
                .id(uuid)
                .amount(100)
                .currency("USD")
                .build();
    }

    @Test
    @DisplayName("getAccountByCurrency should return account")
    public void getAccountByCurrencyShouldReturnAccount(){
        Mockito.when(walletRepository.getFirstByCurrencyAndMerchantId(any(), any())).thenReturn(Mono.just(wallet));
        Mockito.when(walletMapper.map(any(Wallet.class))).thenReturn(walletDto);

        Mono<WalletDto> result = accountService.getWalletByCurrency("USD", merchantId);

        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(uuid) &&
                            resultDto.getAmount() == 100 &&
                            resultDto.getCurrency().equals("USD");
                }).expectComplete()
                .verify();
    }

    @Test
    @DisplayName("getAccountByCurrency should return empty Mono if Account not exists")
    public void getAccountByCurrencyShouldReturnEmptyMono(){
        Mockito.when(walletRepository.getFirstByCurrencyAndMerchantId(any(), any())).thenReturn(Mono.empty());

        Mono<WalletDto> result = accountService.getWalletByCurrency("USD", merchantId);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("getAllAccountsByMerchantId should return Flux of AccountDto")
    public void getAllAccountsByMerchantIdShouldReturnFlux(){
        Mockito.when(walletRepository.getAllByMerchantId(any())).thenReturn(Flux.just(wallet));
        Mockito.when(walletMapper.map(any(Wallet.class))).thenReturn(walletDto);

        Flux<WalletDto> result = accountService.getAllWalletsByMerchantId(merchantId);

        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(uuid) &&
                            resultDto.getAmount() == 100 &&
                            resultDto.getCurrency().equals("USD");
                }).expectComplete()
                .verify();
    }

    @Test
    @DisplayName("getAllAccountsByMerchantId should return empty Flux if merchant does not have account")
    public void getAllAccountsByMerchantIdShouldReturnEmptyFlux(){
        Mockito.when(walletRepository.getAllByMerchantId(any())).thenReturn(Flux.empty());

        Flux<WalletDto> result = accountService.getAllWalletsByMerchantId(merchantId);

        StepVerifier.create(result)
               .expectComplete()
                .verify();
    }
}
