package com.devsu.msclientespersonas.infrastructure.adapter.in.web;

import com.devsu.msclientespersonas.application.dto.ClienteRequestDto;
import com.devsu.msclientespersonas.application.dto.ClienteResponseDto;
import com.devsu.msclientespersonas.application.dto.CuentaDto;
import com.devsu.msclientespersonas.application.mapper.ClienteMapper;
import com.devsu.msclientespersonas.domain.model.Cuenta;
import com.devsu.msclientespersonas.domain.port.in.CrearClienteUseCase;
import com.devsu.msclientespersonas.domain.port.in.ConsultarClienteUseCase;
import com.devsu.msclientespersonas.domain.port.in.InactivarClienteUseCase;
import com.devsu.msclientespersonas.domain.port.in.ActualizarClienteUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "API de gestión de clientes")
public class ClienteController {

  private final CrearClienteUseCase crearClienteUseCase;
  private final ConsultarClienteUseCase consultarClienteUseCase;
  private final InactivarClienteUseCase inactivarClienteUseCase;
  private final ActualizarClienteUseCase actualizarClienteUseCase;
  private final ClienteMapper clienteMapper;

  @PostMapping
  @Operation(summary = "Crear nuevo cliente")
  public ResponseEntity<ClienteResponseDto> crearCliente(
      @Valid @RequestBody ClienteRequestDto requestDto) {
    var cliente = clienteMapper.toDomain(requestDto);
    var clienteCreado = crearClienteUseCase.crearCliente(cliente);
    var responseDto = clienteMapper.toResponseDto(clienteCreado);
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  @PostMapping("/batch")
  @Operation(summary = "Crear múltiples clientes")
  public ResponseEntity<List<ClienteResponseDto>> crearClientes(
      @RequestBody List<@Valid ClienteRequestDto> requestDtos) {
    var clientes = requestDtos.stream().map(clienteMapper::toDomain).collect(Collectors.toList());
    var clientesCreados = crearClienteUseCase.crearClientes(clientes);
    var responseDtos =
        clientesCreados.stream().map(clienteMapper::toResponseDto).collect(Collectors.toList());
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDtos);
  }

  @GetMapping("/{clienteId}")
  @Operation(summary = "Obtener cliente por ID")
  public ResponseEntity<ClienteResponseDto> obtenerCliente(@PathVariable UUID clienteId) {
    return consultarClienteUseCase.obtenerClientePorId(clienteId).map(cliente -> {
      ClienteResponseDto dto = clienteMapper.toResponseDto(cliente);
      dto.setCuentas(mapCuentas(cliente.getCuentas()));
      return ResponseEntity.ok(dto);
    }).orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  @Operation(summary = "Obtener todos los clientes")
  public ResponseEntity<List<ClienteResponseDto>> obtenerTodosLosClientes() {
    var clientes = consultarClienteUseCase.obtenerTodosLosClientes();
    var responseDtos = clientes.stream().map(cliente -> {
      ClienteResponseDto dto = clienteMapper.toResponseDto(cliente);
      dto.setCuentas(mapCuentas(cliente.getCuentas()));
      return dto;
    }).collect(Collectors.toList());
    return ResponseEntity.ok(responseDtos);
  }

  @PutMapping("/{clienteId}")
  @Operation(summary = "Actualizar información del cliente")
  public ResponseEntity<ClienteResponseDto> actualizarCliente(@PathVariable UUID clienteId,
      @Valid @RequestBody ClienteRequestDto requestDto) {
    var clienteParaActualizar = clienteMapper.toDomain(requestDto);
    var clienteActualizado =
        actualizarClienteUseCase.actualizarCliente(clienteId, clienteParaActualizar);
    return ResponseEntity.ok(clienteMapper.toResponseDto(clienteActualizado));
  }

  private List<CuentaDto> mapCuentas(List<Cuenta> cuentas) {
    if (cuentas == null)
      return null;
    return cuentas.stream()
        .map(c -> CuentaDto.builder().cuentaId(c.getCuentaId()).numeroCuenta(c.getNumeroCuenta())
            .tipoCuenta(c.getTipoCuenta()).saldoInicial(c.getSaldoInicial())
            .saldoActual(c.getSaldoActual()).estado(c.isEstado()).build())
        .collect(Collectors.toList());
  }

  @PatchMapping("/{clienteId}/inactivar")
  @Operation(summary = "Inactivar cliente (validando saldo 0 en cuentas)")
  public ResponseEntity<ClienteResponseDto> inactivarCliente(@PathVariable UUID clienteId) {
    var clienteInactivado = inactivarClienteUseCase.inactivarCliente(clienteId);
    return ResponseEntity.ok(clienteMapper.toResponseDto(clienteInactivado));
  }
}

