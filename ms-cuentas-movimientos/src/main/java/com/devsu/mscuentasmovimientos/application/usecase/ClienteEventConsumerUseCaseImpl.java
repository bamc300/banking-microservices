package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.domain.model.ClienteInfo;
import com.devsu.mscuentasmovimientos.domain.port.in.ClienteEventConsumerPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClienteEventConsumerUseCaseImpl implements ClienteEventConsumerPort {

    @Override
    public void procesarClienteCreado(ClienteInfo clienteInfo) {
        log.info("Procesando cliente creado: ID={}, Nombre={}, Identificacion={}",
                clienteInfo.getClienteId(), clienteInfo.getNombre(),
                clienteInfo.getIdentificacion());

        // Aquí podríamos implementar lógica adicional como:
        // - Almacenar información del cliente en caché
        // - Crear cuentas por defecto para nuevos clientes
        // - Enviar notificaciones

        log.info("Cliente {} procesado exitosamente", clienteInfo.getClienteId());
    }
}
