package com.devsu.msclientespersonas.infrastructure.adapter.in.web;

import com.devsu.msclientespersonas.application.dto.ClienteRequestDto;
import com.devsu.msclientespersonas.application.dto.ClienteResponseDto;
import com.devsu.msclientespersonas.application.mapper.ClienteMapper;
import com.devsu.msclientespersonas.domain.exception.ClienteNoEncontradoException;
import com.devsu.msclientespersonas.domain.model.Cliente;
import com.devsu.msclientespersonas.domain.model.Persona;
import com.devsu.msclientespersonas.domain.port.in.ActualizarClienteUseCase;
import com.devsu.msclientespersonas.domain.port.in.ConsultarClienteUseCase;
import com.devsu.msclientespersonas.domain.port.in.CrearClienteUseCase;
import com.devsu.msclientespersonas.domain.port.in.InactivarClienteUseCase;
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

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CrearClienteUseCase crearClienteUseCase;

    @MockBean
    private ConsultarClienteUseCase consultarClienteUseCase;

    @MockBean
    private InactivarClienteUseCase inactivarClienteUseCase;

    @MockBean
    private ActualizarClienteUseCase actualizarClienteUseCase;

    @MockBean
    private ClienteMapper clienteMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteRequestDto requestDto;
    private ClienteResponseDto responseDto;
    private Cliente cliente;
    private UUID clienteId;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        
        requestDto = new ClienteRequestDto();
        requestDto.setNombre("Test User");
        requestDto.setIdentificacion("1234567890");
        requestDto.setContrasena("1234");
        requestDto.setGenero("MASCULINO");
        requestDto.setEdad(30);
        requestDto.setDireccion("Address");
        requestDto.setTelefono("0999999999");

        responseDto = new ClienteResponseDto();
        responseDto.setClienteId(clienteId);
        responseDto.setNombre("Test User");

        cliente = Cliente.builder()
                .clienteId(clienteId)
                .persona(Persona.builder().nombre("Test User").build())
                .build();
    }

    @Test
    void crearCliente_DeberiaRetornar201() throws Exception {
        when(clienteMapper.toDomain(any(ClienteRequestDto.class))).thenReturn(cliente);
        when(crearClienteUseCase.crearCliente(any(Cliente.class))).thenReturn(cliente);
        when(clienteMapper.toResponseDto(any(Cliente.class))).thenReturn(responseDto);

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").exists());
    }

    @Test
    void obtenerCliente_DeberiaRetornar200() throws Exception {
        when(consultarClienteUseCase.obtenerClientePorId(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toResponseDto(any(Cliente.class))).thenReturn(responseDto);

        mockMvc.perform(get("/clientes/{id}", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(clienteId.toString()));
    }

    @Test
    void obtenerCliente_DeberiaRetornar404() throws Exception {
        when(consultarClienteUseCase.obtenerClientePorId(clienteId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/clientes/{id}", clienteId))
                .andExpect(status().isNotFound());
    }

    @Test
    void inactivarCliente_DeberiaRetornar200() throws Exception {
        when(inactivarClienteUseCase.inactivarCliente(clienteId)).thenReturn(cliente);
        when(clienteMapper.toResponseDto(any(Cliente.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/clientes/{id}/inactivar", clienteId))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarCliente_DeberiaRetornar200() throws Exception {
        when(clienteMapper.toDomain(any(ClienteRequestDto.class))).thenReturn(cliente);
        when(actualizarClienteUseCase.actualizarCliente(eq(clienteId), any(Cliente.class))).thenReturn(cliente);
        when(clienteMapper.toResponseDto(any(Cliente.class))).thenReturn(responseDto);

        mockMvc.perform(put("/clientes/{id}", clienteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }
}
