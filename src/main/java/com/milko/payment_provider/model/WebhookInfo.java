package com.milko.payment_provider.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@Table(name = "webhooks")
public class WebhookInfo {
    @Id
    private UUID id;
    private String url;
    @Column(value = "date_time")
    private LocalDateTime dateTime;
    @Column(value = "status_code")
    private Integer statusCode;
    private String response;
}
