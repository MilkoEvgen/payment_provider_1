package com.milko.payment_provider.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "transactions")
public class Transaction {

    @Id
    private UUID id;

    @Column(value = "payment_method")
    private String paymentMethod;

    private Integer amount;

    private String currency;

    @Column(value = "created_at")
    private LocalDateTime createdAt;

    @Column(value = "updated_at")
    private LocalDateTime updatedAt;

    private TransactionType type;

    private String language;

    @Column(value = "notification_url")
    private String notificationUrl;

    @Column(value = "customer_id")
    private UUID customerId;

    @Column(value = "merchant_id")
    private UUID merchantId;

//    @Column(value = "card_data_id")
//    private UUID cardDataId;

    @Column(value = "wallet_id")
    private UUID walletId;

    private TransactionStatus status;

    private String message;
}
