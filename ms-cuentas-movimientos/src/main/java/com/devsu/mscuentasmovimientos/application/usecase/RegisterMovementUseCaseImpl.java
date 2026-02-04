package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.domain.exception.BalanceNotAvailableException;
import com.devsu.mscuentasmovimientos.domain.model.Account;
import com.devsu.mscuentasmovimientos.domain.model.Movement;
import com.devsu.mscuentasmovimientos.domain.port.in.RegisterMovementUseCase;
import com.devsu.mscuentasmovimientos.domain.port.out.AccountRepositoryPort;
import com.devsu.mscuentasmovimientos.domain.port.out.MovementRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegisterMovementUseCaseImpl implements RegisterMovementUseCase {

    private final AccountRepositoryPort accountRepositoryPort;
    private final MovementRepositoryPort movementRepositoryPort;

    @Override
    @Transactional
    public Movement registerMovement(Movement movement) {
        Account account = accountRepositoryPort.findById(movement.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (!account.isStatus()) {
            throw new IllegalArgumentException("Account is inactive");
        }

        BigDecimal currentBalance = calculateCurrentBalance(account);
        BigDecimal newBalance = calculateNewBalance(currentBalance, movement.getAmount(), movement.getMovementType());

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BalanceNotAvailableException("Balance not available");
        }

        movement.setDate(LocalDateTime.now());
        movement.setBalance(newBalance);

        return movementRepositoryPort.save(movement);
    }

    private BigDecimal calculateCurrentBalance(Account account) {
        Optional<Movement> lastMovement = movementRepositoryPort.findLastByAccountId(account.getAccountId());
        return lastMovement.map(Movement::getBalance).orElse(account.getInitialBalance());
    }

    private BigDecimal calculateNewBalance(BigDecimal currentBalance, BigDecimal amount, Movement.MovementType movementType) {
        return switch (movementType) {
            case DEPOSIT -> currentBalance.add(amount);
            case WITHDRAWAL -> currentBalance.subtract(amount);
        };
    }
}
