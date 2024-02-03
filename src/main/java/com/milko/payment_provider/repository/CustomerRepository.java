package com.milko.payment_provider.repository;

import com.milko.payment_provider.model.Customer;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface CustomerRepository extends R2dbcRepository<Customer, Integer> {
}
