package com.devsu.mscuentasmovimientos.infrastructure.adapter.in.web;

import com.devsu.mscuentasmovimientos.application.dto.MovimientoRequestDto;
import com.devsu.mscuentasmovimientos.application.dto.MovimientoResponseDto;
import com.devsu.mscuentasmovimientos.application.mapper.MovimientoMapper;
import com.devsu.mscuentasmovimientos.domain.port.in.RegistrarMovimientoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos", description = "API de gestión de movimientos")
public class MovimientoController {

    private final RegistrarMovimientoUseCase registrarMovimientoUseCase;
    private final MovimientoMapper movimientoMapper;

    @PostMapping
    @Operation(summary = "Registrar movimiento")
    public ResponseEntity<MovimientoResponseDto> registrarMovimiento(
            @Valid @RequestBody MovimientoRequestDto requestDto) {
        var movimiento = movimientoMapper.toDomain(requestDto);
        var movimientoRegistrado = registrarMovimientoUseCase.registrarMovimiento(movimiento);
        var responseDto = movimientoMapper.toResponseDto(movimientoRegistrado);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
