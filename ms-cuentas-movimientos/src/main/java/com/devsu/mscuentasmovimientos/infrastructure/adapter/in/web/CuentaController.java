package com.devsu.mscuentasmovimientos.infrastructure.adapter.in.web;

import com.devsu.mscuentasmovimientos.application.dto.CuentaRequestDto;
import com.devsu.mscuentasmovimientos.application.dto.CuentaResponseDto;
import com.devsu.mscuentasmovimientos.application.mapper.CuentaMapper;
import com.devsu.mscuentasmovimientos.application.dto.CuentaSaldoDto;
import com.devsu.mscuentasmovimientos.domain.port.in.CrearCuentaUseCase;
import com.devsu.mscuentasmovimientos.domain.port.in.ConsultarCuentasPorClienteUseCase;
import com.devsu.mscuentasmovimientos.domain.port.in.InactivarCuentasPorClienteUseCase;
import com.devsu.mscuentasmovimientos.domain.port.in.InactivarCuentaUseCase;
import com.devsu.mscuentasmovimientos.domain.port.in.ValidarInactivacionClienteUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
@Tag(name = "Cuentas", description = "API de gestión de cuentas")
public class CuentaController {

    private final CrearCuentaUseCase crearCuentaUseCase;
    private final ValidarInactivacionClienteUseCase validarInactivacionClienteUseCase;
    private final ConsultarCuentasPorClienteUseCase consultarCuentasPorClienteUseCase;
    private final InactivarCuentasPorClienteUseCase inactivarCuentasPorClienteUseCase;
    private final InactivarCuentaUseCase inactivarCuentaUseCase;
    private final CuentaMapper cuentaMapper;

    @PostMapping
    @Operation(summary = "Crear nueva cuenta")
    public ResponseEntity<CuentaResponseDto> crearCuenta(
            @Valid @RequestBody CuentaRequestDto requestDto) {
        var cuenta = cuentaMapper.toDomain(requestDto);
        var cuentaCreada = crearCuentaUseCase.crearCuenta(cuenta);
        var responseDto = cuentaMapper.toResponseDto(cuentaCreada);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Obtener cuentas por cliente")
    public ResponseEntity<List<CuentaResponseDto>> obtenerCuentasPorCliente(
            @PathVariable UUID clienteId) {
        return ResponseEntity
                .ok(consultarCuentasPorClienteUseCase.obtenerCuentasPorCliente(clienteId));
    }

    @PatchMapping("/cliente/{clienteId}/inactivar")
    @Operation(summary = "Inactivar todas las cuentas de un cliente")
    public ResponseEntity<Void> inactivarCuentasPorCliente(@PathVariable UUID clienteId) {
        inactivarCuentasPorClienteUseCase.inactivarCuentasPorCliente(clienteId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{cuentaId}/inactivar")
    @Operation(summary = "Inactivar cuenta (retirando saldo restante)")
    public ResponseEntity<CuentaResponseDto> inactivarCuenta(@PathVariable UUID cuentaId) {
        var cuentaInactivada = inactivarCuentaUseCase.inactivarCuenta(cuentaId);
        return ResponseEntity.ok(cuentaMapper.toResponseDto(cuentaInactivada));
    }

    @GetMapping("/validacion-inactivacion/{clienteId}")
    @Operation(summary = "Obtener cuentas con saldo positivo que impiden inactivación")
    public ResponseEntity<List<CuentaSaldoDto>> validarInactivacion(@PathVariable UUID clienteId) {
        List<CuentaSaldoDto> cuentasConSaldo =
                validarInactivacionClienteUseCase.obtenerCuentasConSaldo(clienteId);
        return ResponseEntity.ok(cuentasConSaldo);
    }
}
