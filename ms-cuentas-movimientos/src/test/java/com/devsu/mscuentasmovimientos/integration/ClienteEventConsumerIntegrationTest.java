package com.devsu.mscuentasmovimientos.integration;

import com.devsu.mscuentasmovimientos.domain.port.in.ClienteEventConsumerPort;
import com.devsu.mscuentasmovimientos.infrastructure.adapter.in.messaging.event.ClienteCreadoEvent;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
public class ClienteEventConsumerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3-management");

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @SpyBean
    private ClienteEventConsumerPort clienteEventConsumerPort;

    @Test
    void handleClienteCreado_DeberiaProcesarEvento() {
        UUID clienteId = UUID.randomUUID();
        ClienteCreadoEvent event = ClienteCreadoEvent.builder().clienteId(clienteId)
                .nombre("Integration Test User").identificacion("9988776655").estado(true)
                .timestamp(LocalDateTime.now()).build();

        rabbitTemplate.convertAndSend("clientes.queue", event);

        verify(clienteEventConsumerPort, timeout(5000)).procesarClienteCreado(any());
    }
}
