package com.milko.payment_provider.repository;

import com.milko.payment_provider.model.Customer;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerRepository extends R2dbcRepository<Customer, UUID> {

    @Query("SELECT * FROM customers " +
            "INNER JOIN card_data cd ON customers.id = cd.customer_id " +
            "WHERE customers.first_name = :firstName " +
            "AND customers.last_name = :lastName " +
            "AND cd.card_number = :cardNumber " +
            "LIMIT 1")
    Mono<Customer> getByNameAndCardNumber(@Param("firstName") String firstName,
                                                @Param("lastName") String lastName,
                                                @Param("cardNumber") String cardNumber);

}
