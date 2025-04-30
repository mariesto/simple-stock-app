package com.mariesto.simplestockapp.service.scheduler;

import com.mariesto.simplestockapp.constant.OrderStatus;
import com.mariesto.simplestockapp.constant.TradeType;
import com.mariesto.simplestockapp.persistence.entity.LimitOrder;
import com.mariesto.simplestockapp.persistence.entity.Trade;
import com.mariesto.simplestockapp.persistence.repository.LimitOrderRepository;
import com.mariesto.simplestockapp.persistence.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMatchingService {

    private static final Logger log = LogManager.getLogger(OrderMatchingService.class);
    private final LimitOrderRepository limitOrderRepository;
    private final TradeRepository tradeRepository;

    @Transactional
    @Scheduled(fixedRate = 10_000)
    public void runMatchingOrders() {
        PageRequest pageRequest = PageRequest.of(0, 500);
        List<LimitOrder> limitOrders = limitOrderRepository.findPendingOrdersWithLock(pageRequest);
        if (limitOrders.isEmpty()) {
            log.info("No limit orders to match at this cycle.");
            return;
        }
        limitOrders.forEach(this::matchLimitOrder);
    }

    protected void matchLimitOrder(LimitOrder limitOrder) {
        TradeType oppositeOrderType = limitOrder.getType().equals(TradeType.BUY) ? TradeType.SELL : TradeType.BUY;
        List<LimitOrder> limitOrders = limitOrderRepository.findMatchingOrdersByType(limitOrder.getStockSymbol(), oppositeOrderType);

        if (limitOrders.isEmpty()) {
            log.info("No liquidity for the order to be proceed");
            return;
        }

        for (LimitOrder oppositeOrder : limitOrders) {
            Long remainingQuantity = limitOrder.getRemainingQuantity();
            if (remainingQuantity == 0) {
                log.info("Matching order skipped due to insufficient quantity");
                break;
            }

            long matchQty = Math.min(remainingQuantity, oppositeOrder.getRemainingQuantity());
            Long matchedPrice = oppositeOrder.getLimitPrice();

            Trade trade = new Trade();
            trade.setBuyOrderId(limitOrder.getType() == TradeType.BUY ? limitOrder.getOrderId() : oppositeOrder.getOrderId());
            trade.setSellOrderId(limitOrder.getType() == TradeType.SELL ? limitOrder.getOrderId() : oppositeOrder.getOrderId());
            trade.setStockSymbol(limitOrder.getStockSymbol());
            trade.setQuantity(matchQty);
            trade.setPrice(matchedPrice);
            trade.setType(limitOrder.getType());
            trade.setUserId(limitOrder.getUserId());
            tradeRepository.save(trade);

            oppositeOrder.setRemainingQuantity(oppositeOrder.getRemainingQuantity() - matchQty);
            oppositeOrder.setOrderStatus(oppositeOrder.getRemainingQuantity() == 0 ? OrderStatus.FILLED : OrderStatus.PARTIALLY_FILLED);
            limitOrderRepository.save(oppositeOrder);

            remainingQuantity -= matchQty;

            limitOrder.setRemainingQuantity(remainingQuantity);
            limitOrder.setOrderStatus(remainingQuantity == 0 ? OrderStatus.FILLED : OrderStatus.PARTIALLY_FILLED);
            limitOrderRepository.save(limitOrder);
        }
    }

}
