package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.domain.model.Cuenta;
import com.devsu.mscuentasmovimientos.domain.model.Movimiento;
import com.devsu.mscuentasmovimientos.domain.port.in.InactivarCuentaUseCase;
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
public class InactivarCuentaUseCaseImpl implements InactivarCuentaUseCase {

    private final CuentaRepositoryPort cuentaRepositoryPort;
    private final MovimientoRepositoryPort movimientoRepositoryPort;

    @Override
    @Transactional
    public Cuenta inactivarCuenta(UUID cuentaId) {
        Cuenta cuenta = cuentaRepositoryPort.buscarPorId(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con ID: " + cuentaId));

        if (!cuenta.isEstado()) {
            // Ya está inactiva, pero validamos si tiene saldo por si acaso (aunque debería ser 0)
            // Para este requerimiento, asumimos que si ya está inactiva, simplemente retornamos la cuenta
            return cuenta;
        }

        BigDecimal saldoActual = calcularSaldoActual(cuenta);

        if (saldoActual.compareTo(BigDecimal.ZERO) > 0) {
            // Crear movimiento de retiro por el saldo total
            Movimiento retiro = Movimiento.builder()
                    .movimientoId(UUID.randomUUID())
                    .fecha(LocalDateTime.now())
                    .tipoMovimiento(Movimiento.TipoMovimiento.RETIRO)
                    .valor(saldoActual)
                    .saldo(BigDecimal.ZERO)
                    .cuentaId(cuentaId)
                    .build();

            movimientoRepositoryPort.guardar(retiro);
        }

        cuenta.setEstado(false);
        return cuentaRepositoryPort.guardar(cuenta);
    }

    private BigDecimal calcularSaldoActual(Cuenta cuenta) {
        Optional<Movimiento> ultimoMovimiento = Optional.ofNullable(
                movimientoRepositoryPort.obtenerUltimoMovimientoPorCuentaId(cuenta.getCuentaId()));

        return ultimoMovimiento.map(Movimiento::getSaldo).orElse(cuenta.getSaldoInicial());
    }
}
