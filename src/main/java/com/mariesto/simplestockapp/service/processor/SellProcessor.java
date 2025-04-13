package com.mariesto.simplestockapp.service.processor;

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
public class SellProcessor implements TradeProcessor {

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final UserStockRepository userStockRepository;

    @Override
    public void execute(User user, Stock stock, BigDecimal totalCost, TradeRequest request) {

        UserStock holdings = userStockRepository.findById(new UserStockId(user.getId(), stock.getSymbol()))
                .orElseThrow(() -> new RuntimeException("No holdings to sell"));

        validateHoldings(holdings, request.getQuantity());

        updateUserBalance(user, totalCost);
        updateStockQuantity(stock, request.getQuantity());
        updateUserHoldings(holdings, request.getQuantity());
    }

    private void validateHoldings(UserStock holdings, Long quantity) {
        if (holdings.getQuantity() < quantity) {
            throw new RuntimeException("Not enough shares to sell");
        }
    }

    private void updateUserBalance(User user, BigDecimal totalCost) {
        user.setBalance(user.getBalance().add(totalCost));
        userRepository.save(user);
    }

    private void updateStockQuantity(Stock stock, Long quantity) {
        Long remainingQuantity = stock.getAvailableQuantity() + quantity;
        stock.setAvailableQuantity(remainingQuantity);
        stockRepository.save(stock);
    }

    private void updateUserHoldings(UserStock holdings, Long quantity) {
        holdings.setQuantity(holdings.getQuantity() - quantity);
        userStockRepository.save(holdings);
    }
}
