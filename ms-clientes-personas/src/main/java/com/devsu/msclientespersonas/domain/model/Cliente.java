package com.devsu.msclientespersonas.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
  private UUID clienteId;
  private String contrasena;
  private boolean estado;
  private Persona persona;
  private List<Cuenta> cuentas;
}

