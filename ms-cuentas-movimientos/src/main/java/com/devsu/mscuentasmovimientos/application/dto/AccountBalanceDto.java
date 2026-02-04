package com.devsu.mscuentasmovimientos.application.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountBalanceDto {
    private String accountNumber;
    private BigDecimal currentBalance;
}
