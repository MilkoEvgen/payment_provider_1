package com.milko.payment_provider.repository;

import com.milko.payment_provider.model.CardData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface CardDataRepository extends R2dbcRepository<CardData, UUID> {
}
