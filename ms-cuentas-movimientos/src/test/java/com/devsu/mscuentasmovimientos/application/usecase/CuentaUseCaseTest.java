package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.domain.model.Cuenta;
import com.devsu.mscuentasmovimientos.domain.model.Movimiento;
import com.devsu.mscuentasmovimientos.domain.port.out.CuentaRepositoryPort;
import com.devsu.mscuentasmovimientos.domain.port.out.MovimientoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaUseCaseTest {

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @Mock
    private MovimientoRepositoryPort movimientoRepositoryPort;

    @InjectMocks
    private CrearCuentaUseCaseImpl crearCuentaUseCase;

    @InjectMocks
    private InactivarCuentaUseCaseImpl inactivarCuentaUseCase;

    private Cuenta cuenta;
    private UUID cuentaId;

    @BeforeEach
    void setUp() {
        cuentaId = UUID.randomUUID();
        cuenta = Cuenta.builder().cuentaId(cuentaId).numeroCuenta("12345")
                .tipoCuenta(Cuenta.TipoCuenta.AHORROS).saldoInicial(BigDecimal.valueOf(100))
                // .saldoActual(BigDecimal.valueOf(100))
                .estado(true).clienteId(UUID.randomUUID()).build();
    }

    @Test
    void crearCuenta_DeberiaGuardarCuenta() {
        when(cuentaRepositoryPort.guardar(any(Cuenta.class))).thenReturn(cuenta);

        Cuenta resultado = crearCuentaUseCase.crearCuenta(cuenta);

        assertNotNull(resultado);
        assertEquals(cuentaId, resultado.getCuentaId());
        verify(cuentaRepositoryPort).guardar(any(Cuenta.class));
    }

    @Test
    void inactivarCuenta_DeberiaInactivarYCrearMovimiento_CuandoTieneSaldo() {
        when(cuentaRepositoryPort.buscarPorId(cuentaId)).thenReturn(Optional.of(cuenta));
        when(movimientoRepositoryPort.obtenerUltimoMovimientoPorCuentaId(cuentaId))
                .thenReturn(null);
        when(cuentaRepositoryPort.guardar(any(Cuenta.class))).thenReturn(cuenta);

        Cuenta resultado = inactivarCuentaUseCase.inactivarCuenta(cuentaId);

        assertFalse(resultado.isEstado());
        verify(movimientoRepositoryPort).guardar(any(Movimiento.class));
        verify(cuentaRepositoryPort).guardar(cuenta);
    }

    @Test
    void inactivarCuenta_NoDeberiaCrearMovimiento_CuandoSaldoEsCero() {
        cuenta.setSaldoInicial(BigDecimal.ZERO);
        // cuenta.setSaldoActual(BigDecimal.ZERO); 

        when(cuentaRepositoryPort.buscarPorId(cuentaId)).thenReturn(Optional.of(cuenta));
        when(movimientoRepositoryPort.obtenerUltimoMovimientoPorCuentaId(cuentaId))
                .thenReturn(null);
        when(cuentaRepositoryPort.guardar(any(Cuenta.class))).thenReturn(cuenta);

        Cuenta resultado = inactivarCuentaUseCase.inactivarCuenta(cuentaId);

        assertFalse(resultado.isEstado());
        verify(movimientoRepositoryPort, never()).guardar(any(Movimiento.class));
        verify(cuentaRepositoryPort).guardar(cuenta);
    }
}
