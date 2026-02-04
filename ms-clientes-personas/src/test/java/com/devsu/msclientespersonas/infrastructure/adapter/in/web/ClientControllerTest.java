package com.devsu.msclientespersonas.infrastructure.adapter.in.web;

import com.devsu.msclientespersonas.application.dto.ClientRequestDto;
import com.devsu.msclientespersonas.application.dto.ClientResponseDto;
import com.devsu.msclientespersonas.application.mapper.ClientMapper;
import com.devsu.msclientespersonas.domain.model.Client;
import com.devsu.msclientespersonas.domain.model.Person;
import com.devsu.msclientespersonas.domain.port.in.UpdateClientUseCase;
import com.devsu.msclientespersonas.domain.port.in.GetClientUseCase;
import com.devsu.msclientespersonas.domain.port.in.CreateClientUseCase;
import com.devsu.msclientespersonas.domain.port.in.DeleteClientUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateClientUseCase createClientUseCase;

    @MockBean
    private GetClientUseCase getClientUseCase;

    @MockBean
    private DeleteClientUseCase deleteClientUseCase;

    @MockBean
    private UpdateClientUseCase updateClientUseCase;

    @MockBean
    private ClientMapper clientMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private ClientRequestDto requestDto;
    private ClientResponseDto responseDto;
    private Client client;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        
        requestDto = new ClientRequestDto();
        requestDto.setName("Test User");
        requestDto.setIdentification("1234567890");
        requestDto.setPassword("1234");
        requestDto.setGender("MASCULINO");
        requestDto.setAge(30);
        requestDto.setAddress("Address");
        requestDto.setPhone("0999999999");

        responseDto = new ClientResponseDto();
        responseDto.setClientId(clientId);
        responseDto.setName("Test User");

        client = Client.builder()
                .clientId(clientId)
                .person(Person.builder().name("Test User").build())
                .build();
    }

    @Test
    void createClient_ShouldReturn201() throws Exception {
        when(clientMapper.toDomain(any(ClientRequestDto.class))).thenReturn(client);
        when(createClientUseCase.createClient(any(Client.class))).thenReturn(client);
        when(clientMapper.toResponseDto(any(Client.class))).thenReturn(responseDto);

        mockMvc.perform(post("/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientId").exists());
    }

    @Test
    void getClient_ShouldReturn200() throws Exception {
        when(getClientUseCase.getClientById(clientId)).thenReturn(Optional.of(client));
        when(clientMapper.toResponseDto(any(Client.class))).thenReturn(responseDto);

        mockMvc.perform(get("/clients/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").exists());
    }

    @Test
    void updateClient_ShouldReturn200() throws Exception {
        when(clientMapper.toDomain(any(ClientRequestDto.class))).thenReturn(client);
        when(updateClientUseCase.updateClient(eq(clientId), any(Client.class))).thenReturn(client);
        when(clientMapper.toResponseDto(any(Client.class))).thenReturn(responseDto);

        mockMvc.perform(put("/clients/{clientId}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").exists());
    }

    @Test
    void deleteClient_ShouldReturn200() throws Exception {
        when(deleteClientUseCase.deleteClient(clientId)).thenReturn(client);
        when(clientMapper.toResponseDto(any(Client.class))).thenReturn(responseDto);

        mockMvc.perform(delete("/clients/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").exists());
    }
}
