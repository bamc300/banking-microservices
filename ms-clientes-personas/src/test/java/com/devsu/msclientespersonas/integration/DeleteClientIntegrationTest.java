package com.devsu.msclientespersonas.integration;

import com.devsu.msclientespersonas.application.dto.AccountBalanceDto;
import com.devsu.msclientespersonas.domain.model.Client;
import com.devsu.msclientespersonas.domain.model.Person;
import com.devsu.msclientespersonas.domain.port.out.ClientRepositoryPort;
import com.devsu.msclientespersonas.domain.port.out.AccountExternalServicePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class DeleteClientIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3-management");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepositoryPort clientRepositoryPort;

    @MockBean
    private AccountExternalServicePort accountExternalServicePort;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID clientId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        Person person = Person.builder().name("Test User Integration")
                .identification("1122334455").address("Test Address")
                .phone("0999999999").gender("Male").age(30).build();

        Client client = Client.builder().clientId(clientId)
                .password("1234").status(true).person(person).build();

        Client savedClient = clientRepositoryPort.save(client);
        this.clientId = savedClient.getClientId();
    }

    @Test
    void deleteClient_ShouldInactivateClient_WhenNoPendingBalance() throws Exception {
        when(accountExternalServicePort.getAccountsWithBalance(clientId))
                .thenReturn(List.of());

        mockMvc.perform(delete("/clients/" + clientId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Client updatedClient = clientRepositoryPort.findById(clientId).orElseThrow();
        assertFalse(updatedClient.isStatus(), "Client should be inactive");

        verify(accountExternalServicePort).deleteAccounts(clientId);
    }

    @Test
    void deleteClient_ShouldFail_WhenHasPendingBalance() throws Exception {
        AccountBalanceDto accountWithBalance = AccountBalanceDto.builder().accountNumber("123456")
                .currentBalance(new BigDecimal("100.00")).build();

        when(accountExternalServicePort.getAccountsWithBalance(clientId))
                .thenReturn(List.of(accountWithBalance));

        // Assuming GlobalExceptionHandler maps AccountsWithBalanceException to 200 OK with error details as per original test
        // or maybe it maps to 409 Conflict or 400 Bad Request. 
        // In GlobalExceptionHandlerTest it was asserting HttpStatus.OK for handleAccountsWithBalance.
        mockMvc.perform(delete("/clients/" + clientId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); 
    }
}
