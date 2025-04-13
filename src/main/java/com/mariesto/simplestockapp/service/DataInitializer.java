package com.mariesto.simplestockapp.service;

import com.github.javafaker.Faker;
import com.mariesto.simplestockapp.persistence.entity.Stock;
import com.mariesto.simplestockapp.persistence.entity.User;
import com.mariesto.simplestockapp.persistence.entity.UserStock;
import com.mariesto.simplestockapp.persistence.repository.StockRepository;
import com.mariesto.simplestockapp.persistence.repository.UserRepository;
import com.mariesto.simplestockapp.persistence.repository.UserStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@Transactional
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final UserStockRepository userStockRepository;

    @Override
    public void run(String... args) {

        stockRepository.deleteAll();
        userRepository.deleteAll();

        Faker faker = new Faker();

        for (int i = 1; i <= 10; i++) {
            double price = faker.number().randomDouble(4, 100, 500);

            Stock stock = new Stock();
            stock.setSymbol("STCK-" + i);
            stock.setCurrentPrice(BigDecimal.valueOf(price));
            stock.setAvailableQuantity(2_000_000L);

            stockRepository.save(stock);
        }

        List<Stock> stocks = stockRepository.findAll();

        for (int i = 1; i <= 100; i++) {
            User user = new User();
            user.setUserId("user-" + i);
            user.setBalance(BigDecimal.valueOf(1_000_000));
            userRepository.save(user);

            int stockIndex = (i - 1) / (100 / 10);
            Stock randomStock = stocks.get(stockIndex);

            UserStock userStock = new UserStock();
            userStock.setUser(user);
            userStock.setQuantity((long) faker.number().numberBetween(100, 500));
            userStock.setStock(randomStock);
            userStockRepository.save(userStock);
        }
    }
}
