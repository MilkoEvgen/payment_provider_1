package com.milko.payment_provider.mapper;

import com.milko.payment_provider.dto.WalletDto;
import com.milko.payment_provider.model.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public Wallet map(WalletDto walletDto){
        return Wallet.builder()
                .id(walletDto.getId())
                .amount(walletDto.getAmount())
                .currency(walletDto.getCurrency())
                .build();
    }

    public WalletDto map(Wallet wallet){
        return WalletDto.builder()
                .id(wallet.getId())
                .amount(wallet.getAmount())
                .currency(wallet.getCurrency())
                .build();
    }
}
