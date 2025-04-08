package com.mariesto.simplestockapp.model;

import com.mariesto.simplestockapp.constant.TradeType;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class TradeRequest {
    private String userId;

    private String stockSymbol;

    private TradeType tradeType;

    private Long quantity;
}
