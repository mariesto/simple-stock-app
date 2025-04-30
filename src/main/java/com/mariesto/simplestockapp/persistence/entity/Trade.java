package com.mariesto.simplestockapp.persistence.entity;

import com.mariesto.simplestockapp.constant.TradeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "trades", indexes = {@Index(name = "idx_user_id", columnList = "userId")})
@Getter
@Setter
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String buyOrderId;

    private String sellOrderId;

    private String userId;

    private String stockSymbol;

    private Long quantity;

    private Long price;

    @Enumerated(EnumType.STRING)
    private TradeType type;

    @CurrentTimestamp
    private Timestamp timestamp;
}
