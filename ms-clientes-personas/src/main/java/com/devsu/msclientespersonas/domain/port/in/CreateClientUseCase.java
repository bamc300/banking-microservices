package com.devsu.msclientespersonas.domain.port.in;

import com.devsu.msclientespersonas.domain.model.Client;
import java.util.List;

public interface CreateClientUseCase {
    Client createClient(Client client);
    List<Client> createClients(List<Client> clients);
}
