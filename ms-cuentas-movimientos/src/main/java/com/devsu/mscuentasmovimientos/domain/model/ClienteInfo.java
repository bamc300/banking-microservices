package com.devsu.mscuentasmovimientos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteInfo {
    private UUID clienteId;
    private String nombre;
    private String identificacion;
    private boolean estado;
}
