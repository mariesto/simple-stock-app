package com.mariesto.simplestockapp.model;

import com.mariesto.simplestockapp.constant.OrderType;
import com.mariesto.simplestockapp.constant.TradeType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeRequest {
    private String userId;

    private String stockSymbol;

    private TradeType tradeType;

    private Long quantity;

    private OrderType orderType;

    private Long limitPrice;
}
