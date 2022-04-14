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
  - Enabled `max_prepared_transactions` in postgres.

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
# Just to verify that Atomikos REST API is available
# Should return "Hello from Atomikos!"
curl -XGET 'http://localhost:8091/atomikos/atomikos'
curl -XGET 'http://localhost:8092/atomikos/atomikos'
curl -XGET 'http://localhost:8093/atomikos/atomikos'

# To verify, order application endpoints are available
curl -XGET 'http://localhost:8092/orders'
```

```
# Pass (scenario 1)
curl -XPOST 'http://localhost:8092/orders?accountId=5&productId=2'
curl -XPOST 'http://localhost:8092/orders?accountId=5&productId=2'

# Fail because there are no products
curl -XPOST 'http://localhost:8092/orders?accountId=5&productId=2'

# Fail because there is not enough money on account  (scenario 2)
curl -XPOST 'http://localhost:8092/orders?accountId=5&productId=5'
# Correct state in product database. Product id=5 should have amount=5 and it has amount=5!
# Distributed transaction works!

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
> min response time                                    230 (OK=230    KO=-     )
> max response time                                   9243 (OK=9243   KO=-     )
> mean response time                                  6487 (OK=6487   KO=-     )
> std deviation                                       1415 (OK=1415   KO=-     )
> response time 50th percentile                       7176 (OK=7176   KO=-     )
> response time 75th percentile                       7340 (OK=7340   KO=-     )
> response time 95th percentile                       7672 (OK=7672   KO=-     )
> response time 99th percentile                       7974 (OK=7974   KO=-     )
> mean requests/sec                                 15.314 (OK=15.314 KO=-     )
---- Response Time Distribution ------------------------------------------------
> t < 800 ms                                            11 (  0%)
> 800 ms < t < 1200 ms                                   8 (  0%)
> t > 1200 ms                                         9981 (100%)
> failed                                                 0 (  0%)
================================================================================

Repeat: 10000; AtOnceUsers: 1
================================================================================
---- Global Information --------------------------------------------------------
> request count                                      10000 (OK=10000  KO=0     )
> min response time                                     49 (OK=49     KO=-     )
> max response time                                    527 (OK=527    KO=-     )
> mean response time                                    81 (OK=81     KO=-     )
> std deviation                                         16 (OK=16     KO=-     )
> response time 50th percentile                         86 (OK=86     KO=-     )
> response time 75th percentile                         88 (OK=88     KO=-     )
> response time 95th percentile                         99 (OK=99     KO=-     )
> response time 99th percentile                        111 (OK=111    KO=-     )
> mean requests/sec                                 12.376 (OK=12.376 KO=-     )
---- Response Time Distribution ------------------------------------------------
> t < 800 ms                                         10000 (100%)
> 800 ms < t < 1200 ms                                   0 (  0%)
> t > 1200 ms                                            0 (  0%)
> failed                                                 0 (  0%)
================================================================================
```

- Atomikos performs a lot faster when multiple requests do not try to access same resource for writing
  - It is logical, because request doest not have to wait behind other request's lock.

## Other problems
- If account DB dies and has to be restored from older backup, then data is still inconsistent
  - How to overcome:
    - Use one DB but different schemas for each microservice
    - Use DB in HA mode (replication + load balancing)
- When coordinator sends COMMIT to first microservice but fails before sending COMMIT to second microservice
  - ...?
- When first microservice's DB receives COMMIT but second microservice's DB fails before receiving COMMIT
  - ...?
- When using microservices then somehow you must manage transaction logs, so that pending transactions would be finished
- Speed - is it really a problem?