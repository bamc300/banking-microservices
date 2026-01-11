package com.devsu.msclientespersonas.application.mapper;

import com.devsu.msclientespersonas.application.dto.ClienteRequestDto;
import com.devsu.msclientespersonas.application.dto.ClienteResponseDto;
import com.devsu.msclientespersonas.domain.model.Cliente;
import com.devsu.msclientespersonas.domain.model.Persona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClienteMapperTest {

    private ClienteMapper clienteMapper;

    @BeforeEach
    void setUp() {
        clienteMapper = new ClienteMapper();
    }

    @Test
    void toDomain_DeberiaMapearCorrectamente() {
        ClienteRequestDto requestDto = ClienteRequestDto.builder()
                .nombre("Test Name")
                .genero("M")
                .edad(30)
                .identificacion("1234567890")
                .direccion("Test Address")
                .telefono("0987654321")
                .contrasena("password")
                .build();

        Cliente cliente = clienteMapper.toDomain(requestDto);

        assertNotNull(cliente);
        assertNotNull(cliente.getClienteId());
        assertTrue(cliente.isEstado());
        assertNotNull(cliente.getContrasena()); // Encoded password
        assertNotEquals("password", cliente.getContrasena()); // Should be encoded

        assertEquals("Test Name", cliente.getPersona().getNombre());
        assertEquals("M", cliente.getPersona().getGenero());
        assertEquals(30, cliente.getPersona().getEdad());
        assertEquals("1234567890", cliente.getPersona().getIdentificacion());
        assertEquals("Test Address", cliente.getPersona().getDireccion());
        assertEquals("0987654321", cliente.getPersona().getTelefono());
    }

    @Test
    void toResponseDto_DeberiaMapearCorrectamente() {
        UUID clienteId = UUID.randomUUID();
        Persona persona = Persona.builder()
                .nombre("Test Name")
                .genero("M")
                .edad(30)
                .identificacion("1234567890")
                .direccion("Test Address")
                .telefono("0987654321")
                .build();
        Cliente cliente = Cliente.builder()
                .clienteId(clienteId)
                .estado(true)
                .persona(persona)
                .build();

        ClienteResponseDto responseDto = clienteMapper.toResponseDto(cliente);

        assertNotNull(responseDto);
        assertEquals(clienteId, responseDto.getClienteId());
        assertEquals("Test Name", responseDto.getNombre());
        assertEquals("M", responseDto.getGenero());
        assertEquals(30, responseDto.getEdad());
        assertEquals("1234567890", responseDto.getIdentificacion());
        assertEquals("Test Address", responseDto.getDireccion());
        assertEquals("0987654321", responseDto.getTelefono());
        assertTrue(responseDto.isEstado());
    }
}
