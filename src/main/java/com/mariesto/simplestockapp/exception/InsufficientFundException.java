package com.mariesto.simplestockapp.exception;

public class InsufficientFundException extends RuntimeException {
    public InsufficientFundException() {
        super("Insufficient fund");
    }
}
