package com.devsu.mscuentasmovimientos.application.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CuentaSaldoDto {
    private String numeroCuenta;
    private BigDecimal saldoActual;
}
