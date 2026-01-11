package com.devsu.msclientespersonas.domain.port.in;

import com.devsu.msclientespersonas.domain.model.Cliente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultarClienteUseCase {
  Optional<Cliente> obtenerClientePorId(UUID clienteId);

  List<Cliente> obtenerTodosLosClientes();
}

