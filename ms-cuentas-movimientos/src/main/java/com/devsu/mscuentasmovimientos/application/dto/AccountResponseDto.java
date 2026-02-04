package com.devsu.mscuentasmovimientos.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AccountResponseDto {
    private UUID accountId;
    private String accountNumber;
    private String accountType;
    private BigDecimal initialBalance;
    private BigDecimal currentBalance;
    private boolean status;
    private UUID clientId;
}
