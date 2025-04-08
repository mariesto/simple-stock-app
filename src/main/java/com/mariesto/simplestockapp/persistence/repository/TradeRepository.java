package com.mariesto.simplestockapp.persistence.repository;

import com.mariesto.simplestockapp.persistence.entity.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    Page<Trade> findAllByUserId(@Param("userId") String userId, Pageable pageable);

}
