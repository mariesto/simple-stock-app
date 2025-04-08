package com.mariesto.simplestockapp.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_stocks")
@IdClass(UserStockId.class)
@Getter
@Setter
public class UserStock {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_symbol")
    private Stock stock;

    private Long quantity;

    @Version
    private Long version;
}
