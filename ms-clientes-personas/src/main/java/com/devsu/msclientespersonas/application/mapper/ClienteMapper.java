package com.devsu.msclientespersonas.application.mapper;

import com.devsu.msclientespersonas.application.dto.ClienteRequestDto;
import com.devsu.msclientespersonas.application.dto.ClienteResponseDto;
import com.devsu.msclientespersonas.domain.model.Cliente;
import com.devsu.msclientespersonas.domain.model.Persona;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ClienteMapper {

  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public Cliente toDomain(ClienteRequestDto dto) {
    Persona persona = Persona.builder().nombre(dto.getNombre()).genero(dto.getGenero())
        .edad(dto.getEdad()).identificacion(dto.getIdentificacion()).direccion(dto.getDireccion())
        .telefono(dto.getTelefono()).build();

    return Cliente.builder().clienteId(UUID.randomUUID())
        .contrasena(passwordEncoder.encode(dto.getContrasena())).estado(true).persona(persona)
        .build();
  }

  public ClienteResponseDto toResponseDto(Cliente cliente) {
    ClienteResponseDto dto = new ClienteResponseDto();
    dto.setClienteId(cliente.getClienteId());
    dto.setNombre(cliente.getPersona().getNombre());
    dto.setGenero(cliente.getPersona().getGenero());
    dto.setEdad(cliente.getPersona().getEdad());
    dto.setIdentificacion(cliente.getPersona().getIdentificacion());
    dto.setDireccion(cliente.getPersona().getDireccion());
    dto.setTelefono(cliente.getPersona().getTelefono());
    dto.setEstado(cliente.isEstado());
    return dto;
  }
}

