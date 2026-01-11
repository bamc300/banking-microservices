package com.devsu.mscuentasmovimientos.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MovimientoRequestDto {
    @NotNull
    private String tipoMovimiento;

    @NotNull
    @Positive
    private BigDecimal valor;

    @NotNull
    private UUID cuentaId;
}
