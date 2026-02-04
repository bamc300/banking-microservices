package com.devsu.msclientespersonas.domain.port.in;

import com.devsu.msclientespersonas.domain.model.Client;
import java.util.UUID;

public interface UpdateClientUseCase {
    Client updateClient(UUID clientId, Client client);
}
