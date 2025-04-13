package com.mariesto.simplestockapp.service;

import com.mariesto.simplestockapp.constant.TradeType;
import com.mariesto.simplestockapp.service.processor.BuyProcessor;
import com.mariesto.simplestockapp.service.processor.SellProcessor;
import com.mariesto.simplestockapp.service.processor.TradeProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeExecutor {

    private final BuyProcessor buyProcessor;
    private final SellProcessor sellProcessor;

    public TradeProcessor getTradeProcessor(TradeType tradeType) {
        return switch (tradeType) {
            case BUY -> buyProcessor;
            case SELL -> sellProcessor;
        };
    }
}
