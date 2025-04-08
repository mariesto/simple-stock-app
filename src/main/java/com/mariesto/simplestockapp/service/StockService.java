package com.mariesto.simplestockapp.service;

import com.mariesto.simplestockapp.constant.TradeType;
import com.mariesto.simplestockapp.model.TradeRequest;
import com.mariesto.simplestockapp.model.UserStockResponse;
import com.mariesto.simplestockapp.model.UserTradeResponse;
import com.mariesto.simplestockapp.persistence.entity.*;
import com.mariesto.simplestockapp.persistence.repository.StockRepository;
import com.mariesto.simplestockapp.persistence.repository.TradeRepository;
import com.mariesto.simplestockapp.persistence.repository.UserRepository;
import com.mariesto.simplestockapp.persistence.repository.UserStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;
    private final UserStockRepository userStockRepository;

    @Transactional
    public void executeTrade(TradeRequest request) {
        var user = userRepository.findUserByUserIdWithLock(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getQuantity() <= 0) {
            throw new RuntimeException("Stock quantity is zero");
        }

        var stock = stockRepository.findWithLock(request.getStockSymbol())
                .orElseThrow(() -> new RuntimeException("Stock symbol not found"));

        BigDecimal totalCost = stock.getCurrentPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        if (request.getTradeType() == TradeType.BUY) {
            processBuy(user, stock, totalCost, request.getQuantity());
        } else {
            processSell(user, stock, totalCost, request.getQuantity());
        }

        Trade trade = new Trade();
        trade.setType(request.getTradeType());
        trade.setPrice(totalCost);
        trade.setUserId(user.getUserId());
        trade.setStockSymbol(request.getStockSymbol());
        trade.setQuantity(request.getQuantity());
        tradeRepository.save(trade);
    }

    private void processBuy(User user, Stock stock, BigDecimal totalCost, Long quantity) {
        if (user.getBalance().compareTo(totalCost) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        if (stock.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Stock quantity is not enough");
        }

        user.setBalance(user.getBalance().subtract(totalCost));
        userRepository.save(user);

        UserStockId id = new UserStockId(user.getId(), stock.getSymbol());

        UserStock holding = userStockRepository.findById(id)
                .orElseGet(() -> {
                    UserStock newHolding = new UserStock();
                    newHolding.setStock(stock);
                    newHolding.setUser(user);
                    newHolding.setQuantity(quantity);
                    userStockRepository.save(newHolding);
                    return newHolding;
                });

        holding.setQuantity(holding.getQuantity() + quantity);
        userStockRepository.save(holding);

        Long remainingQuantity = stock.getAvailableQuantity() - quantity;
        stock.setAvailableQuantity(remainingQuantity);
        stockRepository.save(stock);
    }

    private void processSell(User user, Stock stock, BigDecimal totalCost, Long quantity) {
        UserStock holdings = userStockRepository.findById(new UserStockId(user.getId(), stock.getSymbol()))
                .orElseThrow(() -> new RuntimeException("No holdings to sell"));

        if (holdings.getQuantity() < quantity) {
            throw new RuntimeException("Not enough shares to sell");
        }

        user.setBalance(user.getBalance().add(totalCost));
        userRepository.save(user);

        holdings.setQuantity(holdings.getQuantity() - quantity);
        userStockRepository.save(holdings);

        Long remainingQuantity = stock.getAvailableQuantity() + quantity;
        stock.setAvailableQuantity(remainingQuantity);
        stockRepository.save(stock);
    }

    public List<UserStockResponse> fetchUserStocks(final String userId) {
        List<UserStock> userStocks = userStockRepository.findByUser(userId);
        return userStocks.stream().map(userStock -> new UserStockResponse(userStock.getStock().getSymbol(), userStock.getQuantity(), BigDecimal.valueOf(userStock.getQuantity()).multiply(userStock.getStock().getCurrentPrice()), userStock.getStock().getCurrentPrice())).collect(Collectors.toList());
    }

    public List<UserTradeResponse> fetchUserTrades(final String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Trade> trades = tradeRepository.findAllByUserId(userId, pageable);
        return trades.stream().map(trade -> new UserTradeResponse(trade.getStockSymbol(), trade.getQuantity(), trade.getPrice(), trade.getType(), trade.getTimestamp())).collect(Collectors.toList());
    }
}
