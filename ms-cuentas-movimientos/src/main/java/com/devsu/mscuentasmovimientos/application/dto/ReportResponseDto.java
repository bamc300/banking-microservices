package com.devsu.mscuentasmovimientos.application.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ReportResponseDto {
    private UUID clientId;
    private String clientName;
    private List<AccountReportDto> accounts;

    @Data
    public static class AccountReportDto {
        private String accountNumber;
        private String accountType;
        private boolean status;
        private List<MovementResponseDto> movements;
    }
}
