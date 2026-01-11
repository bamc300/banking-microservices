package com.devsu.mscuentasmovimientos.domain.port.in;

import com.devsu.mscuentasmovimientos.application.dto.CuentaResponseDto;

import java.util.List;
import java.util.UUID;

public interface ConsultarCuentasPorClienteUseCase {
    List<CuentaResponseDto> obtenerCuentasPorCliente(UUID clienteId);
}
