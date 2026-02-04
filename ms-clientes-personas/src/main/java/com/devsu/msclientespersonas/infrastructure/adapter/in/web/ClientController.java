package com.devsu.msclientespersonas.infrastructure.adapter.in.web;

import com.devsu.msclientespersonas.application.dto.ClientRequestDto;
import com.devsu.msclientespersonas.application.dto.ClientResponseDto;
import com.devsu.msclientespersonas.application.mapper.ClientMapper;
import com.devsu.msclientespersonas.domain.port.in.UpdateClientUseCase;
import com.devsu.msclientespersonas.domain.port.in.GetClientUseCase;
import com.devsu.msclientespersonas.domain.port.in.CreateClientUseCase;
import com.devsu.msclientespersonas.domain.port.in.DeleteClientUseCase;
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
@RequestMapping("/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Client Management API")
public class ClientController {

    private final CreateClientUseCase createClientUseCase;
    private final GetClientUseCase getClientUseCase;
    private final DeleteClientUseCase deleteClientUseCase;
    private final UpdateClientUseCase updateClientUseCase;
    private final ClientMapper clientMapper;

    @PostMapping
    @Operation(summary = "Create new client")
    public ResponseEntity<ClientResponseDto> createClient(
            @Valid @RequestBody ClientRequestDto requestDto) {
        var client = clientMapper.toDomain(requestDto);
        var createdClient = createClientUseCase.createClient(client);
        var responseDto = clientMapper.toResponseDto(createdClient);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/batch")
    @Operation(summary = "Create multiple clients")
    public ResponseEntity<List<ClientResponseDto>> createClients(
            @RequestBody List<@Valid ClientRequestDto> requestDtos) {
        var clients = requestDtos.stream().map(clientMapper::toDomain).collect(Collectors.toList());
        var createdClients = createClientUseCase.createClients(clients);
        var responseDtos = createdClients.stream()
                .map(clientMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDtos);
    }

    @GetMapping
    @Operation(summary = "Get all clients")
    public ResponseEntity<List<ClientResponseDto>> getAllClients() {
        var clients = getClientUseCase.getAllClients();
        var responseDtos = clients.stream()
                .map(clientMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/{clientId}")
    @Operation(summary = "Get client by ID")
    public ResponseEntity<ClientResponseDto> getClient(@PathVariable UUID clientId) {
        return getClientUseCase.getClientById(clientId)
                .map(client -> ResponseEntity.ok(clientMapper.toResponseDto(client)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{clientId}")
    @Operation(summary = "Update client information")
    public ResponseEntity<ClientResponseDto> updateClient(@PathVariable UUID clientId,
            @Valid @RequestBody ClientRequestDto requestDto) {
        var clientToUpdate = clientMapper.toDomain(requestDto);
        var updatedClient = updateClientUseCase.updateClient(clientId, clientToUpdate);
        return ResponseEntity.ok(clientMapper.toResponseDto(updatedClient));
    }

    @DeleteMapping("/{clientId}")
    @Operation(summary = "Delete client (soft delete, validating 0 balance in accounts)")
    public ResponseEntity<ClientResponseDto> deleteClient(@PathVariable UUID clientId) {
        var deletedClient = deleteClientUseCase.deleteClient(clientId);
        return ResponseEntity.ok(clientMapper.toResponseDto(deletedClient));
    }
}
