package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.domain.exception.SaldoNoDisponibleException;
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
class MovimientoUseCaseTest {

    @Mock
    private MovimientoRepositoryPort movimientoRepositoryPort;

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @InjectMocks
    private RegistrarMovimientoUseCaseImpl registrarMovimientoUseCase;

    private Cuenta cuenta;
    private Movimiento movimiento;
    private UUID cuentaId;

    @BeforeEach
    void setUp() {
        cuentaId = UUID.randomUUID();
        cuenta = Cuenta.builder()
                .cuentaId(cuentaId)
                .saldoInicial(BigDecimal.valueOf(1000))
                .estado(true)
                .build();

        movimiento = Movimiento.builder()
                .cuentaId(cuentaId)
                .valor(BigDecimal.valueOf(100))
                .tipoMovimiento(Movimiento.TipoMovimiento.DEPOSITO)
                .build();
    }

    @Test
    void registrarMovimiento_DeberiaRegistrarDeposito() {
        when(cuentaRepositoryPort.buscarPorId(cuentaId)).thenReturn(Optional.of(cuenta));
        when(movimientoRepositoryPort.obtenerUltimoMovimientoPorCuentaId(cuentaId)).thenReturn(null);
        when(movimientoRepositoryPort.guardar(any(Movimiento.class))).thenReturn(movimiento);

        Movimiento resultado = registrarMovimientoUseCase.registrarMovimiento(movimiento);

        assertNotNull(resultado);
        verify(movimientoRepositoryPort).guardar(any(Movimiento.class));
    }

    @Test
    void registrarMovimiento_DeberiaLanzarExcepcion_CuandoSaldoInsuficiente() {
        movimiento.setTipoMovimiento(Movimiento.TipoMovimiento.RETIRO);
        movimiento.setValor(BigDecimal.valueOf(2000)); // Retiro mayor a saldo (1000)

        when(cuentaRepositoryPort.buscarPorId(cuentaId)).thenReturn(Optional.of(cuenta));
        when(movimientoRepositoryPort.obtenerUltimoMovimientoPorCuentaId(cuentaId)).thenReturn(null);

        assertThrows(SaldoNoDisponibleException.class, () -> registrarMovimientoUseCase.registrarMovimiento(movimiento));
        verify(movimientoRepositoryPort, never()).guardar(any(Movimiento.class));
    }
}
