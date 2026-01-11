package com.devsu.msclientespersonas.domain.port.in;

import com.devsu.msclientespersonas.domain.model.Cliente;

import java.util.List;

public interface CrearClienteUseCase {
  Cliente crearCliente(Cliente cliente);
  List<Cliente> crearClientes(List<Cliente> clientes);
}

