package com.mariesto.simplestockapp.persistence.repository;

import com.mariesto.simplestockapp.persistence.entity.UserStock;
import com.mariesto.simplestockapp.persistence.entity.UserStockId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStockRepository extends JpaRepository<UserStock, UserStockId> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT us FROM UserStock us WHERE us.user.userId = :userId AND us.stock.symbol = :symbol")
    Optional<UserStock> findByUserAndStockWithLock(
            @Param("userId") String userId,
            @Param("symbol") String symbol
    );

    @Query("SELECT us FROM UserStock us WHERE us.user.userId = :userId")
    List<UserStock> findByUser(@Param("userId") String userId);
}
