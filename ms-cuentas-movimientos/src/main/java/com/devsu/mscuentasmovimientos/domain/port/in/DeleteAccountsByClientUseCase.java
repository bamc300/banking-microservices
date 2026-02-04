package com.devsu.mscuentasmovimientos.domain.port.in;

import java.util.UUID;

public interface DeleteAccountsByClientUseCase {
    void deleteAccountsByClient(UUID clientId);
}
