package com.milko.payment_provider.testcontainers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Base64;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AccountControllerV1Test{

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

    private final String AUTH_HEADER = "Basic " + Base64.getEncoder().encodeToString("12345:12345".getBytes());

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("getAccountByCurrency should return account if it exists")
    void getAccountByCurrencyShouldReturnAccount() {

        webTestClient.get().uri("http://localhost:" + port + "/api/v1/balance/USD")
                .header("Authorization", AUTH_HEADER)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(2)
                .jsonPath("$.amount").isEqualTo(75000)
                .jsonPath("$.currency").isEqualTo("USD");
    }

    @Test
    @DisplayName("getAccountByCurrency should return empty body if it not exists")
    void getAccountByCurrencyShouldReturnEmptyBody() {

        webTestClient.get().uri("http://localhost:" + port + "/api/v1/balance/RUB")
                .header("Authorization", AUTH_HEADER)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    @DisplayName("getAllAccounts should return all accounts")
    void getAllAccountsShouldReturnAllAccounts() {

        webTestClient.get().uri("http://localhost:" + port + "/api/v1/balance/list")
                .header("Authorization", AUTH_HEADER)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].amount").isEqualTo(10000)
                .jsonPath("$[0].currency").isEqualTo("BRL")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].amount").isEqualTo(75000)
                .jsonPath("$[1].currency").isEqualTo("USD")
                .jsonPath("$.length()").isEqualTo(2);
    }


}
