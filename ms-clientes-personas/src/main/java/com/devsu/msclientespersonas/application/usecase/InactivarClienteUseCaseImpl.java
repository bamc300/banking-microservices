package com.devsu.msclientespersonas.application.usecase;

import com.devsu.msclientespersonas.application.dto.CuentaSaldoDto;
import com.devsu.msclientespersonas.domain.exception.ClienteNoEncontradoException;
import com.devsu.msclientespersonas.domain.exception.CuentasConSaldoException;
import com.devsu.msclientespersonas.domain.model.Cliente;
import com.devsu.msclientespersonas.domain.port.in.InactivarClienteUseCase;
import com.devsu.msclientespersonas.domain.port.out.ClienteRepositoryPort;
import com.devsu.msclientespersonas.domain.port.out.CuentaExternalServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InactivarClienteUseCaseImpl implements InactivarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final CuentaExternalServicePort cuentaExternalServicePort;

    @Override
    @Transactional
    public Cliente inactivarCliente(UUID clienteId) {
        Cliente cliente = clienteRepositoryPort.buscarPorId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException(
                        "Cliente no encontrado con ID: " + clienteId));

        List<CuentaSaldoDto> cuentas = cuentaExternalServicePort.obtenerCuentasConSaldo(clienteId);

        boolean tieneSaldoPendiente =
                cuentas.stream().anyMatch(c -> c.getSaldoActual().compareTo(BigDecimal.ZERO) > 0);

        if (tieneSaldoPendiente) {
            throw new CuentasConSaldoException(cuentas);
        }

        cliente.setEstado(false);
        Cliente clienteGuardado = clienteRepositoryPort.guardar(cliente);

        cuentaExternalServicePort.inactivarCuentas(clienteId);

        return clienteGuardado;
    }
}
