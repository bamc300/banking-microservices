package com.devsu.mscuentasmovimientos.infrastructure.adapter.in.web;

import com.devsu.mscuentasmovimientos.application.dto.MovementRequestDto;
import com.devsu.mscuentasmovimientos.application.dto.MovementResponseDto;
import com.devsu.mscuentasmovimientos.application.mapper.MovementMapper;
import com.devsu.mscuentasmovimientos.domain.port.in.RegisterMovementUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movements")
@RequiredArgsConstructor
@Tag(name = "Movements", description = "Movement Management API")
public class MovementController {

    private final RegisterMovementUseCase registerMovementUseCase;
    private final MovementMapper movementMapper;

    @PostMapping
    @Operation(summary = "Register movement")
    public ResponseEntity<MovementResponseDto> registerMovement(@Valid @RequestBody MovementRequestDto requestDto) {
        var movement = movementMapper.toDomain(requestDto);
        var registeredMovement = registerMovementUseCase.registerMovement(movement);
        var responseDto = movementMapper.toResponseDto(registeredMovement);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
