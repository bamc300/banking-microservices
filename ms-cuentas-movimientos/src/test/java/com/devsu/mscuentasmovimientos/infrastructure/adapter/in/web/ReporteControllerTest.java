package com.devsu.mscuentasmovimientos.infrastructure.adapter.in.web;

import com.devsu.mscuentasmovimientos.application.dto.MovimientoResponseDto;
import com.devsu.mscuentasmovimientos.application.mapper.MovimientoMapper;
import com.devsu.mscuentasmovimientos.domain.model.Cuenta;
import com.devsu.mscuentasmovimientos.domain.model.Movimiento;
import com.devsu.mscuentasmovimientos.domain.model.ReporteEstadoCuenta;
import com.devsu.mscuentasmovimientos.domain.port.in.GenerarReporteUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReporteController.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenerarReporteUseCase generarReporteUseCase;

    @MockBean
    private MovimientoMapper movimientoMapper;

    private ReporteEstadoCuenta reporte;

    @BeforeEach
    void setUp() {
        UUID clienteId = UUID.randomUUID();
        reporte = ReporteEstadoCuenta.builder()
                .clienteId(clienteId)
                .nombreCliente("Cliente Test")
                .cuentas(Collections.singletonList(ReporteEstadoCuenta.CuentaReporte.builder()
                        .numeroCuenta("123456")
                        .tipoCuenta("AHORROS")
                        .estado(true)
                        .movimientos(Collections.singletonList(Movimiento.builder().build()))
                        .build()))
                .build();
    }

    @Test
    void generarReporte_DeberiaRetornar200() throws Exception {
        when(generarReporteUseCase.generarReporte(any(), any(), any())).thenReturn(reporte);
        when(movimientoMapper.toResponseDto(any(Movimiento.class))).thenReturn(new MovimientoResponseDto());

        mockMvc.perform(get("/reportes")
                .param("clienteId", UUID.randomUUID().toString())
                .param("fechaInicio", LocalDate.now().toString())
                .param("fechaFin", LocalDate.now().toString()))
                .andExpect(status().isOk());
    }
}
