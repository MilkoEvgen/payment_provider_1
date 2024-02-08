package com.milko.payment_provider.testcontainers;

import com.milko.payment_provider.dto.CardDataDto;
import com.milko.payment_provider.dto.CustomerDto;
import com.milko.payment_provider.dto.TransactionDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class TransactionRestControllerV1Test{

    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> String.format("r2dbc:postgresql://%s:%d/%s",
                postgreSQLContainer.getHost(),
                postgreSQLContainer.getFirstMappedPort(),
                postgreSQLContainer.getDatabaseName()));
        registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
        registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);

        registry.add("spring.flyway.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.flyway.username", postgreSQLContainer::getUsername);
        registry.add("spring.flyway.password", postgreSQLContainer::getPassword);
    }

    private final String AUTH_HEADER = "Basic " + Base64.getEncoder().encodeToString("c63804ed-a4bc-4ec5-a540-77de60809c2b:00000".getBytes());

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String url;
    private TransactionDto transactionRequest;
    private CardDataDto cardDataDto;
    private CustomerDto customerDto;


    @BeforeEach
    public void init(){
        url = "http://localhost:" + port + "/api/v1/payments";
        cardDataDto = CardDataDto.builder()
                .cardNumber("4102778822334893")
                .expDate("11/23")
                .cvv("566")
                .build();
        customerDto = CustomerDto.builder()
                .firstName("John")
                .lastName("Doe")
                .country("BR")
                .build();
        transactionRequest = TransactionDto.builder()
                .paymentMethod("CARD")
                .amount(1000)
                .currency("USD")
                .cardData(cardDataDto)
                .language("en")
                .notificationUrl("https://proselyte.net/webhook/transaction")
                .customer(customerDto)
                .build();
    }

    @AfterEach
    public void cleanDatabase(){
        jdbcTemplate.update("DELETE FROM transactions");
        jdbcTemplate.update("DELETE FROM customers");
    }

    @Test
    @DisplayName("createTransaction should return ResponseEntity if transaction created successfully")
    void createTransactionShouldReturnResponseEntity() {

        webTestClient.post().uri(url + "/transaction")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.transaction_id").exists()
                .jsonPath("$.status").isEqualTo("IN_PROGRESS")
                .jsonPath("$.message").isEqualTo("OK");
    }

    @Test
    @DisplayName("createPayout should return ResponseEntity if payout created successfully")
    void createPayoutShouldReturnResponseEntity() {

        webTestClient.post().uri(url + "/payout")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.transaction_id").exists()
                .jsonPath("$.status").isEqualTo("IN_PROGRESS")
                .jsonPath("$.message").isEqualTo("OK");
    }

    @Test
    @DisplayName("getAllTransactions should return List of TransactionDto for today if dates equals null")
    void getAllTransactionsShouldReturnListOfTransactionDto() {
        webTestClient.post().uri(url + "/transaction")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionRequest)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get().uri(url + "/transaction/list")
                .header("Authorization", AUTH_HEADER)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").exists()
                .jsonPath("$[0].payment_method").isEqualTo("CARD")
                .jsonPath("$[0].amount").isEqualTo(1000)
                .jsonPath("$[0].currency").isEqualTo("USD")
                .jsonPath("$[0].type").isEqualTo("TRANSACTION")
                .jsonPath("$[0].language").isEqualTo("en")
                .jsonPath("$[0].notification_url").isEqualTo("https://proselyte.net/webhook/transaction")
                .jsonPath("$[0].customer.first_name").isEqualTo("John")
                .jsonPath("$[0].customer.last_name").isEqualTo("Doe")
                .jsonPath("$[0].customer.country").isEqualTo("BR")
                .jsonPath("$[0].status").isEqualTo("IN_PROGRESS")
                .jsonPath("$.length()").isEqualTo(1);
    }

    @Test
    @DisplayName("getAllPayouts should return List of TransactionDto for today if dates equals null")
    void getAllPayoutsShouldReturnListOfTransactionDto() {
        webTestClient.post().uri(url + "/payout")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionRequest)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get().uri(url + "/payout/list")
                .header("Authorization", AUTH_HEADER)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").exists()
                .jsonPath("$[0].payment_method").isEqualTo("CARD")
                .jsonPath("$[0].amount").isEqualTo(1000)
                .jsonPath("$[0].currency").isEqualTo("USD")
                .jsonPath("$[0].type").isEqualTo("PAYOUT")
                .jsonPath("$[0].language").isEqualTo("en")
                .jsonPath("$[0].notification_url").isEqualTo("https://proselyte.net/webhook/transaction")
                .jsonPath("$[0].customer.first_name").isEqualTo("John")
                .jsonPath("$[0].customer.last_name").isEqualTo("Doe")
                .jsonPath("$[0].customer.country").isEqualTo("BR")
                .jsonPath("$[0].status").isEqualTo("IN_PROGRESS")
                .jsonPath("$.length()").isEqualTo(1);
    }

    @Test
    @DisplayName("getTransactionById should return TransactionDto")
    void getTransactionByIdShouldReturnTransactionDto() {
        Map<String, String> transactionId = new HashMap<>();

        webTestClient.post().uri(url + "/transaction")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.transaction_id")
                .value(id -> transactionId.put("id", (String) id));

        webTestClient.get().uri(url + "/transaction/" + transactionId.get("id") + "/details")
                .header("Authorization", AUTH_HEADER)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.payment_method").isEqualTo("CARD")
                .jsonPath("$.amount").isEqualTo(1000)
                .jsonPath("$.currency").isEqualTo("USD")
                .jsonPath("$.type").isEqualTo("TRANSACTION")
                .jsonPath("$.language").isEqualTo("en")
                .jsonPath("$.notification_url").isEqualTo("https://proselyte.net/webhook/transaction")
                .jsonPath("$.customer.first_name").isEqualTo("John")
                .jsonPath("$.customer.last_name").isEqualTo("Doe")
                .jsonPath("$.customer.country").isEqualTo("BR")
                .jsonPath("$.status").isEqualTo("IN_PROGRESS");
    }

    @Test
    @DisplayName("getPayOutById should return TransactionDto")
    void getPayOutByIdShouldReturnTransactionDto() {
        Map<String, String> transactionId = new HashMap<>();

        webTestClient.post().uri(url + "/payout")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.transaction_id")
                .value(id -> transactionId.put("id", (String) id));

        webTestClient.get().uri(url + "/payout/" + transactionId.get("id") + "/details")
                .header("Authorization", AUTH_HEADER)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.payment_method").isEqualTo("CARD")
                .jsonPath("$.amount").isEqualTo(1000)
                .jsonPath("$.currency").isEqualTo("USD")
                .jsonPath("$.type").isEqualTo("PAYOUT")
                .jsonPath("$.language").isEqualTo("en")
                .jsonPath("$.notification_url").isEqualTo("https://proselyte.net/webhook/transaction")
                .jsonPath("$.customer.first_name").isEqualTo("John")
                .jsonPath("$.customer.last_name").isEqualTo("Doe")
                .jsonPath("$.customer.country").isEqualTo("BR")
                .jsonPath("$.status").isEqualTo("IN_PROGRESS");
    }
}
