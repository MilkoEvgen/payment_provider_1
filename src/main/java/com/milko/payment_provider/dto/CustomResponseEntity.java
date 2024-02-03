package com.milko.payment_provider.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class CustomResponseEntity {
    private UUID transaction_id;
    protected String status;
    protected String message;
}
