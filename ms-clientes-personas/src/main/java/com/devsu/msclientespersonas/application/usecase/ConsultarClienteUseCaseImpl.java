package com.devsu.msclientespersonas.application.usecase;

import com.devsu.msclientespersonas.domain.model.Cliente;
import com.devsu.msclientespersonas.domain.port.in.ConsultarClienteUseCase;
import com.devsu.msclientespersonas.domain.port.out.ClienteRepositoryPort;
import com.devsu.msclientespersonas.domain.port.out.CuentaExternalServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultarClienteUseCaseImpl implements ConsultarClienteUseCase {

  private final ClienteRepositoryPort clienteRepositoryPort;
  private final CuentaExternalServicePort cuentaExternalServicePort;

  @Override
  public Optional<Cliente> obtenerClientePorId(UUID clienteId) {
    Optional<Cliente> clienteOpt = clienteRepositoryPort.buscarPorId(clienteId);
    clienteOpt.ifPresent(cliente -> {
      cliente.setCuentas(cuentaExternalServicePort.obtenerCuentas(clienteId));
    });
    return clienteOpt;
  }

  @Override
  public List<Cliente> obtenerTodosLosClientes() {
    List<Cliente> clientes = clienteRepositoryPort.buscarTodos();    
    clientes.forEach(cliente -> {
      cliente.setCuentas(cuentaExternalServicePort.obtenerCuentas(cliente.getClienteId()));
    });
    return clientes;
  }
}
