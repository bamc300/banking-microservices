package com.devsu.mscuentasmovimientos.domain.port.in;

import com.devsu.mscuentasmovimientos.domain.model.Movimiento;

public interface RegistrarMovimientoUseCase {
    Movimiento registrarMovimiento(Movimiento movimiento);
}
