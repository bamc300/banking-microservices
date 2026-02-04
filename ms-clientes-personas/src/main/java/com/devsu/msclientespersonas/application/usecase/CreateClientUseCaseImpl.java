package com.devsu.msclientespersonas.application.usecase;

import com.devsu.msclientespersonas.domain.model.Client;
import com.devsu.msclientespersonas.domain.port.in.CreateClientUseCase;
import com.devsu.msclientespersonas.domain.port.out.ClientEventPublisherPort;
import com.devsu.msclientespersonas.domain.port.out.ClientRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateClientUseCaseImpl implements CreateClientUseCase {

    private final ClientRepositoryPort clientRepositoryPort;
    private final ClientEventPublisherPort clientEventPublisherPort;  

    @Override
    public Client createClient(Client client) {
        if (clientRepositoryPort.existsByIdentification(client.getPerson().getIdentification())) {
            throw new IllegalArgumentException("A client with this identification already exists: " + client.getPerson().getIdentification());
        }

        Client savedClient = clientRepositoryPort.save(client);
        clientEventPublisherPort.publishClientCreated(savedClient);

        return savedClient;
    }

    @Override
    public List<Client> createClients(List<Client> clients) {
        return clients.stream()
                .map(this::createClient)
                .collect(Collectors.toList());
    }
}
