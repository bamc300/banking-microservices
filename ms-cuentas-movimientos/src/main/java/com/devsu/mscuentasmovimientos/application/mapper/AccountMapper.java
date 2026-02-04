package com.devsu.mscuentasmovimientos.application.mapper;

import com.devsu.mscuentasmovimientos.application.dto.AccountRequestDto;
import com.devsu.mscuentasmovimientos.application.dto.AccountResponseDto;
import com.devsu.mscuentasmovimientos.domain.model.Account;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountMapper {

    public Account toDomain(AccountRequestDto dto) {
        return Account.builder().accountId(UUID.randomUUID()).accountNumber(dto.getAccountNumber())
                .accountType(dto.getAccountType())
                .initialBalance(dto.getInitialBalance()).status(true).clientId(dto.getClientId())
                .build();
    }

    public AccountResponseDto toResponseDto(Account account) {
        AccountResponseDto dto = new AccountResponseDto();
        dto.setAccountId(account.getAccountId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(account.getAccountType().name());
        dto.setInitialBalance(account.getInitialBalance());
        dto.setCurrentBalance(account.getCurrentBalance());
        dto.setStatus(account.isStatus());
        dto.setClientId(account.getClientId());
        return dto;
    }
}
