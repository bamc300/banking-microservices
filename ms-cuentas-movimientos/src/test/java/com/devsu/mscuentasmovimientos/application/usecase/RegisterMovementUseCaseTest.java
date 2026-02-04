package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.domain.exception.BalanceNotAvailableException;
import com.devsu.mscuentasmovimientos.domain.model.Account;
import com.devsu.mscuentasmovimientos.domain.model.Movement;
import com.devsu.mscuentasmovimientos.domain.port.out.AccountRepositoryPort;
import com.devsu.mscuentasmovimientos.domain.port.out.MovementRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterMovementUseCaseTest {

    @Mock
    private MovementRepositoryPort movementRepositoryPort;

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @InjectMocks
    private RegisterMovementUseCaseImpl registerMovementUseCase;

    private Account account;
    private Movement movement;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        account = Account.builder()
                .accountId(accountId)
                .initialBalance(BigDecimal.valueOf(1000))
                .status(true)
                .build();

        movement = Movement.builder()
                .accountId(accountId)
                .amount(BigDecimal.valueOf(100))
                .movementType(Movement.MovementType.DEPOSIT)
                .build();
    }

    @Test
    void registerMovement_ShouldRegisterDeposit() {
        when(accountRepositoryPort.findById(accountId)).thenReturn(Optional.of(account));
        when(movementRepositoryPort.findLastByAccountId(accountId)).thenReturn(Optional.empty());
        when(movementRepositoryPort.save(any(Movement.class))).thenReturn(movement);

        Movement result = registerMovementUseCase.registerMovement(movement);

        assertNotNull(result);
        verify(movementRepositoryPort).save(any(Movement.class));
    }

    @Test
    void registerMovement_ShouldThrowException_WhenBalanceInsufficient() {
        movement.setMovementType(Movement.MovementType.WITHDRAWAL);
        movement.setAmount(BigDecimal.valueOf(2000));

        when(accountRepositoryPort.findById(accountId)).thenReturn(Optional.of(account));
        when(movementRepositoryPort.findLastByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(BalanceNotAvailableException.class, () -> registerMovementUseCase.registerMovement(movement));
        verify(movementRepositoryPort, never()).save(any(Movement.class));
    }
}
