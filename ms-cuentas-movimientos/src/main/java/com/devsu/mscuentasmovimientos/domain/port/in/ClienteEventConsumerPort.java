package com.devsu.mscuentasmovimientos.domain.port.in;

import com.devsu.mscuentasmovimientos.domain.model.ClienteInfo;

public interface ClienteEventConsumerPort {
    void procesarClienteCreado(ClienteInfo clienteInfo);
}
