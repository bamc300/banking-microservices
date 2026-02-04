package com.devsu.msclientespersonas.application.usecase;

import com.devsu.msclientespersonas.domain.exception.ClientNotFoundException;
import com.devsu.msclientespersonas.domain.model.Client;
import com.devsu.msclientespersonas.domain.port.in.UpdateClientUseCase;
import com.devsu.msclientespersonas.domain.port.out.ClientRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateClientUseCaseImpl implements UpdateClientUseCase {

    private final ClientRepositoryPort clientRepositoryPort;

    @Override
    @Transactional
    public Client updateClient(UUID clientId, Client updatedClient) {
        Client existingClient = clientRepositoryPort.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with ID: " + clientId));

        if (!existingClient.getPerson().getIdentification().equals(updatedClient.getPerson().getIdentification()) &&
                clientRepositoryPort.existsByIdentification(updatedClient.getPerson().getIdentification())) {
            throw new IllegalArgumentException("A client with this identification already exists");
        }
        
        existingClient.getPerson().setName(updatedClient.getPerson().getName());
        existingClient.getPerson().setGender(updatedClient.getPerson().getGender());
        existingClient.getPerson().setAge(updatedClient.getPerson().getAge());
        existingClient.getPerson().setIdentification(updatedClient.getPerson().getIdentification());
        existingClient.getPerson().setAddress(updatedClient.getPerson().getAddress());
        existingClient.getPerson().setPhone(updatedClient.getPerson().getPhone());

        existingClient.setPassword(updatedClient.getPassword());
        
        return clientRepositoryPort.save(existingClient);
    }
}
