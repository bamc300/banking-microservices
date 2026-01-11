package com.devsu.mscuentasmovimientos.domain.port.in;

import com.devsu.mscuentasmovimientos.domain.model.Cuenta;

import java.util.UUID;

public interface InactivarCuentaUseCase {
    Cuenta inactivarCuenta(UUID cuentaId);
}
