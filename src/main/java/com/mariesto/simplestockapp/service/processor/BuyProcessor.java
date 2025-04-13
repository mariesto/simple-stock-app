package com.mariesto.simplestockapp.service.processor;

import com.mariesto.simplestockapp.exception.InsufficientFundException;
import com.mariesto.simplestockapp.exception.InsufficientStockException;
import com.mariesto.simplestockapp.model.TradeRequest;
import com.mariesto.simplestockapp.persistence.entity.Stock;
import com.mariesto.simplestockapp.persistence.entity.User;
import com.mariesto.simplestockapp.persistence.entity.UserStock;
import com.mariesto.simplestockapp.persistence.entity.UserStockId;
import com.mariesto.simplestockapp.persistence.repository.StockRepository;
import com.mariesto.simplestockapp.persistence.repository.UserRepository;
import com.mariesto.simplestockapp.persistence.repository.UserStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BuyProcessor implements TradeProcessor {

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final UserStockRepository userStockRepository;

    @Override
    public void execute(User user, Stock stock, BigDecimal totalCost, TradeRequest request) {
        validateBalance(user, totalCost);
        validateStockQuantity(stock, request.getQuantity());

        updateUserBalance(user, totalCost.negate());
        updateStockQuantity(stock, request.getQuantity());
        updateUserHoldings(user, stock, request.getQuantity());
    }

    private void validateBalance(User user, BigDecimal totalCost) {
        if (user.getBalance().compareTo(totalCost) < 0) {
            throw new InsufficientFundException();
        }
    }

    private void validateStockQuantity(Stock stock, Long quantity) {
        if (stock.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException(stock.getSymbol());
        }
    }

    private void updateUserBalance(User user, BigDecimal totalCost) {
        user.setBalance(user.getBalance().subtract(totalCost));
        userRepository.save(user);
    }

    private void updateUserHoldings(User user, Stock stock, Long quantity) {
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
    }

    private void updateStockQuantity(Stock stock, Long quantity) {
        Long remainingQuantity = stock.getAvailableQuantity() - quantity;
        stock.setAvailableQuantity(remainingQuantity);
        stockRepository.save(stock);
    }
}
