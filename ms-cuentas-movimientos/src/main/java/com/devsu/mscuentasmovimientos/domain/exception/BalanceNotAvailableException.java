package com.devsu.mscuentasmovimientos.domain.exception;

public class BalanceNotAvailableException extends RuntimeException {
    public BalanceNotAvailableException(String message) {
        super(message);
    }
}
