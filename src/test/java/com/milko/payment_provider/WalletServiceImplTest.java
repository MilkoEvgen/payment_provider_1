//package com.milko.payment_provider;
//
//import com.milko.payment_provider.dto.WalletDto;
//import com.milko.payment_provider.mapper.WalletMapper;
//import com.milko.payment_provider.model.Wallet;
//import com.milko.payment_provider.repository.WalletRepository;
//import com.milko.payment_provider.service.WalletServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import static org.mockito.ArgumentMatchers.any;
//
//@ExtendWith(MockitoExtension.class)
//public class WalletServiceImplTest {
//    @Mock
//    private WalletRepository walletRepository;
//    @Mock
//    private WalletMapper walletMapper;
//    @InjectMocks
//    private WalletServiceImpl accountService;
//
//    private Wallet wallet;
//    private WalletDto walletDto;
//
//    @BeforeEach
//    public void init(){
//        wallet = Wallet.builder()
//                .id(1)
//                .amount(100)
//                .currency("USD")
//                .merchantId(1)
//                .build();
//        walletDto = WalletDto.builder()
//                .id(1)
//                .amount(100)
//                .currency("USD")
//                .build();
//    }
//
//    @Test
//    @DisplayName("getAccountByCurrency should return account")
//    public void getAccountByCurrencyShouldReturnAccount(){
//        Mockito.when(walletRepository.getFirstByCurrencyAndMerchantId(any(), any())).thenReturn(Mono.just(wallet));
//        Mockito.when(walletMapper.map(any(Wallet.class))).thenReturn(walletDto);
//
//        Mono<WalletDto> result = accountService.getWalletByCurrency("USD", 1);
//
//        StepVerifier.create(result)
//                .expectNextMatches(resultDto -> {
//                    return resultDto.getId() == 1 &&
//                            resultDto.getAmount() == 100 &&
//                            resultDto.getCurrency().equals("USD");
//                }).expectComplete()
//                .verify();
//    }
//
//    @Test
//    @DisplayName("getAccountByCurrency should return empty Mono if Account not exists")
//    public void getAccountByCurrencyShouldReturnEmptyMono(){
//        Mockito.when(walletRepository.getFirstByCurrencyAndMerchantId(any(), any())).thenReturn(Mono.empty());
//
//        Mono<WalletDto> result = accountService.getWalletByCurrency("USD", 1);
//
//        StepVerifier.create(result)
//                .expectComplete()
//                .verify();
//    }
//
//    @Test
//    @DisplayName("getAllAccountsByMerchantId should return Flux of AccountDto")
//    public void getAllAccountsByMerchantIdShouldReturnFlux(){
//        Mockito.when(walletRepository.getAllByMerchantId(any())).thenReturn(Flux.just(wallet));
//        Mockito.when(walletMapper.map(any(Wallet.class))).thenReturn(walletDto);
//
//        Flux<WalletDto> result = accountService.getAllWalletsByMerchantId(1);
//
//        StepVerifier.create(result)
//                .expectNextMatches(resultDto -> {
//                    return resultDto.getId() == 1 &&
//                            resultDto.getAmount() == 100 &&
//                            resultDto.getCurrency().equals("USD");
//                }).expectComplete()
//                .verify();
//    }
//
//    @Test
//    @DisplayName("getAllAccountsByMerchantId should return empty Flux if merchant does not have account")
//    public void getAllAccountsByMerchantIdShouldReturnEmptyFlux(){
//        Mockito.when(walletRepository.getAllByMerchantId(any())).thenReturn(Flux.empty());
//
//        Flux<WalletDto> result = accountService.getAllWalletsByMerchantId(1);
//
//        StepVerifier.create(result)
//               .expectComplete()
//                .verify();
//    }
//}
