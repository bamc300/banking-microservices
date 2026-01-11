package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.domain.model.Cuenta;
import com.devsu.mscuentasmovimientos.domain.port.in.InactivarCuentasPorClienteUseCase;
import com.devsu.mscuentasmovimientos.domain.port.out.CuentaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InactivarCuentasPorClienteUseCaseImpl implements InactivarCuentasPorClienteUseCase {

    private final CuentaRepositoryPort cuentaRepositoryPort;

    @Override
    @Transactional
    public void inactivarCuentasPorCliente(UUID clienteId) {
        List<Cuenta> cuentas = cuentaRepositoryPort.buscarPorClienteId(clienteId);
        cuentas.forEach(cuenta -> {
            cuenta.setEstado(false);
            cuentaRepositoryPort.guardar(cuenta);
        });
    }
}
