package com.devsu.msclientespersonas.infrastructure.adapter.in.web;

import com.devsu.msclientespersonas.application.dto.CuentaSaldoDto;
import com.devsu.msclientespersonas.domain.exception.ClienteNoEncontradoException;
import com.devsu.msclientespersonas.domain.exception.CuentasConSaldoException;
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
    void handleClienteNoEncontrado_DeberiaRetornarNotFound() {
        ClienteNoEncontradoException ex = new ClienteNoEncontradoException("Cliente no encontrado");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleClienteNoEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not Found", response.getBody().get("error"));
        assertEquals("Cliente no encontrado", response.getBody().get("message"));
    }

    @Test
    void handleCuentasConSaldo_DeberiaRetornarOk() {
        List<CuentaSaldoDto> cuentas = List.of(new CuentaSaldoDto("123", BigDecimal.TEN));
        CuentasConSaldoException ex = new CuentasConSaldoException(cuentas);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleCuentasConSaldo(ex);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("No se puede inactivar el cliente porque cuenta con saldo.", response.getBody().get("error"));
        assertEquals(cuentas, response.getBody().get("cuentasPendientes"));
    }

    @Test
    void handleIllegalArgument_DeberiaRetornarBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Argumento inválido");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("Argumento inválido", response.getBody().get("message"));
    }

    @Test
    void handleGlobalException_DeberiaRetornarInternalServerError() {
        Exception ex = new Exception("Error interno");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGlobalException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error interno del servidor", response.getBody().get("error"));
        assertEquals("/test", response.getBody().get("path"));
    }
}
