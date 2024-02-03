package com.milko.payment_provider.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "accounts")
public class Account {
    @Id
    private Integer id;
    private Integer amount;
    private String currency;
    @Column(value = "merchant_id")
    private Integer merchantId;
}
