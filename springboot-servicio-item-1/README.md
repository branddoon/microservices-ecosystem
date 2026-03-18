# springboot-servicio-item-1 — Item Microservice

A Spring Boot microservice that acts as an **aggregator**: it fetches product
data from the **Product microservice** and combines it with a quantity value to
produce `Item` objects.

---

## Technology Stack

| Component            | Version        |
|----------------------|----------------|
| Java                 | 17             |
| Spring Boot          | 3.3.6          |
| Spring Cloud         | 2023.0.3       |
| Resilience4j         | (managed)      |
| OpenFeign            | (managed)      |
| Netflix Eureka Client| (managed)      |

---

## Architecture Overview

```
Gateway / API consumer
        │
        ▼
 Item Microservice  (port 8082)
        │
        ├── ItemServiceFeign  ──► [Feign client]  ──► Product Microservice
        └── ItemServiceImpl   ──► [RestTemplate]  ──► Product Microservice
```

Two `ItemService` implementations are provided:

| Bean qualifier            | Transport      | Select with                             |
|---------------------------|----------------|-----------------------------------------|
| `itemServiceFeign`        | OpenFeign      | `@Qualifier("itemServiceFeign")`        |
| `itemServiceRestTemplate` | RestTemplate   | `@Qualifier("itemServiceRestTemplate")` |

The controller uses **`itemServiceFeign`** by default.

---

## REST Endpoints

### Item endpoints

| Method   | Path                              | Description                                     |
|----------|-----------------------------------|-------------------------------------------------|
| `GET`    | `/list`                           | List all items                                  |
| `GET`    | `/detail/{id}/quantity/{quantity}`| Get item (programmatic circuit breaker)         |
| `GET`    | `/detail2/{id}/quantity/{quantity}`| Get item (`@CircuitBreaker` annotation)        |
| `GET`    | `/detail3/{id}/quantity/{quantity}`| Get item (async, `@TimeLimiter` only)          |
| `GET`    | `/detail4/{id}/quantity/{quantity}`| Get item (async, `@CircuitBreaker` + `@TimeLimiter`) |
| `GET`    | `/config`                         | Show active configuration values                |
| `POST`   | `/create`                         | Create a product via the Product service        |
| `PUT`    | `/update/{id}`                    | Update a product via the Product service        |
| `DELETE` | `/delete/{id}`                    | Delete a product via the Product service        |

### Optional headers / params on `GET /list`

| Parameter       | Type   | Required | Description                     |
|-----------------|--------|----------|---------------------------------|
| `name`          | query  | No       | Name filter (logged only)       |
| `token-request` | header | No       | Auth token (logged only)        |

---

## Fault Tolerance

Resilience4j is configured both programmatically and declaratively.

### Programmatic (`AppConfig` + `CircuitBreakerFactory`)

Used by `GET /detail/{id}/quantity/{quantity}`.

| Setting                                  | Value |
|------------------------------------------|-------|
| Sliding window size                      | 10    |
| Failure rate threshold                   | 50 %  |
| Wait duration in open state              | 10 s  |
| Permitted calls in half-open state       | 5     |
| Slow-call rate threshold                 | 50 %  |
| Slow-call duration threshold             | 2 s   |
| Timeout duration                         | 6 s   |

### Declarative (`application.yml` + annotations)

Used by `GET /detail2`, `/detail3`, `/detail4`.

| Setting                                  | Value |
|------------------------------------------|-------|
| Sliding window size                      | 6     |
| Failure rate threshold                   | 50 %  |
| Wait duration in open state              | 20 s  |
| Permitted calls in half-open state       | 4     |
| Slow-call rate threshold                 | 50 %  |
| Slow-call duration threshold             | 2 s   |
| Timeout duration                         | 2 s   |

---

## Configuration

The service reads external configuration from the **Spring Cloud Config Server**.

### `bootstrap.properties` (create/update as needed)

```properties
spring.application.name=servicio-items
spring.profiles.active=dev
spring.cloud.config.uri=http://localhost:8888
management.endpoints.web.exposure.include=*
```

### Required Config Server properties

| Key                        | Description                                 |
|----------------------------|---------------------------------------------|
| `configuracion.texto`      | Arbitrary text shown by `GET /config`       |
| `configuracion.autor.nombre` | Author name (dev profile only)            |
| `configuracion.autor.email`  | Author email (dev profile only)           |

> **Note:** these keys are defined in the Config Server's repository, not in
> this service. To translate them, update the remote configuration files as well.

---

## Running Locally

### Prerequisites

1. **Eureka Server** running on port `8761`
2. **Spring Cloud Config Server** running on port `8888`
3. **Product Microservice** registered in Eureka as `servicio-productos`

### Start the service

```bash
./mvnw spring-boot:run
```

Or run `SpringbootServicioItem1Application` from your IDE.

### Refresh configuration at runtime

```bash
curl -X POST http://localhost:8082/actuator/refresh
```

---

## Running Tests

```bash
./mvnw test
```

Tests disable Eureka and Config Server lookup so they run without any
infrastructure.

---

## Project Structure

```
src/main/java/.../
├── SpringbootServicioItem1Application.java  # Entry point
├── AppConfig.java                           # Bean & circuit-breaker configuration
├── controllers/
│   └── ItemController.java                  # REST endpoints
├── models/
│   ├── Item.java                            # Item DTO (product + quantity)
│   └── Product.java                         # Product DTO (mirrors product service)
├── service/
│   ├── ItemService.java                     # Service interface
│   ├── ItemServiceFeign.java                # Feign implementation (default)
│   └── ItemServiceImpl.java                 # RestTemplate implementation
├── clientes/
│   └── ProductRestClient.java               # Feign client for product service
└── properties/
    └── SpecialHeadersProperties.java        # Custom header config properties
```
