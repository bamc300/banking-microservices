package com.devsu.msclientespersonas.application.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaSaldoDto {
    private String numeroCuenta;
    private BigDecimal saldoActual;
}
