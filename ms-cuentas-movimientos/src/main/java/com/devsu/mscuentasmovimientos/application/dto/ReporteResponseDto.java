package com.devsu.mscuentasmovimientos.application.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ReporteResponseDto {
    private UUID clienteId;
    private String nombreCliente;
    private List<CuentaReporteDto> cuentas;

    @Data
    public static class CuentaReporteDto {
        private String numeroCuenta;
        private String tipoCuenta;
        private boolean estado;
        private List<MovimientoResponseDto> movimientos;
    }
}
