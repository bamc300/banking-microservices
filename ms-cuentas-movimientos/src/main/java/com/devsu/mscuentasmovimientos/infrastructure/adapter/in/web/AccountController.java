package com.devsu.mscuentasmovimientos.infrastructure.adapter.in.web;

import com.devsu.mscuentasmovimientos.application.dto.AccountBalanceDto;
import com.devsu.mscuentasmovimientos.application.dto.AccountRequestDto;
import com.devsu.mscuentasmovimientos.application.dto.AccountResponseDto;
import com.devsu.mscuentasmovimientos.application.mapper.AccountMapper;
import com.devsu.mscuentasmovimientos.domain.port.in.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account Management API")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountsByClientUseCase getAccountsByClientUseCase;
    private final DeleteAccountsByClientUseCase deleteAccountsByClientUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final ValidateClientDeletionUseCase validateClientDeletionUseCase;
    private final AccountMapper accountMapper;

    @PostMapping
    @Operation(summary = "Create new account")
    public ResponseEntity<AccountResponseDto> createAccount(@Valid @RequestBody AccountRequestDto requestDto) {
        var account = accountMapper.toDomain(requestDto);
        var createdAccount = createAccountUseCase.createAccount(account);
        var responseDto = accountMapper.toResponseDto(createdAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Get accounts by client ID")
    public ResponseEntity<List<AccountResponseDto>> getAccountsByClient(@PathVariable UUID clientId) {
        var accounts = getAccountsByClientUseCase.getAccountsByClient(clientId);
        var responseDtos = accounts.stream()
                .map(accountMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    @PatchMapping("/client/{clientId}/inactivate")
    @Operation(summary = "Inactivate all accounts for a client")
    public ResponseEntity<Void> deleteAccountsByClient(@PathVariable UUID clientId) {
        deleteAccountsByClientUseCase.deleteAccountsByClient(clientId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{accountId}/inactivate")
    @Operation(summary = "Inactivate account (withdrawing remaining balance)")
    public ResponseEntity<AccountResponseDto> deleteAccount(@PathVariable UUID accountId) {
        var deletedAccount = deleteAccountUseCase.deleteAccount(accountId);
        return ResponseEntity.ok(accountMapper.toResponseDto(deletedAccount));
    }

    @GetMapping("/validation-inactivation/{clientId}")
    @Operation(summary = "Get accounts with positive balance preventing inactivation")
    public ResponseEntity<List<AccountBalanceDto>> validateInactivation(@PathVariable UUID clientId) {
        List<AccountBalanceDto> accountsWithBalance = validateClientDeletionUseCase.getAccountsWithBalance(clientId);
        return ResponseEntity.ok(accountsWithBalance);
    }
}
