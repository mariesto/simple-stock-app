package com.mariesto.simplestockapp.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String stockSymbol) {
        super("Insufficient stock quantity of: " + stockSymbol);
    }
}
