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
public class ClientCreatedEvent {
    private UUID clientId;
    private String name;
    private String identification;
    private boolean status;
    private LocalDateTime timestamp;
}
