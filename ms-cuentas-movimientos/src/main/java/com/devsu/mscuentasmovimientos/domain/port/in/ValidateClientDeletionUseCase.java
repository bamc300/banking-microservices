package com.devsu.mscuentasmovimientos.domain.port.in;

import com.devsu.mscuentasmovimientos.application.dto.AccountBalanceDto;
import java.util.List;
import java.util.UUID;

public interface ValidateClientDeletionUseCase {
    List<AccountBalanceDto> getAccountsWithBalance(UUID clientId);
}
