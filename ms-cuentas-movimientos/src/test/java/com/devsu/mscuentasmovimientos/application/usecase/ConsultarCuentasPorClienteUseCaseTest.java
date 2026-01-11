package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.application.dto.CuentaResponseDto;
import com.devsu.mscuentasmovimientos.application.mapper.CuentaMapper;
import com.devsu.mscuentasmovimientos.domain.model.Cuenta;
import com.devsu.mscuentasmovimientos.domain.model.Movimiento;
import com.devsu.mscuentasmovimientos.domain.port.out.CuentaRepositoryPort;
import com.devsu.mscuentasmovimientos.domain.port.out.MovimientoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultarCuentasPorClienteUseCaseTest {

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @Mock
    private MovimientoRepositoryPort movimientoRepositoryPort;

    @Mock
    private CuentaMapper cuentaMapper;

    @InjectMocks
    private ConsultarCuentasPorClienteUseCaseImpl consultarCuentasPorClienteUseCase;

    private UUID clienteId;
    private Cuenta cuenta;
    private CuentaResponseDto cuentaResponseDto;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        cuenta = Cuenta.builder()
                .cuentaId(UUID.randomUUID())
                .numeroCuenta("123456")
                .saldoInicial(BigDecimal.valueOf(100))
                .clienteId(clienteId)
                .build();
        
        cuentaResponseDto = new CuentaResponseDto();
        cuentaResponseDto.setNumeroCuenta("123456");
    }

    @Test
    void obtenerCuentasPorCliente_DeberiaRetornarListaDeCuentas() {
        when(cuentaRepositoryPort.buscarPorClienteId(clienteId)).thenReturn(Collections.singletonList(cuenta));
        when(cuentaMapper.toResponseDto(cuenta)).thenReturn(cuentaResponseDto);
        when(movimientoRepositoryPort.obtenerUltimoMovimientoPorCuentaId(cuenta.getCuentaId())).thenReturn(null);

        List<CuentaResponseDto> resultado = consultarCuentasPorClienteUseCase.obtenerCuentasPorCliente(clienteId);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("123456", resultado.get(0).getNumeroCuenta());
        assertEquals(BigDecimal.valueOf(100), resultado.get(0).getSaldoActual());
    }
}
