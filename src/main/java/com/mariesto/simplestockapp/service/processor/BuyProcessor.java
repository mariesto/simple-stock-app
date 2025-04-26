package com.mariesto.simplestockapp.service.processor;

import com.mariesto.simplestockapp.constant.OrderStatus;
import com.mariesto.simplestockapp.constant.OrderType;
import com.mariesto.simplestockapp.constant.TradeType;
import com.mariesto.simplestockapp.exception.InsufficientFundException;
import com.mariesto.simplestockapp.exception.InsufficientStockException;
import com.mariesto.simplestockapp.exception.InvalidRequestException;
import com.mariesto.simplestockapp.model.TradeRequest;
import com.mariesto.simplestockapp.persistence.entity.*;
import com.mariesto.simplestockapp.persistence.repository.*;
import com.mariesto.simplestockapp.service.FractionPriceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BuyProcessor implements TradeProcessor {

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final UserStockRepository userStockRepository;
    private final FractionPriceValidator fractionPriceValidator;
    private final LimitOrderRepository limitOrderRepository;
    private final TradeRepository tradeRepository;

    @Override
    public void execute(User user, Stock stock, TradeRequest request) {
        if (request.getOrderType().equals(OrderType.LIMIT)) {
            processLimitOrder(user, stock, request);
        } else {
            processMarketOrder(user, stock, request);
        }
    }

    private void processLimitOrder(User user, Stock stock, TradeRequest request) {
        if (request.getLimitPrice() == null) {
            throw new InvalidRequestException("Request must have a limit price");
        }

        Long totalCost = request.getQuantity() * request.getLimitPrice();

        fractionPriceValidator.validate(BigDecimal.valueOf(stock.getCurrentPrice()), BigDecimal.valueOf(totalCost));

        LimitOrder limitOrder = new LimitOrder();
        limitOrder.setOrderId(UUID.randomUUID().toString());
        limitOrder.setUserId(user.getUserId());
        limitOrder.setOrderStatus(OrderStatus.PENDING);
        limitOrder.setLimitPrice(request.getLimitPrice());
        limitOrder.setQuantity(request.getQuantity());
        limitOrder.setType(TradeType.BUY);
        limitOrder.setStockSymbol(stock.getSymbol());
        limitOrderRepository.save(limitOrder);
    }

    private void processMarketOrder(User user, Stock stock, TradeRequest request) {
        Long totalCost = stock.getCurrentPrice() * request.getQuantity();
        validateBalance(user, totalCost);
        validateStockQuantity(stock, request.getQuantity());

        updateUserBalance(user, totalCost);
        updateStockQuantity(stock, request.getQuantity());
        updateUserHoldings(user, stock, request.getQuantity());
        createNewTrade(request, user, totalCost);
    }

    private void createNewTrade(TradeRequest request, User user, Long totalCost) {
        Trade trade = new Trade();
        trade.setType(request.getTradeType());
        trade.setPrice(totalCost);
        trade.setUserId(user.getUserId());
        trade.setStockSymbol(request.getStockSymbol());
        trade.setQuantity(request.getQuantity());
        tradeRepository.save(trade);
    }

    private void validateBalance(User user, Long totalCost) {
        if (user.getBalance().compareTo(BigDecimal.valueOf(totalCost)) < 0) {
            throw new InsufficientFundException();
        }
    }

    private void validateStockQuantity(Stock stock, Long quantity) {
        if (stock.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException(stock.getSymbol());
        }
    }

    private void updateUserBalance(User user, Long totalCost) {
        user.setBalance(user.getBalance().subtract(BigDecimal.valueOf(totalCost)));
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
