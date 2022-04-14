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

### Load tests
```
cd test
mvn gatling:test
```

#### Results
```
Repeat: 100; AtOnceUsers: 100
================================================================================
---- Global Information --------------------------------------------------------
> request count                                      10000 (OK=10000  KO=0     )
> min response time                                      9 (OK=9      KO=-     )
> max response time                                   1358 (OK=1358   KO=-     )
> mean response time                                   190 (OK=190    KO=-     )
> std deviation                                         86 (OK=86     KO=-     )
> response time 50th percentile                        176 (OK=176    KO=-     )
> response time 75th percentile                        187 (OK=186    KO=-     )
> response time 95th percentile                        226 (OK=226    KO=-     )
> response time 99th percentile                        688 (OK=688    KO=-     )
> mean requests/sec                                    500 (OK=500    KO=-     )
---- Response Time Distribution ------------------------------------------------
> t < 800 ms                                          9915 ( 99%)
> 800 ms < t < 1200 ms                                  81 (  1%)
> t > 1200 ms                                            4 (  0%)
> failed                                                 0 (  0%)
================================================================================

Repeat: 10000; AtOnceUsers: 1
================================================================================
---- Global Information --------------------------------------------------------
> request count                                      10000 (OK=10000  KO=0     )
> min response time                                      7 (OK=7      KO=-     )
> max response time                                    665 (OK=665    KO=-     )
> mean response time                                    11 (OK=11     KO=-     )
> std deviation                                          7 (OK=7      KO=-     )
> response time 50th percentile                         11 (OK=11     KO=-     )
> response time 75th percentile                         12 (OK=12     KO=-     )
> response time 95th percentile                         15 (OK=15     KO=-     )
> response time 99th percentile                         19 (OK=19     KO=-     )
> mean requests/sec                                 87.719 (OK=87.719 KO=-     )
---- Response Time Distribution ------------------------------------------------
> t < 800 ms                                         10000 (100%)
> 800 ms < t < 1200 ms                                   0 (  0%)
> t > 1200 ms                                            0 (  0%)
> failed                                                 0 (  0%)
================================================================================
```