package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.application.mapper.MovimientoMapper;
import com.devsu.mscuentasmovimientos.domain.model.Cuenta;
import com.devsu.mscuentasmovimientos.domain.model.Movimiento;
import com.devsu.mscuentasmovimientos.domain.model.ReporteEstadoCuenta;
import com.devsu.mscuentasmovimientos.domain.port.out.CuentaRepositoryPort;
import com.devsu.mscuentasmovimientos.domain.port.out.MovimientoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerarReporteUseCaseTest {

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @Mock
    private MovimientoRepositoryPort movimientoRepositoryPort;

    @Mock
    private MovimientoMapper movimientoMapper;

    @InjectMocks
    private GenerarReporteUseCaseImpl generarReporteUseCase;

    private UUID clienteId;
    private Cuenta cuenta;
    private Movimiento movimiento;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        cuenta = Cuenta.builder()
                .cuentaId(UUID.randomUUID())
                .numeroCuenta("123456")
                .tipoCuenta(Cuenta.TipoCuenta.AHORROS)
                .estado(true)
                .clienteId(clienteId)
                .build();
        
        movimiento = Movimiento.builder()
                .movimientoId(UUID.randomUUID())
                .cuentaId(cuenta.getCuentaId())
                .valor(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    void generarReporte_DeberiaGenerarReporteConCuentasYMovimientos() {
        LocalDate fechaInicio = LocalDate.now().minusDays(30);
        LocalDate fechaFin = LocalDate.now();

        when(cuentaRepositoryPort.buscarPorClienteId(clienteId)).thenReturn(Collections.singletonList(cuenta));
        when(movimientoRepositoryPort.buscarPorCuentaIdYRangoFechas(eq(cuenta.getCuentaId()), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(movimiento));

        ReporteEstadoCuenta reporte = generarReporteUseCase.generarReporte(clienteId, fechaInicio, fechaFin);

        assertNotNull(reporte);
        assertEquals(clienteId, reporte.getClienteId());
        assertEquals(1, reporte.getCuentas().size());
        assertEquals(1, reporte.getCuentas().get(0).getMovimientos().size());
    }

    @Test
    void generarReporte_DeberiaLanzarExcepcion_CuandoNoHayCuentas() {
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = LocalDate.now();

        when(cuentaRepositoryPort.buscarPorClienteId(clienteId)).thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> generarReporteUseCase.generarReporte(clienteId, fechaInicio, fechaFin));
    }
}
