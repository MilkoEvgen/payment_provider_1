package com.milko.payment_provider.mapper;

import com.milko.payment_provider.dto.CustomerDto;
import com.milko.payment_provider.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public Customer map(CustomerDto customerDto){
        return Customer.builder()
                .firstName(customerDto.getFirstName())
                .lastName(customerDto.getLastName())
                .country(customerDto.getCountry())
                .build();
    }

    public CustomerDto map(Customer customer){
        return CustomerDto.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .country(customer.getCountry())
                .build();
    }
}
