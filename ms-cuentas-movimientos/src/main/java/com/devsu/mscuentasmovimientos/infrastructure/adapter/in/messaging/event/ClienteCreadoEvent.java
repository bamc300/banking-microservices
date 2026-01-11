package com.devsu.mscuentasmovimientos.infrastructure.adapter.in.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteCreadoEvent {
    private UUID clienteId;
    private String nombre;
    private String identificacion;
    private boolean estado;
    private LocalDateTime timestamp;
}
