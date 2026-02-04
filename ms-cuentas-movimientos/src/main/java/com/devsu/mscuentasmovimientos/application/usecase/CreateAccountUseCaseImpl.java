package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.domain.model.Account;
import com.devsu.mscuentasmovimientos.domain.port.in.CreateAccountUseCase;
import com.devsu.mscuentasmovimientos.domain.port.out.AccountRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateAccountUseCaseImpl implements CreateAccountUseCase {

    private final AccountRepositoryPort accountRepositoryPort;

    @Override
    public Account createAccount(Account account) {
        if (accountRepositoryPort.existsByAccountNumber(account.getAccountNumber())) {
            throw new IllegalArgumentException("Account with this number already exists");
        }

        return accountRepositoryPort.save(account);
    }
}
