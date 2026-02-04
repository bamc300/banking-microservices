package com.devsu.msclientespersonas.application.usecase;

import com.devsu.msclientespersonas.application.dto.AccountBalanceDto;
import com.devsu.msclientespersonas.application.mapper.ClientMapper;
import com.devsu.msclientespersonas.domain.exception.AccountsWithBalanceException;
import com.devsu.msclientespersonas.domain.model.Client;
import com.devsu.msclientespersonas.domain.model.Person;
import com.devsu.msclientespersonas.domain.port.out.ClientEventPublisherPort;
import com.devsu.msclientespersonas.domain.port.out.ClientRepositoryPort;
import com.devsu.msclientespersonas.domain.port.out.AccountExternalServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientUseCaseTest {

    @Mock
    private ClientRepositoryPort clientRepositoryPort;

    @Mock
    private AccountExternalServicePort accountExternalServicePort;

    @Mock
    private ClientEventPublisherPort clientEventPublisherPort;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private CreateClientUseCaseImpl createClientUseCase;

    @InjectMocks
    private DeleteClientUseCaseImpl deleteClientUseCase;

    @InjectMocks
    private UpdateClientUseCaseImpl updateClientUseCase;

    @InjectMocks
    private GetClientUseCaseImpl getClientUseCase;

    private Client client;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        Person person =
                Person.builder().name("Test User").identification("1234567890").build();

        client = Client.builder().clientId(clientId).password("1234").status(true)
                .person(person).build();
    }

    @Test
    void createClient_ShouldSaveClient() {
        when(clientRepositoryPort.existsByIdentification(any())).thenReturn(false);
        when(clientRepositoryPort.save(any(Client.class))).thenReturn(client);

        Client result = createClientUseCase.createClient(client);

        assertNotNull(result);
        assertEquals(clientId, result.getClientId());
        verify(clientRepositoryPort).save(any(Client.class));
    }

    @Test
    void createClient_ShouldThrowException_WhenIdentificationExists() {
        when(clientRepositoryPort.existsByIdentification(any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> createClientUseCase.createClient(client));
        verify(clientRepositoryPort, never()).save(any());
    }

    @Test
    void deleteClient_ShouldInactivate_WhenValidationSuccessful() {
        when(clientRepositoryPort.findById(clientId)).thenReturn(Optional.of(client));
        when(accountExternalServicePort.getAccountsWithBalance(clientId)).thenReturn(List.of());
        when(clientRepositoryPort.save(any(Client.class))).thenReturn(client);

        Client result = deleteClientUseCase.deleteClient(clientId);

        assertFalse(result.isStatus());
        verify(clientRepositoryPort).save(client);
        verify(accountExternalServicePort).deleteAccounts(clientId);
    }

    @Test
    void deleteClient_ShouldThrowException_WhenValidationFails() {
        when(clientRepositoryPort.findById(clientId)).thenReturn(Optional.of(client));
        AccountBalanceDto accountWithBalance =
                AccountBalanceDto.builder().accountNumber("12345").currentBalance(BigDecimal.TEN).build();
        when(accountExternalServicePort.getAccountsWithBalance(clientId))
                .thenReturn(List.of(accountWithBalance));

        assertThrows(AccountsWithBalanceException.class,
                () -> deleteClientUseCase.deleteClient(clientId));
        verify(clientRepositoryPort, never()).save(any());
    }

    @Test
    void updateClient_ShouldUpdate_WhenExists() {
        Client updatedClient =
                Client.builder().clientId(clientId).password("5678").person(Person.builder()
                        .name("Updated Name").identification("1234567890").build()).build();

        when(clientRepositoryPort.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepositoryPort.save(any(Client.class))).thenReturn(updatedClient);

        Client result =
                updateClientUseCase.updateClient(clientId, updatedClient);

        assertEquals("5678", result.getPassword());
        assertEquals("Updated Name", result.getPerson().getName());
    }

    @Test
    void getClient_ShouldReturnClient_WhenExists() {
        when(clientRepositoryPort.findById(clientId)).thenReturn(Optional.of(client));

        Optional<Client> result = getClientUseCase.getClientById(clientId);

        assertTrue(result.isPresent());
        assertEquals(clientId, result.get().getClientId());
    }
}
