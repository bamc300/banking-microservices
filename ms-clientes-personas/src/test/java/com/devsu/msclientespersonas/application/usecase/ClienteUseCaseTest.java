package com.devsu.msclientespersonas.application.usecase;

import com.devsu.msclientespersonas.application.dto.CuentaSaldoDto;
import com.devsu.msclientespersonas.application.mapper.ClienteMapper;
import com.devsu.msclientespersonas.domain.exception.ClienteNoEncontradoException;
import com.devsu.msclientespersonas.domain.exception.CuentasConSaldoException;
import com.devsu.msclientespersonas.domain.model.Cliente;
import com.devsu.msclientespersonas.domain.model.Persona;
import com.devsu.msclientespersonas.domain.port.out.ClienteEventPublisherPort;
import com.devsu.msclientespersonas.domain.port.out.ClienteRepositoryPort;
import com.devsu.msclientespersonas.domain.port.out.CuentaExternalServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteUseCaseTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private CuentaExternalServicePort cuentaExternalServicePort;

    @Mock
    private ClienteEventPublisherPort clienteEventPublisherPort;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private CrearClienteUseCaseImpl crearClienteUseCase;

    @InjectMocks
    private InactivarClienteUseCaseImpl inactivarClienteUseCase;

    @InjectMocks
    private ActualizarClienteUseCaseImpl actualizarClienteUseCase;

    @InjectMocks
    private ConsultarClienteUseCaseImpl consultarClienteUseCase;

    private Cliente cliente;
    private UUID clienteId;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        Persona persona =
                Persona.builder().nombre("Test User").identificacion("1234567890").build();

        cliente = Cliente.builder().clienteId(clienteId).contrasena("1234").estado(true)
                .persona(persona).build();
    }

    @Test
    void crearCliente_DeberiaGuardarCliente() {
        when(clienteRepositoryPort.existePorIdentificacion(any())).thenReturn(false);
        when(clienteRepositoryPort.guardar(any(Cliente.class))).thenReturn(cliente);

        Cliente resultado = crearClienteUseCase.crearCliente(cliente);

        assertNotNull(resultado);
        assertEquals(clienteId, resultado.getClienteId());
        verify(clienteRepositoryPort).guardar(any(Cliente.class));
    }

    @Test
    void crearCliente_DeberiaLanzarExcepcion_CuandoExisteIdentificacion() {
        when(clienteRepositoryPort.existePorIdentificacion(any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> crearClienteUseCase.crearCliente(cliente));
        verify(clienteRepositoryPort, never()).guardar(any());
    }

    @Test
    void inactivarCliente_DeberiaInactivar_CuandoValidacionEsExitosa() {
        when(clienteRepositoryPort.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));
        when(cuentaExternalServicePort.obtenerCuentasConSaldo(clienteId)).thenReturn(List.of());
        when(clienteRepositoryPort.guardar(any(Cliente.class))).thenReturn(cliente);

        Cliente resultado = inactivarClienteUseCase.inactivarCliente(clienteId);

        assertFalse(resultado.isEstado());
        verify(clienteRepositoryPort).guardar(cliente);
        verify(cuentaExternalServicePort).inactivarCuentas(clienteId);
    }

    @Test
    void inactivarCliente_DeberiaLanzarExcepcion_CuandoValidacionFalla() {
        when(clienteRepositoryPort.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));
        CuentaSaldoDto cuentaConSaldo =
                CuentaSaldoDto.builder().numeroCuenta("12345").saldoActual(BigDecimal.TEN).build();
        when(cuentaExternalServicePort.obtenerCuentasConSaldo(clienteId))
                .thenReturn(List.of(cuentaConSaldo));

        assertThrows(CuentasConSaldoException.class,
                () -> inactivarClienteUseCase.inactivarCliente(clienteId));
        verify(clienteRepositoryPort, never()).guardar(any());
    }

    @Test
    void actualizarCliente_DeberiaActualizar_CuandoExiste() {
        Cliente clienteActualizado =
                Cliente.builder().clienteId(clienteId).contrasena("5678").persona(Persona.builder()
                        .nombre("Updated Name").identificacion("1234567890").build()).build();

        when(clienteRepositoryPort.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepositoryPort.guardar(any(Cliente.class))).thenReturn(clienteActualizado);

        Cliente resultado =
                actualizarClienteUseCase.actualizarCliente(clienteId, clienteActualizado);

        assertEquals("5678", resultado.getContrasena());
        assertEquals("Updated Name", resultado.getPersona().getNombre());
    }

    @Test
    void consultarCliente_DeberiaRetornarCliente_CuandoExiste() {
        when(clienteRepositoryPort.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));

        Optional<Cliente> resultado = consultarClienteUseCase.obtenerClientePorId(clienteId);

        assertTrue(resultado.isPresent());
        assertEquals(clienteId, resultado.get().getClienteId());
    }
}
