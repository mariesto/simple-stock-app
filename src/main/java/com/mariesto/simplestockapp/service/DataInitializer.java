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
import java.util.Random;
import java.util.UUID;

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
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setUserId(UUID.randomUUID().toString());
            user.setBalance(BigDecimal.valueOf(100000));
            user.setVersion(0L);
            userRepository.save(user);
        }

        for (int i = 0; i < 10; i++) {
            int availableQuantity = faker.number().numberBetween(100, 500);
            String stockSymbol = generateRandomStockSymbol(random);
            double price = faker.number().randomDouble(4, 100, 500);

            Stock stock = new Stock();
            stock.setSymbol(stockSymbol);
            stock.setCurrentPrice(BigDecimal.valueOf(price));
            stock.setAvailableQuantity((long) availableQuantity);

            stockRepository.save(stock);
        }
    }

    private String generateRandomStockSymbol(Random random) {
        int length = random.nextBoolean() ? 3 : 4;
        StringBuilder symbolBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char letter = (char) ('A' + random.nextInt(26));
            symbolBuilder.append(letter);
        }
        return symbolBuilder.toString();
    }
}
