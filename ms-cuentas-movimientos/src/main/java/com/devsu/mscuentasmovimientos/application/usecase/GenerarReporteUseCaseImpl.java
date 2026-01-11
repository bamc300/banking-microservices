package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.application.dto.ReporteResponseDto;
import com.devsu.mscuentasmovimientos.application.mapper.MovimientoMapper;
import com.devsu.mscuentasmovimientos.domain.model.Cuenta;
import com.devsu.mscuentasmovimientos.domain.model.Movimiento;
import com.devsu.mscuentasmovimientos.domain.model.ReporteEstadoCuenta;
import com.devsu.mscuentasmovimientos.domain.port.in.GenerarReporteUseCase;
import com.devsu.mscuentasmovimientos.domain.port.out.CuentaRepositoryPort;
import com.devsu.mscuentasmovimientos.domain.port.out.MovimientoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenerarReporteUseCaseImpl implements GenerarReporteUseCase {

    private final CuentaRepositoryPort cuentaRepositoryPort;
    private final MovimientoRepositoryPort movimientoRepositoryPort;
    private final MovimientoMapper movimientoMapper;

    @Override
    public ReporteEstadoCuenta generarReporte(UUID clienteId, LocalDate fechaInicio,
            LocalDate fechaFin) {
        List<Cuenta> cuentas = cuentaRepositoryPort.buscarPorClienteId(clienteId);

        if (cuentas.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron cuentas para el cliente");
        }

        LocalDateTime fechaInicioDateTime = fechaInicio.atStartOfDay();
        LocalDateTime fechaFinDateTime = fechaFin.atTime(23, 59, 59);

        List<ReporteEstadoCuenta.CuentaReporte> cuentasReporte = cuentas.stream().map(cuenta -> {
            List<Movimiento> movimientos = movimientoRepositoryPort.buscarPorCuentaIdYRangoFechas(
                    cuenta.getCuentaId(), fechaInicioDateTime, fechaFinDateTime);

            return ReporteEstadoCuenta.CuentaReporte.builder()
                    .numeroCuenta(cuenta.getNumeroCuenta())
                    .tipoCuenta(cuenta.getTipoCuenta().name()).estado(cuenta.isEstado())
                    .movimientos(movimientos).build();
        }).collect(Collectors.toList());

        return ReporteEstadoCuenta.builder().clienteId(clienteId).nombreCliente("Cliente") // En una
                                                                                           // implementación
                                                                                           // real,
                                                                                           // obtendríamos
                                                                                           // el
                                                                                           // nombre
                                                                                           // del
                                                                                           // servicio
                                                                                           // de
                                                                                           // clientes
                .cuentas(cuentasReporte).build();
    }
}
