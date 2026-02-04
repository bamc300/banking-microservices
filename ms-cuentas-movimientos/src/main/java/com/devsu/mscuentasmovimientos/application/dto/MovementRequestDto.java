package com.devsu.mscuentasmovimientos.application.dto;

import com.devsu.mscuentasmovimientos.domain.model.Movement;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MovementRequestDto {
    @NotNull(message = "Movement type is required")
    private Movement.MovementType movementType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Account ID is required")
    private UUID accountId;
}
