package com.devsu.mscuentasmovimientos.integration;

import com.devsu.mscuentasmovimientos.domain.model.Account;
import com.devsu.mscuentasmovimientos.domain.port.out.AccountRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class AccountIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3-management");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepositoryPort accountRepositoryPort;

    private UUID clientId;
    private Account accountWithBalance;
    private Account accountWithoutBalance;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();

        Account account1 = Account.builder()
                .clientId(clientId)
                .accountNumber("111111")
                .accountType(Account.AccountType.SAVINGS)
                .initialBalance(new BigDecimal("100.00"))
                .status(true)
                .build();

        Account account2 = Account.builder()
                .clientId(clientId)
                .accountNumber("222222")
                .accountType(Account.AccountType.CHECKING)
                .initialBalance(new BigDecimal("0.00"))
                .status(true)
                .build();

        accountWithBalance = accountRepositoryPort.save(account1);
        accountWithoutBalance = accountRepositoryPort.save(account2);
    }

    @Test
    void getAccountsByClient_ShouldReturnAccounts() throws Exception {
        mockMvc.perform(get("/accounts/client/" + clientId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void validateInactivation_ShouldReturnAccountsWithBalance() throws Exception {
        mockMvc.perform(get("/accounts/validation-inactivation/" + clientId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deleteAccountsByClient_ShouldInactivateAll() throws Exception {
        mockMvc.perform(patch("/accounts/client/" + clientId + "/inactivate")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Account c1 = accountRepositoryPort.findById(accountWithBalance.getAccountId()).orElseThrow();
        Account c2 = accountRepositoryPort.findById(accountWithoutBalance.getAccountId()).orElseThrow();

        assertFalse(c1.isStatus());
        assertFalse(c2.isStatus());
    }
}
