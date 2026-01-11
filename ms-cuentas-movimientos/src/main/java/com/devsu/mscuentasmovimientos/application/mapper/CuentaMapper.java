package com.devsu.mscuentasmovimientos.application.mapper;

import com.devsu.mscuentasmovimientos.application.dto.CuentaRequestDto;
import com.devsu.mscuentasmovimientos.application.dto.CuentaResponseDto;
import com.devsu.mscuentasmovimientos.domain.model.Cuenta;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CuentaMapper {

    public Cuenta toDomain(CuentaRequestDto dto) {
        return Cuenta.builder().cuentaId(UUID.randomUUID()).numeroCuenta(dto.getNumeroCuenta())
                .tipoCuenta(dto.getTipoCuenta())
                .saldoInicial(dto.getSaldoInicial()).estado(true).clienteId(dto.getClienteId())
                .build();
    }

    public CuentaResponseDto toResponseDto(Cuenta cuenta) {
        CuentaResponseDto dto = new CuentaResponseDto();
        dto.setCuentaId(cuenta.getCuentaId());
        dto.setNumeroCuenta(cuenta.getNumeroCuenta());
        dto.setTipoCuenta(cuenta.getTipoCuenta().name());
        dto.setSaldoInicial(cuenta.getSaldoInicial());
        dto.setEstado(cuenta.isEstado());
        dto.setClienteId(cuenta.getClienteId());
        return dto;
    }
}
