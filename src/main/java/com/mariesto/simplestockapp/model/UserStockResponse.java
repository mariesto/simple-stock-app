package com.mariesto.simplestockapp.model;

public record UserStockResponse(String stockSymbol, Long quantity, Long totalPrice, Long currentPrice) {
}

