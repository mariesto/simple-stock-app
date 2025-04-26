package com.mariesto.simplestockapp.service;

import com.mariesto.simplestockapp.exception.InvalidRequestException;
import com.mariesto.simplestockapp.exception.NotFoundException;
import com.mariesto.simplestockapp.model.TradeRequest;
import com.mariesto.simplestockapp.model.UserStockResponse;
import com.mariesto.simplestockapp.model.UserTradeResponse;
import com.mariesto.simplestockapp.persistence.entity.Trade;
import com.mariesto.simplestockapp.persistence.entity.UserStock;
import com.mariesto.simplestockapp.persistence.repository.StockRepository;
import com.mariesto.simplestockapp.persistence.repository.TradeRepository;
import com.mariesto.simplestockapp.persistence.repository.UserRepository;
import com.mariesto.simplestockapp.persistence.repository.UserStockRepository;
import com.mariesto.simplestockapp.service.processor.TradeProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;
    private final UserStockRepository userStockRepository;
    private final TradeExecutor tradeExecutor;

    @Transactional
    public void executeTrade(TradeRequest request) {
        if (request.getQuantity() <= 0) {
            throw new InvalidRequestException("Stock quantity is zero");
        }

        var stock = stockRepository.findWithLock(request.getStockSymbol())
                .orElseThrow(() -> new NotFoundException(request.getStockSymbol()));

        var user = userRepository.findUserByUserIdWithLock(request.getUserId())
                .orElseThrow(() -> new NotFoundException(request.getUserId()));

        TradeProcessor tradeProcessor = tradeExecutor.getTradeProcessor(request.getTradeType());
        tradeProcessor.execute(user, stock, request);
    }

    public List<UserStockResponse> fetchUserStocks(final String userId) {
        List<UserStock> userStocks = userStockRepository.findByUser(userId);
        return userStocks.stream().map(userStock -> {
            long totalPrice = userStock.getQuantity() * userStock.getStock().getCurrentPrice();
            return new UserStockResponse(userStock.getStock().getSymbol(), userStock.getQuantity(), totalPrice, userStock.getStock().getCurrentPrice());
        }).collect(Collectors.toList());
    }

    public List<UserTradeResponse> fetchUserTrades(final String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Trade> trades = tradeRepository.findAllByUserId(userId, pageable);
        return trades.stream().map(trade -> new UserTradeResponse(trade.getStockSymbol(), trade.getQuantity(), trade.getPrice(), trade.getType(), trade.getTimestamp())).collect(Collectors.toList());
    }
}
