package com.devsu.mscuentasmovimientos.domain.port.in;

import com.devsu.mscuentasmovimientos.domain.model.ReporteEstadoCuenta;

import java.time.LocalDate;
import java.util.UUID;

public interface GenerarReporteUseCase {
    ReporteEstadoCuenta generarReporte(UUID clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
