package com.devsu.mscuentasmovimientos.infrastructure.adapter.in.web;

import com.devsu.mscuentasmovimientos.application.dto.CuentaRequestDto;
import com.devsu.mscuentasmovimientos.application.dto.CuentaResponseDto;
import com.devsu.mscuentasmovimientos.application.mapper.CuentaMapper;
import com.devsu.mscuentasmovimientos.domain.model.Cuenta;
import com.devsu.mscuentasmovimientos.domain.port.in.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CrearCuentaUseCase crearCuentaUseCase;

    @MockBean
    private ValidarInactivacionClienteUseCase validarInactivacionClienteUseCase;

    @MockBean
    private ConsultarCuentasPorClienteUseCase consultarCuentasPorClienteUseCase;

    @MockBean
    private InactivarCuentasPorClienteUseCase inactivarCuentasPorClienteUseCase;

    @MockBean
    private InactivarCuentaUseCase inactivarCuentaUseCase;

    @MockBean
    private CuentaMapper cuentaMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private CuentaRequestDto requestDto;
    private CuentaResponseDto responseDto;
    private Cuenta cuenta;
    private UUID cuentaId;

    @BeforeEach
    void setUp() {
        cuentaId = UUID.randomUUID();
        requestDto = new CuentaRequestDto();
        requestDto.setNumeroCuenta("123456");
        requestDto.setTipoCuenta(Cuenta.TipoCuenta.AHORROS);
        requestDto.setClienteId(UUID.randomUUID());
        requestDto.setSaldoInicial(java.math.BigDecimal.TEN);

        cuenta = Cuenta.builder().cuentaId(cuentaId).numeroCuenta("123456")
                .tipoCuenta(Cuenta.TipoCuenta.AHORROS).build();
        responseDto = new CuentaResponseDto();
        responseDto.setNumeroCuenta("123456");
        responseDto.setTipoCuenta("AHORROS");
    }

    @Test
    void crearCuenta_DeberiaRetornar201() throws Exception {
        when(cuentaMapper.toDomain(any(CuentaRequestDto.class))).thenReturn(cuenta);
        when(crearCuentaUseCase.crearCuenta(any(Cuenta.class))).thenReturn(cuenta);
        when(cuentaMapper.toResponseDto(any(Cuenta.class))).thenReturn(responseDto);

        mockMvc.perform(post("/cuentas").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isCreated());
    }

    @Test
    void inactivarCuenta_DeberiaRetornar200() throws Exception {
        when(inactivarCuentaUseCase.inactivarCuenta(cuentaId)).thenReturn(cuenta);
        when(cuentaMapper.toResponseDto(any(Cuenta.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/cuentas/{id}/inactivar", cuentaId)).andExpect(status().isOk());
    }
}
