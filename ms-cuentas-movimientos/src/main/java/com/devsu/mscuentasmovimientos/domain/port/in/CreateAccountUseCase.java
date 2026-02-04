package com.devsu.mscuentasmovimientos.domain.port.in;

import com.devsu.mscuentasmovimientos.domain.model.Account;

public interface CreateAccountUseCase {
    Account createAccount(Account account);
}
