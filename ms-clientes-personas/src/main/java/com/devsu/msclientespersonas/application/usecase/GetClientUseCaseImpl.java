package com.devsu.msclientespersonas.application.usecase;

import com.devsu.msclientespersonas.domain.model.Client;
import com.devsu.msclientespersonas.domain.port.in.GetClientUseCase;
import com.devsu.msclientespersonas.domain.port.out.ClientRepositoryPort;
import com.devsu.msclientespersonas.domain.port.out.AccountExternalServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetClientUseCaseImpl implements GetClientUseCase {

    private final ClientRepositoryPort clientRepositoryPort;
    private final AccountExternalServicePort accountExternalServicePort;

    @Override
    public Optional<Client> getClientById(UUID clientId) {
        Optional<Client> clientOpt = clientRepositoryPort.findById(clientId);
        clientOpt.ifPresent(client -> {
            client.setAccounts(accountExternalServicePort.getAccounts(clientId));
        });
        return clientOpt;
    }

    @Override
    public List<Client> getAllClients() {
        List<Client> clients = clientRepositoryPort.findAll();    
        clients.forEach(client -> {
            client.setAccounts(accountExternalServicePort.getAccounts(client.getClientId()));
        });
        return clients;
    }
}
