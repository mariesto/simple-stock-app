package com.mariesto.simplestockapp.model;

import com.mariesto.simplestockapp.constant.TradeType;

import java.sql.Timestamp;

public record UserTradeResponse(String stockSymbol, Long quantity, Long price, TradeType tradeType,
                                Timestamp timestamp) {
}
