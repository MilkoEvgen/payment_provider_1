package com.milko.payment_provider.rest;

import com.milko.payment_provider.dto.CustomResponseEntity;
import com.milko.payment_provider.dto.TransactionDto;
import com.milko.payment_provider.model.TransactionStatus;
import com.milko.payment_provider.model.TransactionType;
import com.milko.payment_provider.security.MerchantDetails;
import com.milko.payment_provider.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name="Transaction controller", description="Allows to create transaction, get transaction list and get transaction details")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class TransactionRestControllerV1 {
    private final TransactionService transactionService;

    @Operation(
            summary = "Create top up transaction",
            description = "Allows the merchant to create transaction and top up his balance"
    )
    @PostMapping("/transaction")
    public Mono<CustomResponseEntity> createTransaction(@RequestBody TransactionDto dto, Authentication auth){
        UUID merchantId = ((MerchantDetails) auth.getPrincipal()).getId();
        return transactionService.createTransaction(dto, merchantId, TransactionType.TRANSACTION)
                .map(transactionDto -> CustomResponseEntity.builder()
                            .transaction_id(transactionDto.getId())
                            .status(transactionDto.getStatus().toString())
                            .message("OK")
                            .build()
                ).onErrorResume(e -> Mono.just(CustomResponseEntity.builder()
                        .transaction_id(null)
                        .status(TransactionStatus.FAILED.toString())
                        .message(e.getMessage())
                        .build())
                );
    }

    @Operation(
            summary = "Get all top up transactions",
            description = "Allows the merchant to get all his top up transactions"
    )
    @GetMapping("/transaction/list")
    public Flux<TransactionDto> getAllTransactions(@RequestParam(name = "start_date", required = false) Long startDate,
                                                   @RequestParam(name = "end_date", required = false) Long endDate,
                                                   Authentication auth){
        UUID merchantId = ((MerchantDetails) auth.getPrincipal()).getId();
        return transactionService.getAllTransactions(startDate, endDate, merchantId, TransactionType.TRANSACTION);
    }

    @Operation(
            summary = "Get top up transaction by id",
            description = "Allows the merchant to find top up transaction by id"
    )
    @GetMapping("/transaction/{transactionId}/details")
    public Mono<TransactionDto> getTransactionById(@PathVariable @Parameter(description = "Transaction id", required = true) UUID transactionId, Authentication auth){
        UUID merchantId = ((MerchantDetails) auth.getPrincipal()).getId();
        return transactionService.findTransactionById(transactionId, merchantId, TransactionType.TRANSACTION);
    }

    @Operation(
            summary = "Create payout transaction",
            description = "Allows the merchant to create payout transaction and withdraw money from his balance"
    )
    @PostMapping("/payout")
    public Mono<CustomResponseEntity> createPayout(@RequestBody TransactionDto dto, Authentication auth){
        UUID merchantId = ((MerchantDetails) auth.getPrincipal()).getId();
        return transactionService.createTransaction(dto, merchantId, TransactionType.PAYOUT)
                .map(transactionDto -> CustomResponseEntity.builder()
                        .transaction_id(transactionDto.getId())
                        .status(transactionDto.getStatus().toString())
                        .message("OK")
                        .build()
                ).onErrorResume(e -> Mono.just(CustomResponseEntity.builder()
                        .transaction_id(null)
                        .status(TransactionStatus.FAILED.toString())
                        .message(e.getMessage())
                        .build())
                );
    }

    @Operation(
            summary = "Get all payout transactions",
            description = "Allows the merchant to get all his payout transactions"
    )
    @GetMapping("/payout/list")
    public Flux<TransactionDto> getAllPayouts(@RequestParam(name = "start_date", required = false) Long startDate,
                              @RequestParam(name = "end_date", required = false) Long endDate,
                              Authentication auth){
        UUID merchantId = ((MerchantDetails) auth.getPrincipal()).getId();
        return transactionService.getAllTransactions(startDate, endDate, merchantId, TransactionType.PAYOUT);
    }

    @Operation(
            summary = "Get payout transaction by id",
            description = "Allows the merchant to find payout transaction by id"
    )
    @GetMapping("/payout/{payoutId}/details")
    public Mono<TransactionDto> getPayOutById(@PathVariable @Parameter(description = "Transaction id", required = true) UUID payoutId, Authentication auth){
        UUID merchantId = ((MerchantDetails) auth.getPrincipal()).getId();
        return transactionService.findTransactionById(payoutId, merchantId, TransactionType.PAYOUT);
    }
}
