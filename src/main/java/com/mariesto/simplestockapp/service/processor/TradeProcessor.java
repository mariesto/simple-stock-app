package com.mariesto.simplestockapp.service.processor;

import com.mariesto.simplestockapp.model.TradeRequest;
import com.mariesto.simplestockapp.persistence.entity.Stock;
import com.mariesto.simplestockapp.persistence.entity.User;

public interface TradeProcessor {
    void execute(User user, Stock stock, TradeRequest request);
}
