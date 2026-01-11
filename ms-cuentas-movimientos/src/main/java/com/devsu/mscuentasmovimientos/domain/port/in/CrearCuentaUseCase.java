package com.devsu.mscuentasmovimientos.domain.port.in;

import com.devsu.mscuentasmovimientos.domain.model.Cuenta;

public interface CrearCuentaUseCase {
    Cuenta crearCuenta(Cuenta cuenta);
}
