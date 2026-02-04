package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.application.dto.AccountBalanceDto;
import com.devsu.mscuentasmovimientos.domain.model.Account;
import com.devsu.mscuentasmovimientos.domain.model.Movement;
import com.devsu.mscuentasmovimientos.domain.port.in.ValidateClientDeletionUseCase;
import com.devsu.mscuentasmovimientos.domain.port.out.AccountRepositoryPort;
import com.devsu.mscuentasmovimientos.domain.port.out.MovementRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ValidateClientDeletionUseCaseImpl implements ValidateClientDeletionUseCase {

    private final AccountRepositoryPort accountRepositoryPort;
    private final MovementRepositoryPort movementRepositoryPort;

    @Override
    public List<AccountBalanceDto> getAccountsWithBalance(UUID clientId) {
        List<Account> accounts = accountRepositoryPort.findByClientId(clientId);
        List<AccountBalanceDto> accountsWithBalance = new ArrayList<>();

        for (Account account : accounts) {
            BigDecimal currentBalance = calculateCurrentBalance(account);
            AccountBalanceDto dto = AccountBalanceDto.builder()
                    .accountNumber(account.getAccountNumber())
                    .currentBalance(currentBalance)
                    .build();
            accountsWithBalance.add(dto);
        }

        return accountsWithBalance;
    }

    private BigDecimal calculateCurrentBalance(Account account) {
        Optional<Movement> lastMovement = movementRepositoryPort.findLastByAccountId(account.getAccountId());
        return lastMovement.map(Movement::getBalance).orElse(account.getInitialBalance());
    }
}
