package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.domain.model.Account;
import com.devsu.mscuentasmovimientos.domain.port.out.AccountRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAccountUseCaseTest {

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @InjectMocks
    private CreateAccountUseCaseImpl createAccountUseCase;

    private Account account;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        account = Account.builder()
                .accountId(accountId)
                .accountNumber("12345")
                .accountType(Account.AccountType.SAVINGS)
                .initialBalance(BigDecimal.valueOf(100))
                .status(true)
                .clientId(UUID.randomUUID())
                .build();
    }

    @Test
    void createAccount_ShouldSaveAccount() {
        when(accountRepositoryPort.save(any(Account.class))).thenReturn(account);
        when(accountRepositoryPort.existsByAccountNumber(any())).thenReturn(false);

        Account result = createAccountUseCase.createAccount(account);

        assertNotNull(result);
        assertEquals(accountId, result.getAccountId());
        verify(accountRepositoryPort).save(any(Account.class));
    }
}
