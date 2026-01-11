package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.domain.model.Cuenta;
import com.devsu.mscuentasmovimientos.domain.port.in.CrearCuentaUseCase;
import com.devsu.mscuentasmovimientos.domain.port.out.CuentaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrearCuentaUseCaseImpl implements CrearCuentaUseCase {

    private final CuentaRepositoryPort cuentaRepositoryPort;

    @Override
    public Cuenta crearCuenta(Cuenta cuenta) {
        if (cuentaRepositoryPort.existePorNumeroCuenta(cuenta.getNumeroCuenta())) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese número");
        }

        return cuentaRepositoryPort.guardar(cuenta);
    }
}
