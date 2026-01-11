package com.devsu.mscuentasmovimientos.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CuentaResponseDto {
    private UUID cuentaId;
    private String numeroCuenta;
    private String tipoCuenta;
    private BigDecimal saldoInicial;
    private BigDecimal saldoActual;
    private boolean estado;
    private UUID clienteId;
}
