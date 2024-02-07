package com.milko.payment_provider.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.milko.payment_provider.model.TransactionStatus;
import com.milko.payment_provider.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDto {

    private UUID id;

    private String paymentMethod;

    private Integer amount;

    private String currency;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private TransactionType type;

    private String language;

    private String notificationUrl;

    private CustomerDto customer;

    private CardDataDto cardData;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private TransactionStatus status;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String message;
}
