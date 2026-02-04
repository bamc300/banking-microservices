package com.devsu.mscuentasmovimientos.domain.port.in;

import com.devsu.mscuentasmovimientos.domain.model.Account;
import java.util.List;
import java.util.UUID;

public interface GetAccountsByClientUseCase {
    List<Account> getAccountsByClient(UUID clientId);
}
