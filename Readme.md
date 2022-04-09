# Distributed transactions across REST microservices

## Motivation
When splitting up monolith into microservices, there will be transactions that are distributed across multiple microservices.
We do not want to use other patterns like SAGA, TCC, etc. because then we have to rewrite the existing logic, but we do not have the time and money.

## Problem
- We have microservices that communicate to each other using REST.
- We have transactions that involve multiple microservices.
- We want data to be correct in our database.
- When one microservice fails to persist data, then other microservices should do rollback as well.

## Prerequisites
- Java 11
- Maven
- Docker (DB setup)

## Architecture
We have 3 services:
- order: this is the service user calls to create new order.
- product: this service contains info about different products that can be ordered
- account: this service contains info about user account balance

Each service has it's own database.

## Scenarios

### Scenario 1 (happy path)
- User creates new order
- Order service creates new order
- Order service asks product service to remove one item
- Order service asks account service to take money from user account

### Scenario 2 (failure)
- User creates new order
- Order service creates new order
- Order service asks product service to remove one item
- Order service asks account service to take money from user account
  - User does not have enough money
- Previous changes have to be rolled back:
  - Remove created order
  - Set back removed product

## Run example
- Start databases: `docker-compose up -d`
- Start applications
- Run requests

### Requests
```
# Pass (scenario 1)
curl -XPOST 'http://localhost:8092/orders?accountId=5&productId=2'
curl -XPOST 'http://localhost:8092/orders?accountId=5&productId=2'

# Fail because there are no products
curl -XPOST 'http://localhost:8092/orders?accountId=5&productId=2'

# Fail because there is not enough money on account  (scenario 2)
curl -XPOST 'http://localhost:8092/orders?accountId=5&productId=5'
# Wrong state in product database. Product id=5 should have amount=5 but actually amount=4!

# Pass (scenario 1)
curl -XPOST 'http://localhost:8092/orders?accountId=5&productId=1'
```