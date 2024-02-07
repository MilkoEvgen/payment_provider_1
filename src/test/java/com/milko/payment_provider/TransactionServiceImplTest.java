//package com.milko.payment_provider;
//
//import com.milko.payment_provider.dto.CardDataDto;
//import com.milko.payment_provider.dto.CustomerDto;
//import com.milko.payment_provider.dto.TransactionDto;
//import com.milko.payment_provider.mapper.CardDataMapper;
//import com.milko.payment_provider.mapper.CustomerMapper;
//import com.milko.payment_provider.mapper.TransactionMapper;
//import com.milko.payment_provider.model.*;
//import com.milko.payment_provider.repository.WalletRepository;
//import com.milko.payment_provider.repository.CardDataRepository;
//import com.milko.payment_provider.repository.CustomerRepository;
//import com.milko.payment_provider.repository.TransactionRepository;
//import com.milko.payment_provider.service.TransactionServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.transaction.ReactiveTransaction;
//import org.springframework.transaction.ReactiveTransactionManager;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//
//@ExtendWith(MockitoExtension.class)
//public class TransactionServiceImplTest {
//    @Mock
//    private ReactiveTransactionManager transactionManager;
//    @Mock
//    private CardDataRepository cardRepository;
//    @Mock
//    private CustomerRepository customerRepository;
//    @Mock
//    private TransactionRepository transactionRepository;
//    @Mock
//    private WalletRepository walletRepository;
//    @Mock
//    private CardDataMapper cardDataMapper;
//    @Mock
//    private CustomerMapper customerMapper;
//    @Mock
//    private TransactionMapper transactionMapper;
//    @InjectMocks
//    private TransactionServiceImpl transactionService;
//    private TransactionDto transactionDto;
//    private Transaction transaction;
//    private CardDataDto cardDataDto;
//    private CardData cardData;
//    private CustomerDto customerDto;
//    private Customer customer;
//
//    @BeforeEach
//    public void init(){
//        cardDataDto = CardDataDto.builder()
//                .cardNumber("4102778822334893")
//                .expDate("11/23")
//                .cvv("566")
//                .build();
//        cardData = CardData.builder()
//                .id(1)
//                .cardNumber("4102778822334893")
//                .expDate("11/23")
//                .cvv("566")
//                .build();
//        customerDto = CustomerDto.builder()
//                .firstName("John")
//                .lastName("Doe")
//                .country("BR")
//                .build();
//        customer = Customer.builder()
//                .id(1)
//                .firstName("John")
//                .lastName("Doe")
//                .country("BR")
//                .build();
//        transactionDto = TransactionDto.builder()
//                .paymentMethod("CARD")
//                .amount(10)
//                .currency("USD")
//                .cardData(cardDataDto)
//                .language("en")
//                .notificationUrl("https://proselyte.net/webhook/transaction")
//                .customer(customerDto)
//                .build();
//        transaction = Transaction.builder()
//                .paymentMethod("CARD")
//                .amount(10)
//                .currency("USD")
//                .cardDataId(cardData.getId())
//                .language("en")
//                .notificationUrl("https://proselyte.net/webhook/transaction")
//                .customerId(customer.getId())
//                .build();
//    }
//
//    @Test
//    @DisplayName("createTransaction() should return TransactionDto")
//    public void createTransactionShouldReturnTransactionDto(){
//        transactionDto.setType(TransactionType.TRANSACTION);
//        transaction.setType(TransactionType.TRANSACTION);
//        ReactiveTransaction mockTransaction = Mockito.mock(ReactiveTransaction.class);
//        Mockito.when(transactionManager.getReactiveTransaction(any())).thenReturn(Mono.just(mockTransaction));
//        Mockito.when(transactionManager.commit(mockTransaction)).thenReturn(Mono.empty());
//        Mockito.when(cardDataMapper.map(any(CardDataDto.class))).thenReturn(cardData);
//        Mockito.when(customerMapper.map(any(CustomerDto.class))).thenReturn(customer);
//        Mockito.when(transactionMapper.map(any(TransactionDto.class))).thenReturn(transaction);
//        Mockito.when(cardRepository.save(any(CardData.class))).thenReturn(Mono.just(cardData));
//        Mockito.when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(customer));
//        Mockito.when(walletRepository.topUpWallet(any(), any(), any())).thenReturn(Mono.empty());
//        Mockito.when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(transaction));
//        Mockito.when(transactionMapper.map(any(Transaction.class))).thenReturn(transactionDto);
//
//        Mono<TransactionDto> result = transactionService.createTransaction(transactionDto, 1, TransactionType.TRANSACTION);
//
//        StepVerifier.create(result)
//                .expectNextMatches(resultDto -> {
//                    return resultDto.getPaymentMethod().equals("CARD") &&
//                            resultDto.getAmount() == 10 &&
//                            resultDto.getCurrency().equals("USD") &&
//                            resultDto.getCardData().getCardNumber().equals("4102778822334893") &&
//                            resultDto.getCardData().getExpDate().equals("11/23") &&
//                            resultDto.getCardData().getCvv().equals("566") &&
//                            resultDto.getLanguage().equals("en") &&
//                            resultDto.getNotificationUrl().equals("https://proselyte.net/webhook/transaction") &&
//                            resultDto.getCustomer().getFirstName().equals("John") &&
//                            resultDto.getCustomer().getLastName().equals("Doe") &&
//                            resultDto.getCustomer().getCountry().equals("BR") &&
//                            resultDto.getType().equals(TransactionType.TRANSACTION);
//                }).expectComplete()
//                .verify();
//        Mockito.verify(cardDataMapper).map(any(CardDataDto.class));
//        Mockito.verify(customerMapper).map(any(CustomerDto.class));
//        Mockito.verify(transactionMapper).map(any(TransactionDto.class));
//        Mockito.verify(cardRepository).save(any(CardData.class));
//        Mockito.verify(customerRepository).save(any(Customer.class));
//        Mockito.verify(walletRepository).topUpWallet(any(), any(), any());
//        Mockito.verify(transactionRepository).save(any(Transaction.class));
//        Mockito.verify(transactionMapper).map(any(Transaction.class));
//        Mockito.verify(transactionManager).getReactiveTransaction(any());
//        Mockito.verify(transactionManager).commit(any());
//    }
//
//    @Test
//    @DisplayName("createTransaction() should rollback transaction and throw exception")
//    public void createTransactionShouldThrowException(){
//        transactionDto.setType(TransactionType.TRANSACTION);
//        transaction.setType(TransactionType.TRANSACTION);
//        ReactiveTransaction mockTransaction = Mockito.mock(ReactiveTransaction.class);
//        Mockito.when(transactionManager.getReactiveTransaction(any())).thenReturn(Mono.just(mockTransaction));
//        Mockito.when(transactionManager.rollback(mockTransaction)).thenReturn(Mono.empty());
//        Mockito.when(cardDataMapper.map(any(CardDataDto.class))).thenReturn(cardData);
//        Mockito.when(customerMapper.map(any(CustomerDto.class))).thenReturn(customer);
//        Mockito.when(transactionMapper.map(any(TransactionDto.class))).thenReturn(transaction);
//        Mockito.when(cardRepository.save(any(CardData.class))).thenReturn(Mono.just(cardData));
//        Mockito.when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(customer));
//        Mockito.when(walletRepository.topUpWallet(any(), any(), any())).thenReturn(Mono.error(new Exception()));
//        Mockito.when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(transaction));
//
//        Mono<TransactionDto> result = transactionService.createTransaction(transactionDto, 1, TransactionType.TRANSACTION);
//
//        StepVerifier.create(result)
//                .expectError()
//                .verify();
//        Mockito.verify(cardDataMapper).map(any(CardDataDto.class));
//        Mockito.verify(customerMapper).map(any(CustomerDto.class));
//        Mockito.verify(transactionMapper).map(any(TransactionDto.class));
//        Mockito.verify(cardRepository).save(any(CardData.class));
//        Mockito.verify(customerRepository).save(any(Customer.class));
//        Mockito.verify(walletRepository).topUpWallet(any(), any(), any());
//        Mockito.verify(transactionRepository).save(any(Transaction.class));
//        Mockito.verify(transactionManager).getReactiveTransaction(any());
//        Mockito.verify(transactionManager).rollback(any());
//    }
//
//    @Test
//    @DisplayName("getAllTransactions() should return list of TransactionDto for today if startDate and andDate empty")
//    public void getAllTransactionsShouldReturnTransactionDtoIfDatesEmpty(){
//        transactionDto.setType(TransactionType.TRANSACTION);
//        Mockito.when(transactionRepository.getAllForToday(any(), any())).thenReturn(Flux.just(transaction));
//        Mockito.when(cardRepository.findById(any(Integer.class))).thenReturn(Mono.just(cardData));
//        Mockito.when(cardDataMapper.map(any(CardData.class))).thenReturn(cardDataDto);
//        Mockito.when(customerRepository.findById(any(Integer.class))).thenReturn(Mono.just(customer));
//        Mockito.when(customerMapper.map(any(Customer.class))).thenReturn(customerDto);
//        Mockito.when(transactionMapper.map(any(Transaction.class), any(CardDataDto.class), any(CustomerDto.class))).thenReturn(transactionDto);
//
//        Flux<TransactionDto> result = transactionService.getAllTransactions(null, null, 1, TransactionType.TRANSACTION);
//
//        StepVerifier.create(result)
//                .expectNextMatches(resultDto -> {
//                    return resultDto.getPaymentMethod().equals("CARD") &&
//                            resultDto.getAmount() == 10 &&
//                            resultDto.getCurrency().equals("USD") &&
//                            resultDto.getCardData().getCardNumber().equals("4102778822334893") &&
//                            resultDto.getCardData().getExpDate().equals("11/23") &&
//                            resultDto.getCardData().getCvv().equals("566") &&
//                            resultDto.getLanguage().equals("en") &&
//                            resultDto.getNotificationUrl().equals("https://proselyte.net/webhook/transaction") &&
//                            resultDto.getCustomer().getFirstName().equals("John") &&
//                            resultDto.getCustomer().getLastName().equals("Doe") &&
//                            resultDto.getCustomer().getCountry().equals("BR") &&
//                            resultDto.getType().equals(TransactionType.TRANSACTION);
//                }).expectComplete()
//                .verify();
//        Mockito.verify(transactionRepository).getAllForToday(any(), any());
//        Mockito.verify(cardRepository).findById(any(Integer.class));
//        Mockito.verify(cardDataMapper).map(any(CardData.class));
//        Mockito.verify(customerRepository).findById(any(Integer.class));
//        Mockito.verify(customerMapper).map(any(Customer.class));
//        Mockito.verify(transactionMapper).map(any(Transaction.class), any(CardDataDto.class), any(CustomerDto.class));
//    }
//
//    @Test
//    @DisplayName("getAllTransactions() should return list of TransactionDto between dates")
//    public void getAllTransactionsShouldReturnTransactionDtoBetweenDates(){
//        transactionDto.setType(TransactionType.TRANSACTION);
//        Mockito.when(transactionRepository.getAllBetweenDates(any(), any(), any(), any())).thenReturn(Flux.just(transaction));
//        Mockito.when(cardRepository.findById(any(Integer.class))).thenReturn(Mono.just(cardData));
//        Mockito.when(cardDataMapper.map(any(CardData.class))).thenReturn(cardDataDto);
//        Mockito.when(customerRepository.findById(any(Integer.class))).thenReturn(Mono.just(customer));
//        Mockito.when(customerMapper.map(any(Customer.class))).thenReturn(customerDto);
//        Mockito.when(transactionMapper.map(any(Transaction.class), any(CardDataDto.class), any(CustomerDto.class))).thenReturn(transactionDto);
//
//        Flux<TransactionDto> result = transactionService.getAllTransactions(1706227199L, 171627199L, 1, TransactionType.TRANSACTION);
//
//        StepVerifier.create(result)
//                .expectNextMatches(resultDto -> {
//                    return resultDto.getPaymentMethod().equals("CARD") &&
//                            resultDto.getAmount() == 10 &&
//                            resultDto.getCurrency().equals("USD") &&
//                            resultDto.getCardData().getCardNumber().equals("4102778822334893") &&
//                            resultDto.getCardData().getExpDate().equals("11/23") &&
//                            resultDto.getCardData().getCvv().equals("566") &&
//                            resultDto.getLanguage().equals("en") &&
//                            resultDto.getNotificationUrl().equals("https://proselyte.net/webhook/transaction") &&
//                            resultDto.getCustomer().getFirstName().equals("John") &&
//                            resultDto.getCustomer().getLastName().equals("Doe") &&
//                            resultDto.getCustomer().getCountry().equals("BR") &&
//                            resultDto.getType().equals(TransactionType.TRANSACTION);
//                }).expectComplete()
//                .verify();
//        Mockito.verify(transactionRepository).getAllBetweenDates(any(), any(), any(), any());
//        Mockito.verify(cardRepository).findById(any(Integer.class));
//        Mockito.verify(cardDataMapper).map(any(CardData.class));
//        Mockito.verify(customerRepository).findById(any(Integer.class));
//        Mockito.verify(customerMapper).map(any(Customer.class));
//        Mockito.verify(transactionMapper).map(any(Transaction.class), any(CardDataDto.class), any(CustomerDto.class));
//    }
//
//    @Test
//    @DisplayName("findTransactionById() should return TransactionDto")
//    public void findTransactionByIdShouldReturnTransactionDto(){
//        transactionDto.setType(TransactionType.TRANSACTION);
//        Mockito.when(transactionRepository.findByIdAndMerchantIdAndType(any(), any(), any())).thenReturn(Mono.just(transaction));
//        Mockito.when(cardRepository.findById(any(Integer.class))).thenReturn(Mono.just(cardData));
//        Mockito.when(cardDataMapper.map(any(CardData.class))).thenReturn(cardDataDto);
//        Mockito.when(customerRepository.findById(any(Integer.class))).thenReturn(Mono.just(customer));
//        Mockito.when(customerMapper.map(any(Customer.class))).thenReturn(customerDto);
//        Mockito.when(transactionMapper.map(any(Transaction.class), any(CardDataDto.class), any(CustomerDto.class))).thenReturn(transactionDto);
//
//        Mono<TransactionDto> result = transactionService.findTransactionById(UUID.fromString("087d9b6e-494e-42a9-b932-1969212d620a"), 1, TransactionType.TRANSACTION);
//
//        StepVerifier.create(result)
//                .expectNextMatches(resultDto -> {
//                    return resultDto.getPaymentMethod().equals("CARD") &&
//                            resultDto.getAmount() == 10 &&
//                            resultDto.getCurrency().equals("USD") &&
//                            resultDto.getCardData().getCardNumber().equals("4102778822334893") &&
//                            resultDto.getCardData().getExpDate().equals("11/23") &&
//                            resultDto.getCardData().getCvv().equals("566") &&
//                            resultDto.getLanguage().equals("en") &&
//                            resultDto.getNotificationUrl().equals("https://proselyte.net/webhook/transaction") &&
//                            resultDto.getCustomer().getFirstName().equals("John") &&
//                            resultDto.getCustomer().getLastName().equals("Doe") &&
//                            resultDto.getCustomer().getCountry().equals("BR") &&
//                            resultDto.getType().equals(TransactionType.TRANSACTION);
//                }).expectComplete()
//                .verify();
//        Mockito.verify(transactionRepository).findByIdAndMerchantIdAndType(any(), any(), any());
//        Mockito.verify(cardRepository).findById(any(Integer.class));
//        Mockito.verify(cardDataMapper).map(any(CardData.class));
//        Mockito.verify(customerRepository).findById(any(Integer.class));
//        Mockito.verify(customerMapper).map(any(Customer.class));
//        Mockito.verify(transactionMapper).map(any(Transaction.class), any(CardDataDto.class), any(CustomerDto.class));
//    }
//
//    @Test
//    @DisplayName("findTransactionById() should return empty Mono if transaction not exist")
//    public void findTransactionByIdShouldReturnEmptyMonoIfTransactionNotExist(){
//        transactionDto.setType(TransactionType.TRANSACTION);
//        Mockito.when(transactionRepository.findByIdAndMerchantIdAndType(any(), any(), any())).thenReturn(Mono.empty());
//
//        Mono<TransactionDto> result = transactionService.findTransactionById(UUID.fromString("087d9b6e-494e-42a9-b932-1969212d620a"), 1, TransactionType.TRANSACTION);
//
//        StepVerifier.create(result)
//               .expectComplete()
//                .verify();
//        Mockito.verify(transactionRepository).findByIdAndMerchantIdAndType(any(), any(), any());
//    }
//}
