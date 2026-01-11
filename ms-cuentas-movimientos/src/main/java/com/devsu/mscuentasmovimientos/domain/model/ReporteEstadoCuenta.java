package com.devsu.mscuentasmovimientos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteEstadoCuenta {
    private UUID clienteId;
    private String nombreCliente;
    private List<CuentaReporte> cuentas;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CuentaReporte {
        private String numeroCuenta;
        private String tipoCuenta;
        private boolean estado;
        private List<Movimiento> movimientos;
    }
}
