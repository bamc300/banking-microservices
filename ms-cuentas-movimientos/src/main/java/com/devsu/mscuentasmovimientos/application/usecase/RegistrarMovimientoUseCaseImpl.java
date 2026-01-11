package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.domain.exception.SaldoNoDisponibleException;
import com.devsu.mscuentasmovimientos.domain.model.Cuenta;
import com.devsu.mscuentasmovimientos.domain.model.Movimiento;
import com.devsu.mscuentasmovimientos.domain.port.in.RegistrarMovimientoUseCase;
import com.devsu.mscuentasmovimientos.domain.port.out.CuentaRepositoryPort;
import com.devsu.mscuentasmovimientos.domain.port.out.MovimientoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrarMovimientoUseCaseImpl implements RegistrarMovimientoUseCase {

    private final CuentaRepositoryPort cuentaRepositoryPort;
    private final MovimientoRepositoryPort movimientoRepositoryPort;

    @Override
    @Transactional
    public Movimiento registrarMovimiento(Movimiento movimiento) {
        Cuenta cuenta = cuentaRepositoryPort.buscarPorId(movimiento.getCuentaId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

        if (!cuenta.isEstado()) {
            throw new IllegalArgumentException("La cuenta está inactiva");
        }

        BigDecimal saldoActual = calcularSaldoActual(cuenta);
        BigDecimal nuevoSaldo = calcularNuevoSaldo(saldoActual, movimiento.getValor(),
                movimiento.getTipoMovimiento());

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoNoDisponibleException("Saldo no disponible");
        }

        movimiento.setFecha(LocalDateTime.now());
        movimiento.setSaldo(nuevoSaldo);

        return movimientoRepositoryPort.guardar(movimiento);
    }

    private BigDecimal calcularSaldoActual(Cuenta cuenta) {
        Optional<Movimiento> ultimoMovimiento = Optional
                .ofNullable(movimientoRepositoryPort.obtenerUltimoMovimientoPorCuentaId(cuenta.getCuentaId()));

        return ultimoMovimiento.map(Movimiento::getSaldo).orElse(cuenta.getSaldoInicial());
    }

    private BigDecimal calcularNuevoSaldo(BigDecimal saldoActual, BigDecimal valor,
            Movimiento.TipoMovimiento tipoMovimiento) {
        return switch (tipoMovimiento) {
            case DEPOSITO -> saldoActual.add(valor);
            case RETIRO -> saldoActual.subtract(valor);
        };
    }
}
