package com.devsu.mscuentasmovimientos.domain.port.in;

import java.util.UUID;

public interface InactivarCuentasPorClienteUseCase {
    void inactivarCuentasPorCliente(UUID clienteId);
}
