package com.milko.payment_provider.repository;

import com.milko.payment_provider.model.WebhookInfo;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface WebhookRepository extends R2dbcRepository<WebhookInfo, UUID> {
}
