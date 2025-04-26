package com.mariesto.simplestockapp.exception;

public class InsufficientStockException extends BusinessValidationException {
    public InsufficientStockException(String stockSymbol) {
        super("Insufficient stock quantity of: " + stockSymbol);
    }
}
