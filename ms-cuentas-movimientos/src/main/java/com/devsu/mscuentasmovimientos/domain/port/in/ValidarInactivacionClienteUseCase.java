package com.devsu.mscuentasmovimientos.domain.port.in;

import com.devsu.mscuentasmovimientos.application.dto.CuentaSaldoDto;
import java.util.List;
import java.util.UUID;

public interface ValidarInactivacionClienteUseCase {
    List<CuentaSaldoDto> obtenerCuentasConSaldo(UUID clienteId);
}
