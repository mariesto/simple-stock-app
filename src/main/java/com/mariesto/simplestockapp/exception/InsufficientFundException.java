package com.mariesto.simplestockapp.exception;

public class InsufficientFundException extends BusinessValidationException {
    public InsufficientFundException() {
        super("Insufficient fund");
    }
}
