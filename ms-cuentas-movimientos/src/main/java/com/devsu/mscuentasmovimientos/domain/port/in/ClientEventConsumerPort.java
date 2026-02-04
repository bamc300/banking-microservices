package com.devsu.mscuentasmovimientos.domain.port.in;

import com.devsu.mscuentasmovimientos.domain.model.ClientInfo;

public interface ClientEventConsumerPort {
    void processClientCreated(ClientInfo clientInfo);
}
