package com.devsu.msclientespersonas.application.usecase;

import com.devsu.msclientespersonas.application.mapper.ClienteMapper;
import com.devsu.msclientespersonas.domain.model.Cliente;
import com.devsu.msclientespersonas.domain.port.in.CrearClienteUseCase;
import com.devsu.msclientespersonas.domain.port.out.ClienteEventPublisherPort;
import com.devsu.msclientespersonas.domain.port.out.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrearClienteUseCaseImpl implements CrearClienteUseCase {

  private final ClienteRepositoryPort clienteRepositoryPort;
  private final ClienteEventPublisherPort clienteEventPublisherPort;  

  @Override
  public Cliente crearCliente(Cliente cliente) {
    if (clienteRepositoryPort.existePorIdentificacion(cliente.getPersona().getIdentificacion())) {
      throw new IllegalArgumentException("Ya existe un cliente con esa identificación: " + cliente.getPersona().getIdentificacion());
    }

    Cliente clienteGuardado = clienteRepositoryPort.guardar(cliente);
    clienteEventPublisherPort.publicarClienteCreado(clienteGuardado);

    return clienteGuardado;
  }

  @Override
  public List<Cliente> crearClientes(List<Cliente> clientes) {
    return clientes.stream()
        .map(this::crearCliente)
        .collect(Collectors.toList());
  }
}

