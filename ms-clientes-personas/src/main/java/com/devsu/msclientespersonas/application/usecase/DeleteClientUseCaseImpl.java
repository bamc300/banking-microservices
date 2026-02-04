package com.devsu.msclientespersonas.application.usecase;

import com.devsu.msclientespersonas.application.dto.AccountBalanceDto;
import com.devsu.msclientespersonas.domain.exception.ClientNotFoundException;
import com.devsu.msclientespersonas.domain.exception.AccountsWithBalanceException;
import com.devsu.msclientespersonas.domain.model.Client;
import com.devsu.msclientespersonas.domain.port.in.DeleteClientUseCase;
import com.devsu.msclientespersonas.domain.port.out.ClientRepositoryPort;
import com.devsu.msclientespersonas.domain.port.out.AccountExternalServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteClientUseCaseImpl implements DeleteClientUseCase {

    private final ClientRepositoryPort clientRepositoryPort;
    private final AccountExternalServicePort accountExternalServicePort;

    @Override
    @Transactional
    public Client deleteClient(UUID clientId) {
        Client client = clientRepositoryPort.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(
                        "Client not found with ID: " + clientId));

        List<AccountBalanceDto> accounts = accountExternalServicePort.getAccountsWithBalance(clientId);

        boolean hasPendingBalance =
                accounts.stream().anyMatch(c -> c.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0);

        if (hasPendingBalance) {
            throw new AccountsWithBalanceException(accounts);
        }

        client.setStatus(false);
        Client savedClient = clientRepositoryPort.save(client);

        accountExternalServicePort.deleteAccounts(clientId);

        return savedClient;
    }
}
