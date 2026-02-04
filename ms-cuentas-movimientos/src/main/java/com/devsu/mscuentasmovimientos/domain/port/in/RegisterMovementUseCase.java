package com.devsu.mscuentasmovimientos.domain.port.in;

import com.devsu.mscuentasmovimientos.domain.model.Movement;

public interface RegisterMovementUseCase {
    Movement registerMovement(Movement movement);
}
