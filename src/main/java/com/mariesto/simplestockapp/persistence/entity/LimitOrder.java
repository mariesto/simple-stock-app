package com.mariesto.simplestockapp.persistence.entity;

import com.mariesto.simplestockapp.constant.OrderStatus;
import com.mariesto.simplestockapp.constant.TradeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "limit_orders", indexes = {@Index(name = "idx_user_id", columnList = "userId")})
@Getter
@Setter
public class LimitOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private String userId;

    private String stockSymbol;

    private Long quantity;

    private Long remainingQuantity;

    private Long limitPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private TradeType type;

    @CurrentTimestamp
    private Timestamp createdAt;

    @CurrentTimestamp
    private Timestamp updatedAt;

}
