package com.devsu.msclientespersonas.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ClienteRequestDto {
  @NotBlank
  private String nombre;

  @NotBlank
  private String genero;

  @NotNull
  @Positive
  private Integer edad;

  @NotBlank
  private String identificacion;

  @NotBlank
  private String direccion;

  @NotBlank
  private String telefono;

  @NotBlank
  private String contrasena;
}

