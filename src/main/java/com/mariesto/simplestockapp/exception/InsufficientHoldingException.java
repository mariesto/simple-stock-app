package com.mariesto.simplestockapp.exception;

public class InsufficientHoldingException extends RuntimeException {
    public InsufficientHoldingException() {
        super("Insufficient holding");
    }
}
