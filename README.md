# Simple Stock App

This is a simple project which simulate a buy and sell a stock. 
The main purpose is to simulate a transactional process with high throughput.
- Using `@Transactional` annotation
- `Optimistic Locking` was chosen by adding `@Version` annotation in the entity class
- Using simple lock mechanism of `@Lock(LockModeType.PESSIMISTIC_WRITE)` in order to acquire an exclusive lock on the database row to prevent other transactions from reading or writing to it. Using `PESSIMISTIC_WRITE` ensures that once a transaction reads the user's data for update, no other transaction can modify it until the first one commits.
- Having pagination for fetching all user trades
- Connection Pool was enabled by default using HikariCP

The main target is to be able to serve `100 requests / second`.

### Tech Stack
- Spring Boot (3.4.3)
- Java (21)
- Postgres DB
- Docker
- K6 (Load Testing)

### How to Use
- Run the docker-compose to spin up a Postgres DB
- Run the spring boot project (automatically trigger the data initializer)

