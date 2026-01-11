package com.devsu.msclientespersonas.domain.port.in;

import com.devsu.msclientespersonas.domain.model.Cliente;

import java.util.UUID;

public interface ActualizarClienteUseCase {
    Cliente actualizarCliente(UUID clienteId, Cliente cliente);
}
