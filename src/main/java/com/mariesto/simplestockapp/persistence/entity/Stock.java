package com.mariesto.simplestockapp.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "stocks")
@Getter
@Setter
public class Stock {

    @Id
    @Column(length = 20)
    private String symbol;

    private Long currentPrice;

    private Long availableQuantity;

    @Version
    private Long version;
}
