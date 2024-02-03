package com.milko.payment_provider.mapper;

import com.milko.payment_provider.dto.AccountDto;
import com.milko.payment_provider.model.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account map(AccountDto accountDto){
        return Account.builder()
                .id(accountDto.getId())
                .amount(accountDto.getAmount())
                .currency(accountDto.getCurrency())
                .build();
    }

    public AccountDto map(Account account){
        return AccountDto.builder()
                .id(account.getId())
                .amount(account.getAmount())
                .currency(account.getCurrency())
                .build();
    }
}
