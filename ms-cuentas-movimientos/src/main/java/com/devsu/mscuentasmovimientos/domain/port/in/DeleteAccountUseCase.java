package com.devsu.mscuentasmovimientos.domain.port.in;

import com.devsu.mscuentasmovimientos.domain.model.Account;
import java.util.UUID;

public interface DeleteAccountUseCase {
    Account deleteAccount(UUID accountId);
}
