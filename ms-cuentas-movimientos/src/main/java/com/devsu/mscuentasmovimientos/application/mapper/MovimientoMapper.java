package com.devsu.mscuentasmovimientos.application.mapper;

import com.devsu.mscuentasmovimientos.application.dto.MovimientoRequestDto;
import com.devsu.mscuentasmovimientos.application.dto.MovimientoResponseDto;
import com.devsu.mscuentasmovimientos.domain.model.Movimiento;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class MovimientoMapper {

    public Movimiento toDomain(MovimientoRequestDto dto) {
        return Movimiento.builder().movimientoId(UUID.randomUUID()).fecha(LocalDateTime.now())
                .tipoMovimiento(Movimiento.TipoMovimiento.valueOf(dto.getTipoMovimiento()))
                .valor(dto.getValor()).cuentaId(dto.getCuentaId()).build();
    }

    public MovimientoResponseDto toResponseDto(Movimiento movimiento) {
        MovimientoResponseDto dto = new MovimientoResponseDto();
        dto.setMovimientoId(movimiento.getMovimientoId());
        dto.setFecha(movimiento.getFecha());
        dto.setTipoMovimiento(movimiento.getTipoMovimiento().name());
        dto.setValor(movimiento.getValor());
        dto.setSaldo(movimiento.getSaldo());
        dto.setCuentaId(movimiento.getCuentaId());
        return dto;
    }
}
