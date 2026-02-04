package com.devsu.msclientespersonas.domain.exception;

import com.devsu.msclientespersonas.application.dto.AccountBalanceDto;
import lombok.Getter;

import java.util.List;

@Getter
public class AccountsWithBalanceException extends RuntimeException {
    private final List<AccountBalanceDto> accounts;

    public AccountsWithBalanceException(List<AccountBalanceDto> accounts) {
        super("The client has accounts with pending balance.");
        this.accounts = accounts;
    }
}
