package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.domain.model.Account;
import com.devsu.mscuentasmovimientos.domain.model.Movement;
import com.devsu.mscuentasmovimientos.domain.port.in.GetAccountsByClientUseCase;
import com.devsu.mscuentasmovimientos.domain.port.out.AccountRepositoryPort;
import com.devsu.mscuentasmovimientos.domain.port.out.MovementRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAccountsByClientUseCaseImpl implements GetAccountsByClientUseCase {

    private final AccountRepositoryPort accountRepositoryPort;
    private final MovementRepositoryPort movementRepositoryPort;

    @Override
    public List<Account> getAccountsByClient(UUID clientId) {
        List<Account> accounts = accountRepositoryPort.findByClientId(clientId);
        return accounts.stream().map(account -> {
            account.setCurrentBalance(calculateCurrentBalance(account));
            return account;
        }).collect(Collectors.toList());
    }

    private BigDecimal calculateCurrentBalance(Account account) {
        Optional<Movement> lastMovement = movementRepositoryPort.findLastByAccountId(account.getAccountId());
        return lastMovement.map(Movement::getBalance).orElse(account.getInitialBalance());
    }
}
