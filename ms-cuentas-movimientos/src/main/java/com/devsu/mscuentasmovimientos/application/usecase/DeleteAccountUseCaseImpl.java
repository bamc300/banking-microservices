package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.domain.model.Account;
import com.devsu.mscuentasmovimientos.domain.model.Movement;
import com.devsu.mscuentasmovimientos.domain.port.in.DeleteAccountUseCase;
import com.devsu.mscuentasmovimientos.domain.port.out.AccountRepositoryPort;
import com.devsu.mscuentasmovimientos.domain.port.out.MovementRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteAccountUseCaseImpl implements DeleteAccountUseCase {

    private final AccountRepositoryPort accountRepositoryPort;
    private final MovementRepositoryPort movementRepositoryPort;

    @Override
    @Transactional
    public Account deleteAccount(UUID accountId) {
        Account account = accountRepositoryPort.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + accountId));

        if (!account.isStatus()) {
            return account;
        }

        BigDecimal currentBalance = calculateCurrentBalance(account);

        if (currentBalance.compareTo(BigDecimal.ZERO) > 0) {
            Movement withdrawal = Movement.builder()
                    .movementId(UUID.randomUUID())
                    .date(LocalDateTime.now())
                    .movementType(Movement.MovementType.WITHDRAWAL)
                    .amount(currentBalance)
                    .balance(BigDecimal.ZERO)
                    .accountId(accountId)
                    .build();

            movementRepositoryPort.save(withdrawal);
        }

        account.setStatus(false);
        return accountRepositoryPort.save(account);
    }

    private BigDecimal calculateCurrentBalance(Account account) {
        Optional<Movement> lastMovement = movementRepositoryPort.findLastByAccountId(account.getAccountId());
        return lastMovement.map(Movement::getBalance).orElse(account.getInitialBalance());
    }
}
