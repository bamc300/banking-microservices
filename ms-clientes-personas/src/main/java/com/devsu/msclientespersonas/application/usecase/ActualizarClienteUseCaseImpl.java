package com.devsu.msclientespersonas.application.usecase;

import com.devsu.msclientespersonas.domain.exception.ClienteNoEncontradoException;
import com.devsu.msclientespersonas.domain.model.Cliente;
import com.devsu.msclientespersonas.domain.port.in.ActualizarClienteUseCase;
import com.devsu.msclientespersonas.domain.port.out.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActualizarClienteUseCaseImpl implements ActualizarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    @Transactional
    public Cliente actualizarCliente(UUID clienteId, Cliente clienteActualizado) {
        Cliente clienteExistente = clienteRepositoryPort.buscarPorId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado con ID: " + clienteId));

        if (!clienteExistente.getPersona().getIdentificacion().equals(clienteActualizado.getPersona().getIdentificacion()) &&
                clienteRepositoryPort.existePorIdentificacion(clienteActualizado.getPersona().getIdentificacion())) {
            throw new IllegalArgumentException("Ya existe un cliente con esa identificación");
        }
        
        clienteExistente.getPersona().setNombre(clienteActualizado.getPersona().getNombre());
        clienteExistente.getPersona().setGenero(clienteActualizado.getPersona().getGenero());
        clienteExistente.getPersona().setEdad(clienteActualizado.getPersona().getEdad());
        clienteExistente.getPersona().setIdentificacion(clienteActualizado.getPersona().getIdentificacion());
        clienteExistente.getPersona().setDireccion(clienteActualizado.getPersona().getDireccion());
        clienteExistente.getPersona().setTelefono(clienteActualizado.getPersona().getTelefono());

        clienteExistente.setContrasena(clienteActualizado.getContrasena());
        
        return clienteRepositoryPort.guardar(clienteExistente);
    }
}
