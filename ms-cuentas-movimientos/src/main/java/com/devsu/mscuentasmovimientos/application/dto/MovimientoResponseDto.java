package com.devsu.mscuentasmovimientos.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MovimientoResponseDto {
    private UUID movimientoId;
    private LocalDateTime fecha;
    private String tipoMovimiento;
    private BigDecimal valor;
    private BigDecimal saldo;
    private UUID cuentaId;
}
