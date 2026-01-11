package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.application.dto.CuentaSaldoDto;
import com.devsu.mscuentasmovimientos.domain.model.Cuenta;
import com.devsu.mscuentasmovimientos.domain.model.Movimiento;
import com.devsu.mscuentasmovimientos.domain.port.in.ValidarInactivacionClienteUseCase;
import com.devsu.mscuentasmovimientos.domain.port.out.CuentaRepositoryPort;
import com.devsu.mscuentasmovimientos.domain.port.out.MovimientoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ValidarInactivacionClienteUseCaseImpl implements ValidarInactivacionClienteUseCase {

    private final CuentaRepositoryPort cuentaRepositoryPort;
    private final MovimientoRepositoryPort movimientoRepositoryPort;

    @Override
    public List<CuentaSaldoDto> obtenerCuentasConSaldo(UUID clienteId) {
        List<Cuenta> cuentas = cuentaRepositoryPort.buscarPorClienteId(clienteId);
        List<CuentaSaldoDto> cuentasConSaldo = new ArrayList<>();

        for (Cuenta cuenta : cuentas) {
            BigDecimal saldoActual = calcularSaldoActual(cuenta);
            // Devolvemos todas las cuentas con su saldo actual, independientemente de si es 0 o
            // mayor
            cuentasConSaldo.add(CuentaSaldoDto.builder().numeroCuenta(cuenta.getNumeroCuenta())
                    .saldoActual(saldoActual).build());
        }

        return cuentasConSaldo;
    }

    private BigDecimal calcularSaldoActual(Cuenta cuenta) {
        Optional<Movimiento> ultimoMovimiento = Optional.ofNullable(
                movimientoRepositoryPort.obtenerUltimoMovimientoPorCuentaId(cuenta.getCuentaId()));

        return ultimoMovimiento.map(Movimiento::getSaldo).orElse(cuenta.getSaldoInicial());
    }
}
