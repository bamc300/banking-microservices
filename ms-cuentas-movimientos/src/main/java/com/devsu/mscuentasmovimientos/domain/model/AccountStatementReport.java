package com.devsu.mscuentasmovimientos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementReport {
    private UUID clientId;
    private String clientName;
    private List<AccountReport> accounts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountReport {
        private String accountNumber;
        private String accountType;
        private boolean status;
        private List<Movement> movements;
    }
}
