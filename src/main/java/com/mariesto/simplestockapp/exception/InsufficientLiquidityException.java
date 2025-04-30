package com.mariesto.simplestockapp.exception;

public class InsufficientLiquidityException extends BusinessValidationException {
    public InsufficientLiquidityException() {
        super("Not enough orders to fulfill the request.");
    }
}
