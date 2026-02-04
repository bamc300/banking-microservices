package com.devsu.mscuentasmovimientos.application.mapper;

import com.devsu.mscuentasmovimientos.application.dto.ReportResponseDto;
import com.devsu.mscuentasmovimientos.domain.model.AccountStatementReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReportMapper {

    private final MovementMapper movementMapper;

    public ReportResponseDto toResponseDto(AccountStatementReport report) {
        ReportResponseDto dto = new ReportResponseDto();
        dto.setClientId(report.getClientId());
        dto.setClientName(report.getClientName());
        dto.setAccounts(report.getAccounts().stream().map(accountReport -> {
            ReportResponseDto.AccountReportDto accountDto = new ReportResponseDto.AccountReportDto();
            accountDto.setAccountNumber(accountReport.getAccountNumber());
            accountDto.setAccountType(accountReport.getAccountType());
            accountDto.setStatus(accountReport.isStatus());
            accountDto.setMovements(accountReport.getMovements().stream()
                    .map(movementMapper::toResponseDto)
                    .collect(Collectors.toList()));
            return accountDto;
        }).collect(Collectors.toList()));
        return dto;
    }
}
