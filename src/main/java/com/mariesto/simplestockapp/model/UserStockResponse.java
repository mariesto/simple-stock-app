package com.mariesto.simplestockapp.model;

import java.math.BigDecimal;

public record UserStockResponse(String stockSymbol, Long quantity, BigDecimal totalPrice, BigDecimal currentPrice) {
}

