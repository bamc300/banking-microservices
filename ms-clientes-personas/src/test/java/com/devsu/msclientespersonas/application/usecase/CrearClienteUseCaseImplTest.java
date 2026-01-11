package com.devsu.msclientespersonas.application.usecase;

import com.devsu.msclientespersonas.domain.model.Cliente;
import com.devsu.msclientespersonas.domain.model.Persona;
import com.devsu.msclientespersonas.domain.port.out.ClienteEventPublisherPort;
import com.devsu.msclientespersonas.domain.port.out.ClienteRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrearClienteUseCaseImplTest {

  @Mock
  private ClienteRepositoryPort clienteRepositoryPort;

  @Mock
  private ClienteEventPublisherPort clienteEventPublisherPort;

  @Mock
  private com.devsu.msclientespersonas.application.mapper.ClienteMapper clienteMapper;

  @InjectMocks
  private CrearClienteUseCaseImpl crearClienteUseCase;

  private Cliente cliente;
  private Persona persona;

  @BeforeEach
  void setUp() {
    persona = Persona.builder().nombre("Juan Pérez").genero("Masculino").edad(30)
        .identificacion("1234567890").direccion("Calle Principal 123").telefono("0987654321")
        .build();

    cliente = Cliente.builder().clienteId(UUID.randomUUID()).contrasena("password123").estado(true)
        .persona(persona).build();
  }

  @Test
  void crearCliente_DebeCrearClienteYPublicarEvento() {
    // Given
    when(clienteRepositoryPort.existePorIdentificacion(anyString())).thenReturn(false);
    when(clienteRepositoryPort.guardar(any(Cliente.class))).thenReturn(cliente);

    // When
    Cliente resultado = crearClienteUseCase.crearCliente(cliente);

    // Then
    assertNotNull(resultado);
    assertEquals(cliente.getClienteId(), resultado.getClienteId());
    assertEquals(cliente.getPersona().getNombre(), resultado.getPersona().getNombre());

    verify(clienteRepositoryPort).existePorIdentificacion("1234567890");
    verify(clienteRepositoryPort).guardar(cliente);
    verify(clienteEventPublisherPort).publicarClienteCreado(cliente);
  }

  @Test
  void crearCliente_ConIdentificacionExistente_DebeLanzarExcepcion() {
    // Given
    when(clienteRepositoryPort.existePorIdentificacion(anyString())).thenReturn(true);

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> crearClienteUseCase.crearCliente(cliente));

    assertEquals("Ya existe un cliente con esa identificación: 1234567890", exception.getMessage());
    verify(clienteRepositoryPort).existePorIdentificacion("1234567890");
    verify(clienteRepositoryPort, never()).guardar(any(Cliente.class));
    verify(clienteEventPublisherPort, never()).publicarClienteCreado(any(Cliente.class));
  }
}

