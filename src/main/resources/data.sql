INSERT INTO users (id, user_id, balance, version)
VALUES (1, 'user1', 100000.0000, 0),
       (2, 'user2', 50000.0000, 0),
       (3, 'user3', 75000.0000, 0);

INSERT INTO stocks (symbol, current_price, available_quantity, version)
VALUES ('AAPL', 189.9500, 200, 0),
       ('GOOGL', 138.4200, 500, 0),
       ('TSLA', 260.1300, 400, 0),
       ('MSFT', 338.4300, 250, 0),
       ('AMZN', 128.0800, 500, 0);

INSERT INTO user_stocks (user_id, stock_symbol, quantity, version)
VALUES (1, 'AAPL', 100, 0),
       (1, 'GOOGL', 50, 0),
       (2, 'TSLA', 200, 0),
       (3, 'MSFT', 150, 0),
       (3, 'AMZN', 75, 0);

INSERT INTO trades (user_id, stock_symbol, quantity, price, type, timestamp)
VALUES ('user1', 'AAPL', 50, 185.5000, 'BUY', NOW() - INTERVAL '2 DAY'),
       ('user1', 'GOOGL', 30, 135.0000, 'BUY', NOW() - INTERVAL '1 DAY'),
       ('user2', 'TSLA', 100, 255.0000, 'BUY', NOW() - INTERVAL '3 HOUR'),
       ('user3', 'MSFT', 75, 335.0000, 'BUY', NOW() - INTERVAL '5 HOUR'),
       ('user1', 'AAPL', 25, 190.0000, 'SELL', NOW() - INTERVAL '1 HOUR');