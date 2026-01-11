package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.application.dto.CuentaSaldoDto;
import com.devsu.mscuentasmovimientos.domain.model.Cuenta;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidarInactivacionClienteUseCaseTest {

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @Mock
    private MovimientoRepositoryPort movimientoRepositoryPort;

    @InjectMocks
    private ValidarInactivacionClienteUseCaseImpl validarInactivacionClienteUseCase;

    private UUID clienteId;
    private Cuenta cuenta;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        cuenta = Cuenta.builder()
                .cuentaId(UUID.randomUUID())
                .numeroCuenta("123456")
                .saldoInicial(BigDecimal.valueOf(100))
                .clienteId(clienteId)
                .build();
    }

    @Test
    void obtenerCuentasConSaldo_DeberiaRetornarListaDeCuentasConSaldo() {
        when(cuentaRepositoryPort.buscarPorClienteId(clienteId)).thenReturn(Collections.singletonList(cuenta));
        when(movimientoRepositoryPort.obtenerUltimoMovimientoPorCuentaId(cuenta.getCuentaId())).thenReturn(null);

        List<CuentaSaldoDto> resultado = validarInactivacionClienteUseCase.obtenerCuentasConSaldo(clienteId);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals("123456", resultado.get(0).getNumeroCuenta());
        assertEquals(BigDecimal.valueOf(100), resultado.get(0).getSaldoActual());
    }
}
