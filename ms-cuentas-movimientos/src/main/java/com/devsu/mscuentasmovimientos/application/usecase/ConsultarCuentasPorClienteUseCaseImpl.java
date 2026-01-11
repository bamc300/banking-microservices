package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.application.dto.CuentaResponseDto;
import com.devsu.mscuentasmovimientos.application.mapper.CuentaMapper;
import com.devsu.mscuentasmovimientos.domain.model.Cuenta;
import com.devsu.mscuentasmovimientos.domain.model.Movimiento;
import com.devsu.mscuentasmovimientos.domain.port.in.ConsultarCuentasPorClienteUseCase;
import com.devsu.mscuentasmovimientos.domain.port.out.CuentaRepositoryPort;
import com.devsu.mscuentasmovimientos.domain.port.out.MovimientoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultarCuentasPorClienteUseCaseImpl implements ConsultarCuentasPorClienteUseCase {

    private final CuentaRepositoryPort cuentaRepositoryPort;
    private final MovimientoRepositoryPort movimientoRepositoryPort;
    private final CuentaMapper cuentaMapper;

    @Override
    public List<CuentaResponseDto> obtenerCuentasPorCliente(UUID clienteId) {
        List<Cuenta> cuentas = cuentaRepositoryPort.buscarPorClienteId(clienteId);
        return cuentas.stream().map(cuenta -> {
            CuentaResponseDto dto = cuentaMapper.toResponseDto(cuenta);
            dto.setSaldoActual(calcularSaldoActual(cuenta));
            return dto;
        }).collect(Collectors.toList());
    }

    private BigDecimal calcularSaldoActual(Cuenta cuenta) {
        Optional<Movimiento> ultimoMovimiento = Optional.ofNullable(
                movimientoRepositoryPort.obtenerUltimoMovimientoPorCuentaId(cuenta.getCuentaId()));

        return ultimoMovimiento.map(Movimiento::getSaldo).orElse(cuenta.getSaldoInicial());
    }
}
