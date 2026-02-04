package com.devsu.mscuentasmovimientos.infrastructure.adapter.in.messaging;

import com.devsu.mscuentasmovimientos.domain.model.ClientInfo;
import com.devsu.mscuentasmovimientos.domain.port.in.ClientEventConsumerPort;
import com.devsu.mscuentasmovimientos.infrastructure.adapter.in.messaging.event.ClientCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientEventConsumerAdapter {

    private final ClientEventConsumerPort clientEventConsumerPort;

    @RabbitListener(queues = "${rabbitmq.queue.clients:clients.queue}")
    public void handleClientCreated(ClientCreatedEvent event) {
        log.info("Received ClientCreatedEvent: {}", event);

        ClientInfo clientInfo = ClientInfo.builder()
                .clientId(event.getClientId())
                .name(event.getName())
                .identification(event.getIdentification())
                .status(event.isStatus())
                .build();

        clientEventConsumerPort.processClientCreated(clientInfo);

        log.info("ClientCreatedEvent processed successfully");
    }
}
