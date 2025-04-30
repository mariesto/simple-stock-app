package com.mariesto.simplestockapp.persistence.repository;

import com.mariesto.simplestockapp.constant.OrderType;
import com.mariesto.simplestockapp.constant.TradeType;
import com.mariesto.simplestockapp.persistence.entity.LimitOrder;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LimitOrderRepository extends JpaRepository<LimitOrder, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM LimitOrder o WHERE o.orderStatus IN ('PENDING', 'PARTIALLY_FILLED') ORDER BY o.updatedAt ASC, o.createdAt ASC")
    List<LimitOrder> findPendingOrdersWithLock(Pageable pageable);


    @Query("SELECT o FROM LimitOrder o WHERE o.stockSymbol = :stockSymbol AND o.type = :tradeType AND o.orderStatus IN ('PENDING', 'PARTIALLY_FILLED') ORDER BY o.limitPrice ASC, o.createdAt ASC")
    List<LimitOrder> findMatchingOrdersByType(@Param("stockSymbol") String stockSymbol, @Param("tradeType") TradeType tradeType);

}
