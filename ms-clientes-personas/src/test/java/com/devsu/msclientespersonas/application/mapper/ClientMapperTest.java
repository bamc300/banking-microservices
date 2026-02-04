package com.devsu.msclientespersonas.application.mapper;

import com.devsu.msclientespersonas.application.dto.ClientRequestDto;
import com.devsu.msclientespersonas.application.dto.ClientResponseDto;
import com.devsu.msclientespersonas.domain.model.Client;
import com.devsu.msclientespersonas.domain.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClientMapperTest {

    private ClientMapper clientMapper;

    @BeforeEach
    void setUp() {
        clientMapper = new ClientMapper();
    }

    @Test
    void toDomain_ShouldMapCorrectly() {
        ClientRequestDto requestDto = ClientRequestDto.builder()
                .name("Test Name")
                .gender("M")
                .age(30)
                .identification("1234567890")
                .address("Test Address")
                .phone("0987654321")
                .password("password")
                .build();

        Client client = clientMapper.toDomain(requestDto);

        assertNotNull(client);
        assertNotNull(client.getClientId());
        assertTrue(client.isStatus());
        assertNotNull(client.getPassword()); 
        assertNotEquals("password", client.getPassword());

        assertEquals("Test Name", client.getPerson().getName());
        assertEquals("M", client.getPerson().getGender());
        assertEquals(30, client.getPerson().getAge());
        assertEquals("1234567890", client.getPerson().getIdentification());
        assertEquals("Test Address", client.getPerson().getAddress());
        assertEquals("0987654321", client.getPerson().getPhone());
    }

    @Test
    void toResponseDto_ShouldMapCorrectly() {
        UUID clientId = UUID.randomUUID();
        Person person = Person.builder()
                .name("Test Name")
                .gender("M")
                .age(30)
                .identification("1234567890")
                .address("Test Address")
                .phone("0987654321")
                .build();
        Client client = Client.builder()
                .clientId(clientId)
                .status(true)
                .person(person)
                .build();

        ClientResponseDto responseDto = clientMapper.toResponseDto(client);

        assertNotNull(responseDto);
        assertEquals(clientId, responseDto.getClientId());
        assertEquals("Test Name", responseDto.getName());
        assertEquals("M", responseDto.getGender());
        assertEquals(30, responseDto.getAge());
        assertEquals("1234567890", responseDto.getIdentification());
        assertEquals("Test Address", responseDto.getAddress());
        assertEquals("0987654321", responseDto.getPhone());
        assertTrue(responseDto.isStatus());
    }
}
