package com.devsu.mscuentasmovimientos.application.usecase;

import com.devsu.mscuentasmovimientos.application.mapper.MovementMapper;
import com.devsu.mscuentasmovimientos.domain.model.Account;
import com.devsu.mscuentasmovimientos.domain.model.AccountStatementReport;
import com.devsu.mscuentasmovimientos.domain.model.Movement;
import com.devsu.mscuentasmovimientos.domain.port.in.GenerateReportUseCase;
import com.devsu.mscuentasmovimientos.domain.port.out.AccountRepositoryPort;
import com.devsu.mscuentasmovimientos.domain.port.out.MovementRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenerateReportUseCaseImpl implements GenerateReportUseCase {

    private final AccountRepositoryPort accountRepositoryPort;
    private final MovementRepositoryPort movementRepositoryPort;
    // mapper not needed if we return domain model, but wait, AccountStatementReport has nested Movement objects?
    // AccountStatementReport has List<AccountReport> which has List<Movement>.
    // So we don't need MovementMapper here.

    @Override
    public AccountStatementReport generateReport(LocalDate startDate, LocalDate endDate, UUID clientId) {
        List<Account> accounts = accountRepositoryPort.findByClientId(clientId);

        if (accounts.isEmpty()) {
            throw new IllegalArgumentException("No accounts found for client");
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<AccountStatementReport.AccountReport> accountReports = accounts.stream().map(account -> {
            List<Movement> movements = movementRepositoryPort.findByAccountIdAndDateBetween(
                    account.getAccountId(), startDateTime, endDateTime);

            return AccountStatementReport.AccountReport.builder()
                    .accountNumber(account.getAccountNumber())
                    .accountType(account.getAccountType().name())
                    .status(account.isStatus())
                    .movements(movements)
                    .build();
        }).collect(Collectors.toList());

        return AccountStatementReport.builder()
                .clientId(clientId)
                // .clientName("Unknown") // Client name is not in Account DB, maybe need to fetch from Client Service? 
                // For now leaving null or "Unknown" as per original implementation which didn't fetch it either?
                // Original code: ReportResponseDto had clientName. Where did it come from?
                // Original code didn't set clientName in the snippet I read.
                .accounts(accountReports)
                .build();
    }
}
