package com.devsu.msclientespersonas.infrastructure.adapter.in.web;

import com.devsu.msclientespersonas.application.dto.AccountBalanceDto;
import com.devsu.msclientespersonas.domain.exception.ClientNotFoundException;
import com.devsu.msclientespersonas.domain.exception.AccountsWithBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/test");
    }

    @Test
    void handleClientNotFound_ShouldReturnNotFound() {
        ClientNotFoundException ex = new ClientNotFoundException("Client not found");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleClientNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not Found", response.getBody().get("error"));
        assertEquals("Client not found", response.getBody().get("message"));
    }

    @Test
    void handleAccountsWithBalance_ShouldReturnOk() {
        List<AccountBalanceDto> accounts = List.of(new AccountBalanceDto("123", BigDecimal.TEN));
        AccountsWithBalanceException ex = new AccountsWithBalanceException(accounts);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAccountsWithBalance(ex);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cannot inactivate client because they have accounts with balance.", response.getBody().get("error"));
        assertEquals(accounts, response.getBody().get("pendingAccounts"));
    }

    @Test
    void handleIllegalArgument_ShouldReturnBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("Invalid argument", response.getBody().get("message"));
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerError() {
        Exception ex = new Exception("Internal error");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGlobalException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody().get("error"));
        assertEquals("/test", response.getBody().get("path"));
    }
}
