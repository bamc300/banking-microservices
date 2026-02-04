package com.devsu.mscuentasmovimientos.infrastructure.adapter.in.web;

import com.devsu.mscuentasmovimientos.application.dto.ReportRequestDto;
import com.devsu.mscuentasmovimientos.application.dto.ReportResponseDto;
import com.devsu.mscuentasmovimientos.application.mapper.ReportMapper;
import com.devsu.mscuentasmovimientos.domain.port.in.GenerateReportUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Report Generation API")
public class ReportController {

    private final GenerateReportUseCase generateReportUseCase;
    private final ReportMapper reportMapper;

    @GetMapping
    @Operation(summary = "Generate account statement report")
    public ResponseEntity<ReportResponseDto> generateReport(@Valid ReportRequestDto requestDto) {
        var report = generateReportUseCase.generateReport(
                requestDto.getStartDate(),
                requestDto.getEndDate(),
                requestDto.getClientId());

        return ResponseEntity.ok(reportMapper.toResponseDto(report));
    }
}
