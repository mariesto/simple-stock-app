package com.mariesto.simplestockapp.service;

import com.github.javafaker.Faker;
import com.mariesto.simplestockapp.persistence.entity.Stock;
import com.mariesto.simplestockapp.persistence.entity.User;
import com.mariesto.simplestockapp.persistence.repository.StockRepository;
import com.mariesto.simplestockapp.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StockRepository stockRepository;

    @Override
    public void run(String... args) {

        stockRepository.deleteAll();
        userRepository.deleteAll();

        Faker faker = new Faker();

        for (int i = 1; i <= 100; i++) {
            User user = new User();
            user.setUserId("user-" + i);
            user.setBalance(BigDecimal.valueOf(1_000_000));
            userRepository.save(user);
        }

        for (int i = 1; i <= 10; i++) {
            double price = faker.number().randomDouble(4, 100, 500);

            Stock stock = new Stock();
            stock.setSymbol("STCK-" + i);
            stock.setCurrentPrice(BigDecimal.valueOf(price));
            stock.setAvailableQuantity(2_000_000L);

            stockRepository.save(stock);
        }
    }
}
