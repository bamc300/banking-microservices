package com.devsu.mscuentasmovimientos.application.mapper;

import com.devsu.mscuentasmovimientos.application.dto.MovementRequestDto;
import com.devsu.mscuentasmovimientos.application.dto.MovementResponseDto;
import com.devsu.mscuentasmovimientos.domain.model.Movement;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class MovementMapper {

    public Movement toDomain(MovementRequestDto dto) {
        return Movement.builder().movementId(UUID.randomUUID()).date(LocalDateTime.now())
                .movementType(dto.getMovementType())
                .amount(dto.getAmount()).accountId(dto.getAccountId()).build();
    }

    public MovementResponseDto toResponseDto(Movement movement) {
        MovementResponseDto dto = new MovementResponseDto();
        dto.setMovementId(movement.getMovementId());
        dto.setDate(movement.getDate());
        dto.setMovementType(movement.getMovementType().name());
        dto.setAmount(movement.getAmount());
        dto.setBalance(movement.getBalance());
        dto.setAccountId(movement.getAccountId());
        return dto;
    }
}
