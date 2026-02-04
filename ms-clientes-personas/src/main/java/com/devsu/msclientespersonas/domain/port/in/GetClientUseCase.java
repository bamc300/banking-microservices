package com.devsu.msclientespersonas.domain.port.in;

import com.devsu.msclientespersonas.domain.model.Client;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GetClientUseCase {
    Optional<Client> getClientById(UUID clientId);
    List<Client> getAllClients();
}
