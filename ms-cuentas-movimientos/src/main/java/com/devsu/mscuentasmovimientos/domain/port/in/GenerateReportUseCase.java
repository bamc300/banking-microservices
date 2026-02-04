package com.devsu.mscuentasmovimientos.domain.port.in;

import com.devsu.mscuentasmovimientos.domain.model.AccountStatementReport;
import java.time.LocalDate;
import java.util.UUID;

public interface GenerateReportUseCase {
    AccountStatementReport generateReport(LocalDate startDate, LocalDate endDate, UUID clientId);
}
