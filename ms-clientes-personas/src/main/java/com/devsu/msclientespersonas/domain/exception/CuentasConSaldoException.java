package com.devsu.msclientespersonas.domain.exception;

import com.devsu.msclientespersonas.application.dto.CuentaSaldoDto;
import lombok.Getter;

import java.util.List;

@Getter
public class CuentasConSaldoException extends RuntimeException {
    private final List<CuentaSaldoDto> cuentas;

    public CuentasConSaldoException(List<CuentaSaldoDto> cuentas) {
        super("El cliente tiene cuentas con saldo pendiente.");
        this.cuentas = cuentas;
    }
}
