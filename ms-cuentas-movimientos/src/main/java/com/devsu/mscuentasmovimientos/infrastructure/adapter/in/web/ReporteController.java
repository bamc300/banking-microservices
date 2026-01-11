package com.devsu.mscuentasmovimientos.infrastructure.adapter.in.web;

import com.devsu.mscuentasmovimientos.application.dto.ReporteRequestDto;
import com.devsu.mscuentasmovimientos.application.dto.ReporteResponseDto;
import com.devsu.mscuentasmovimientos.application.mapper.MovimientoMapper;
import com.devsu.mscuentasmovimientos.domain.model.ReporteEstadoCuenta;
import com.devsu.mscuentasmovimientos.domain.port.in.GenerarReporteUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "API de generación de reportes")
public class ReporteController {

    private final GenerarReporteUseCase generarReporteUseCase;
    private final MovimientoMapper movimientoMapper;

    @GetMapping
    @Operation(summary = "Generar reporte de estado de cuenta")
    public ResponseEntity<ReporteResponseDto> generarReporte(@Valid ReporteRequestDto requestDto) {
        var reporte = generarReporteUseCase.generarReporte(requestDto.getClienteId(),
                requestDto.getFechaInicio(), requestDto.getFechaFin());

        var responseDto = new ReporteResponseDto();
        responseDto.setClienteId(reporte.getClienteId());
        responseDto.setNombreCliente(reporte.getNombreCliente());

        var cuentasReporte = reporte.getCuentas().stream().map(cuenta -> {
            var cuentaDto = new ReporteResponseDto.CuentaReporteDto();
            cuentaDto.setNumeroCuenta(cuenta.getNumeroCuenta());
            cuentaDto.setTipoCuenta(cuenta.getTipoCuenta());
            cuentaDto.setEstado(cuenta.isEstado());

            var movimientosDto = cuenta.getMovimientos().stream()
                    .map(movimientoMapper::toResponseDto).collect(Collectors.toList());

            cuentaDto.setMovimientos(movimientosDto);
            return cuentaDto;
        }).collect(Collectors.toList());

        responseDto.setCuentas(cuentasReporte);

        return ResponseEntity.ok(responseDto);
    }
}
