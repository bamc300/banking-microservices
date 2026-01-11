package com.devsu.mscuentasmovimientos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {
  private UUID cuentaId;
  private String numeroCuenta;
  private TipoCuenta tipoCuenta;
  private BigDecimal saldoInicial;
  private boolean estado;
  private UUID clienteId;

  public enum TipoCuenta {
    AHORROS, CORRIENTE
  }
}

