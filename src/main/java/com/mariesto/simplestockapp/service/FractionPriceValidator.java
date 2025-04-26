package com.mariesto.simplestockapp.service;

import com.mariesto.simplestockapp.exception.InvalidFractionException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FractionPriceValidator {

    public void validate(BigDecimal currentPrice, BigDecimal totalCost) {
        // should reject decimal totalCost
        if (totalCost.scale() > 0) {
            throw new InvalidFractionException("Price must be a whole number.");
        }

        int fractionPrice = getFractionPrice(currentPrice);
        BigDecimal decimalFractionPrice = BigDecimal.valueOf(fractionPrice);

        if (totalCost.remainder(decimalFractionPrice).compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidFractionException("Invalid fraction totalCost. Price should be in range of : " + decimalFractionPrice);
        }
    }

    private int getFractionPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.valueOf(200)) < 0) {
            return 1;
        } else if (price.compareTo(BigDecimal.valueOf(500)) <= 0) {
            return 2;
        } else if (price.compareTo(BigDecimal.valueOf(2000)) <= 0) {
            return 5;
        } else if (price.compareTo(BigDecimal.valueOf(5000)) <= 0) {
            return 10;
        } else {
            return 25;
        }
    }

}
