package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.domain.model.Account;
import com.devsu.mscuentasmovimientos.domain.port.in.DeleteAccountsByClientUseCase;
import com.devsu.mscuentasmovimientos.domain.port.out.AccountRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteAccountsByClientUseCaseImpl implements DeleteAccountsByClientUseCase {

    private final AccountRepositoryPort accountRepositoryPort;

    @Override
    @Transactional
    public void deleteAccountsByClient(UUID clientId) {
        List<Account> accounts = accountRepositoryPort.findByClientId(clientId);
        accounts.forEach(account -> {
            account.setStatus(false);
            accountRepositoryPort.save(account);
        });
    }
}
