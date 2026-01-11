package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.domain.model.Cuenta;
import com.devsu.mscuentasmovimientos.domain.port.out.CuentaRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InactivarCuentasPorClienteUseCaseTest {

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @InjectMocks
    private InactivarCuentasPorClienteUseCaseImpl inactivarCuentasPorClienteUseCase;

    @Test
    void inactivarCuentasPorCliente_DeberiaInactivarTodasLasCuentas() {
        UUID clienteId = UUID.randomUUID();
        Cuenta cuenta = Cuenta.builder().estado(true).build();
        when(cuentaRepositoryPort.buscarPorClienteId(clienteId)).thenReturn(Collections.singletonList(cuenta));

        inactivarCuentasPorClienteUseCase.inactivarCuentasPorCliente(clienteId);

        verify(cuentaRepositoryPort).guardar(argThat(c -> !c.isEstado()));
    }
}
