package com.devsu.msclientespersonas.application.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class ClienteResponseDto {
  private UUID clienteId;
  private String nombre;
  private String genero;
  private Integer edad;
  private String identificacion;
  private String direccion;
  private String telefono;
  private boolean estado;
  private List<CuentaDto> cuentas;
}

