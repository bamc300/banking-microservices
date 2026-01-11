package com.devsu.mscuentasmovimientos.infrastructure.adapter.in.messaging;

import com.devsu.mscuentasmovimientos.domain.model.ClienteInfo;
import com.devsu.mscuentasmovimientos.domain.port.in.ClienteEventConsumerPort;
import com.devsu.mscuentasmovimientos.infrastructure.adapter.in.messaging.event.ClienteCreadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteEventConsumerAdapter {

    private final ClienteEventConsumerPort clienteEventConsumerPort;

    @RabbitListener(queues = "${rabbitmq.queue.clientes:clientes.queue}")
    public void handleClienteCreado(ClienteCreadoEvent event) {
        log.info("Recibido evento ClienteCreado: {}", event);

        ClienteInfo clienteInfo =
                ClienteInfo.builder().clienteId(event.getClienteId()).nombre(event.getNombre())
                        .identificacion(event.getIdentificacion()).estado(event.isEstado()).build();

        clienteEventConsumerPort.procesarClienteCreado(clienteInfo);

        log.info("Evento ClienteCreado procesado exitosamente");
    }
}
