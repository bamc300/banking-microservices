package com.devsu.msclientespersonas.integration;

import com.devsu.msclientespersonas.application.dto.ClienteRequestDto;
import com.devsu.msclientespersonas.domain.port.out.ClienteRepositoryPort;
import com.devsu.msclientespersonas.infrastructure.adapter.out.messaging.event.ClienteCreadoEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class CrearClienteIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3-management");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepositoryPort clienteRepositoryPort;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crearCliente_DeberiaGuardarClienteYPublicarEvento() throws Exception {
        ClienteRequestDto requestDto = ClienteRequestDto.builder()
                .nombre("Nuevo Cliente Integration").identificacion("9988776655")
                .direccion("Direccion Test").telefono("0987654321").genero("Femenino").edad(25)
                .contrasena("1234").build();

        mockMvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.clienteId").exists());

        // Verificar base de datos
        boolean existe = clienteRepositoryPort.existePorIdentificacion("9988776655");
        assertThat(existe).isTrue();

        // Verificar evento RabbitMQ
        // Esperamos un poco para asegurarnos de que el mensaje llegue a la cola
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Object message = rabbitTemplate.receiveAndConvert("clientes.queue");
            assertThat(message).isNotNull();

            // Si el convertidor funciona correctamente, deberíamos obtener un objeto o un mapa
            // Dependiendo de la configuración del MessageConverter
            if (message instanceof ClienteCreadoEvent) {
                ClienteCreadoEvent event = (ClienteCreadoEvent) message;
                assertThat(event.getIdentificacion()).isEqualTo("9988776655");
            } else {
                // Si llega como LinkedHashMap (Jackson default)
                // O verificar simplemente que no es nulo si el tipo es complejo
                assertThat(message.toString()).contains("9988776655");
            }
        });
    }
}
