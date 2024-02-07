package com.milko.payment_provider.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "card_data")
public class CardData {
    @Id
    private UUID id;
    @Column(value = "card_number")
    private String cardNumber;
    @Column(value = "exp_date")
    private String expDate;
    private String cvv;
    private UUID customerId;
}
