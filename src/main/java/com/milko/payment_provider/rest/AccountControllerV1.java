package com.milko.payment_provider.rest;

import com.milko.payment_provider.dto.WalletDto;
import com.milko.payment_provider.security.MerchantDetails;
import com.milko.payment_provider.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name="Balance controller", description="Allows to get merchant's balance")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/balance")
public class AccountControllerV1 {
    private final WalletService walletService;

    @Operation(
            summary = "Get balance by currency",
            description = "Allows the merchant to get balance by currency name"
    )
    @GetMapping("{currency}")
    public Mono<WalletDto> getAccountByCurrency(@PathVariable @Parameter(description = "Currency", required = true) String currency, Authentication auth){
        UUID merchantId = ((MerchantDetails) auth.getPrincipal()).getId();
        return walletService.getWalletByCurrency(currency, merchantId);
    }

    @Operation(
            summary = "Get all balances",
            description = "Allows the merchant to get his all balances"
    )
    @GetMapping("/list")
    public Flux<WalletDto> getAllAccounts(Authentication auth){
        UUID merchantId = ((MerchantDetails) auth.getPrincipal()).getId();
        return walletService.getAllWalletsByMerchantId(merchantId);
    }
}
