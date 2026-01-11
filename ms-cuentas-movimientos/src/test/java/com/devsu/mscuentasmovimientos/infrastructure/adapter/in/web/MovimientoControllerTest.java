package com.devsu.mscuentasmovimientos.infrastructure.adapter.in.web;

import com.devsu.mscuentasmovimientos.application.dto.MovimientoRequestDto;
import com.devsu.mscuentasmovimientos.application.dto.MovimientoResponseDto;
import com.devsu.mscuentasmovimientos.application.mapper.MovimientoMapper;
import com.devsu.mscuentasmovimientos.domain.model.Movimiento;
import com.devsu.mscuentasmovimientos.domain.port.in.RegistrarMovimientoUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovimientoController.class)
class MovimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrarMovimientoUseCase registrarMovimientoUseCase;

    @MockBean
    private MovimientoMapper movimientoMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private MovimientoRequestDto requestDto;
    private Movimiento movimiento;

    @BeforeEach
    void setUp() {
        requestDto = new MovimientoRequestDto();
        requestDto.setTipoMovimiento("DEPOSITO");
        requestDto.setValor(BigDecimal.valueOf(100));
        requestDto.setCuentaId(UUID.randomUUID());

        movimiento = Movimiento.builder().build();
    }

    @Test
    void registrarMovimiento_DeberiaRetornar201() throws Exception {
        when(movimientoMapper.toDomain(any(MovimientoRequestDto.class))).thenReturn(movimiento);
        when(registrarMovimientoUseCase.registrarMovimiento(any(Movimiento.class))).thenReturn(movimiento);
        when(movimientoMapper.toResponseDto(any(Movimiento.class))).thenReturn(new MovimientoResponseDto());

        mockMvc.perform(post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());
    }
}
