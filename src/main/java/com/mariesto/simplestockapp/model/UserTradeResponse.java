package com.mariesto.simplestockapp.model;

import com.mariesto.simplestockapp.constant.TradeType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record UserTradeResponse(String stockSymbol, Long quantity, BigDecimal price, TradeType tradeType, Timestamp timestamp) {
}
