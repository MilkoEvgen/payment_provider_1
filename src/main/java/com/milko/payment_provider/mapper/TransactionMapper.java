package com.milko.payment_provider.mapper;

import com.milko.payment_provider.dto.CardDataDto;
import com.milko.payment_provider.dto.CustomerDto;
import com.milko.payment_provider.dto.TransactionDto;
import com.milko.payment_provider.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionMapper {


    public Transaction map(TransactionDto transactionDto){
        return Transaction.builder()
                .id(transactionDto.getId())
                .paymentMethod(transactionDto.getPaymentMethod())
                .amount(transactionDto.getAmount())
                .currency(transactionDto.getCurrency())
                .createdAt(transactionDto.getCreatedAt())
                .updatedAt(transactionDto.getUpdatedAt())
                .type(transactionDto.getType())
                .cardDataId(transactionDto.getCardData().getId())
                .language(transactionDto.getLanguage())
                .notificationUrl(transactionDto.getNotificationUrl())
                .customerId(transactionDto.getCustomer().getId())
                .status(transactionDto.getStatus())
                .message(transactionDto.getMessage())
                .build();
    }

    public TransactionDto map(Transaction transaction){
        return TransactionDto.builder()
                .id(transaction.getId())
                .paymentMethod(transaction.getPaymentMethod())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .type(transaction.getType())
                .language(transaction.getLanguage())
                .notificationUrl(transaction.getNotificationUrl())
                .status(transaction.getStatus())
                .message(transaction.getMessage())
                .build();
    }

    public TransactionDto map(Transaction transaction, CardDataDto cardDataDto, CustomerDto customerDto){
        return TransactionDto.builder()
                .id(transaction.getId())
                .cardData(cardDataDto)
                .customer(customerDto)
                .paymentMethod(transaction.getPaymentMethod())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .type(transaction.getType())
                .language(transaction.getLanguage())
                .notificationUrl(transaction.getNotificationUrl())
                .status(transaction.getStatus())
                .message(transaction.getMessage())
                .build();
    }
}
