package com.devsu.msclientespersonas.integration;

import com.devsu.msclientespersonas.application.dto.ClientRequestDto;
import com.devsu.msclientespersonas.domain.port.out.ClientRepositoryPort;
import com.devsu.msclientespersonas.infrastructure.adapter.out.messaging.event.ClientCreatedEvent;
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
public class CreateClientIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3-management");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepositoryPort clientRepositoryPort;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createClient_ShouldSaveClientAndPublishEvent() throws Exception {
        ClientRequestDto requestDto = ClientRequestDto.builder()
                .name("New Client Integration").identification("9988776655")
                .address("Test Address").phone("0987654321").gender("Female").age(25)
                .password("1234").build();

        mockMvc.perform(post("/clients").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.clientId").exists());

        boolean exists = clientRepositoryPort.existsByIdentification("9988776655");
        assertThat(exists).isTrue();

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Object message = rabbitTemplate.receiveAndConvert("clients.queue");
            assertThat(message).isNotNull();

            if (message instanceof ClientCreatedEvent) {
                ClientCreatedEvent event = (ClientCreatedEvent) message;
                assertThat(event.getIdentification()).isEqualTo("9988776655");
            } else {
                assertThat(message.toString()).contains("9988776655");
            }
        });
    }
}
