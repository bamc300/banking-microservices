package com.devsu.mscuentasmovimientos.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CuentaRequestDto {
    @NotBlank
    private String numeroCuenta;

    @NotNull
    private com.devsu.mscuentasmovimientos.domain.model.Cuenta.TipoCuenta tipoCuenta;

    @NotNull
    @Positive
    private BigDecimal saldoInicial;

    @NotNull
    private UUID clienteId;
}
