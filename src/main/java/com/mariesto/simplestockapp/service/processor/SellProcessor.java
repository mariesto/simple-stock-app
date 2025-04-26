package com.mariesto.simplestockapp.service.processor;

import com.mariesto.simplestockapp.constant.OrderStatus;
import com.mariesto.simplestockapp.constant.OrderType;
import com.mariesto.simplestockapp.constant.TradeType;
import com.mariesto.simplestockapp.exception.InsufficientFundException;
import com.mariesto.simplestockapp.exception.InsufficientHoldingException;
import com.mariesto.simplestockapp.exception.NotFoundException;
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
public class SellProcessor implements TradeProcessor {

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final UserStockRepository userStockRepository;
    private final LimitOrderRepository limitOrderRepository;
    private final FractionPriceValidator fractionPriceValidator;
    private final TradeRepository tradeRepository;

    @Override
    public void execute(User user, Stock stock, TradeRequest request) {
        if (request.getOrderType().equals(OrderType.LIMIT)) {
            processLimitOrder(user, stock, request);
        } else {
            processMarketOrder(user, stock, request);
        }
    }

    private void processMarketOrder(User user, Stock stock, TradeRequest request) {
        UserStock holdings = userStockRepository.findById(new UserStockId(user.getId(), stock.getSymbol()))
                .orElseThrow(() -> new NotFoundException(stock.getSymbol()));

        validateHoldings(holdings, request.getQuantity());
        Long totalCost = stock.getCurrentPrice() * request.getQuantity();
        updateUserBalance(user, totalCost);
        updateStockQuantity(stock, request.getQuantity());
        updateUserHoldings(holdings, request.getQuantity());
        createNewTrade(request, user, totalCost);
    }

    private void processLimitOrder(User user, Stock stock, TradeRequest request) {
        if (request.getLimitPrice() == null) {
            throw new InsufficientFundException();
        }

        Long totalCost = request.getQuantity() * request.getLimitPrice();

        fractionPriceValidator.validate(BigDecimal.valueOf(stock.getCurrentPrice()), BigDecimal.valueOf(totalCost));

        LimitOrder limitOrder = new LimitOrder();
        limitOrder.setOrderId(UUID.randomUUID().toString());
        limitOrder.setUserId(user.getUserId());
        limitOrder.setOrderStatus(OrderStatus.PENDING);
        limitOrder.setLimitPrice(request.getLimitPrice());
        limitOrder.setQuantity(request.getQuantity());
        limitOrder.setType(TradeType.SELL);
        limitOrder.setStockSymbol(stock.getSymbol());
        limitOrderRepository.save(limitOrder);
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

    private void validateHoldings(UserStock holdings, Long quantity) {
        if (holdings.getQuantity() < quantity) {
            throw new InsufficientHoldingException();
        }
    }

    private void updateUserBalance(User user, Long totalCost) {
        user.setBalance(user.getBalance().add(BigDecimal.valueOf(totalCost)));
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
