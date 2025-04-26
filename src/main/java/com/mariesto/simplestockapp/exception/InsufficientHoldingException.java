package com.mariesto.simplestockapp.exception;

public class InsufficientHoldingException extends BusinessValidationException {
    public InsufficientHoldingException() {
        super("Insufficient holding");
    }
}
