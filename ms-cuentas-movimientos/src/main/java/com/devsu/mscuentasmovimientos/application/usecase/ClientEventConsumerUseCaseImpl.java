package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.domain.model.ClientInfo;
import com.devsu.mscuentasmovimientos.domain.port.in.ClientEventConsumerPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientEventConsumerUseCaseImpl implements ClientEventConsumerPort {

    @Override
    public void processClientCreated(ClientInfo clientInfo) {
        log.info("Processing created client: ID={}, Name={}, Identification={}",
                clientInfo.getClientId(), clientInfo.getName(), clientInfo.getIdentification());

        log.info("Client {} processed successfully", clientInfo.getClientId());
    }
}
