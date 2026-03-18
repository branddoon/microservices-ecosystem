# springboot-servicio-zuul-server — API Gateway

Reactive API Gateway microservice built with **Spring Cloud Gateway**, **Spring Boot 3**, and **Java 17**. It replaces the legacy Netflix Zuul proxy and routes external HTTP traffic to downstream microservices registered in a Eureka service registry.

---

## Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Migration Notes (Zuul → Spring Cloud Gateway)](#migration-notes-zuul--spring-cloud-gateway)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Running the Service](#running-the-service)
- [Routes](#routes)
- [Global Filters](#global-filters)
- [Running Tests](#running-tests)

---

## Overview

This service acts as the single entry point for all client requests. It:

1. Receives an HTTP request on port **8090**.
2. Resolves the target downstream service via **Eureka** service discovery.
3. Load-balances the request across available instances using **Spring Cloud LoadBalancer**.
4. Forwards the request and returns the response to the client.
5. Logs the HTTP method, path, and total elapsed time via reactive **GlobalFilter** components.

---

## Technology Stack

| Technology              | Version      |
|-------------------------|--------------|
| Java                    | 17           |
| Spring Boot             | 3.3.4        |
| Spring Cloud            | 2023.0.3     |
| Spring Cloud Gateway    | (managed)    |
| Spring Cloud Netflix Eureka Client | (managed) |
| Maven                   | 3.9.6        |

---

## Migration Notes (Zuul → Spring Cloud Gateway)

Netflix Zuul was removed from Spring Cloud starting with version **2021.0.x (Jubilee)**. This project has been fully migrated to **Spring Cloud Gateway**, the officially recommended replacement.

| Aspect                 | Zuul (old)                              | Spring Cloud Gateway (new)                          |
|------------------------|-----------------------------------------|-----------------------------------------------------|
| Programming model      | Servlet (blocking, `javax.servlet`)     | Reactive (non-blocking, `WebFlux` / Reactor)        |
| Filter base class      | `ZuulFilter` (abstract class)           | `GlobalFilter` + `Ordered` (interfaces)             |
| Enable annotation      | `@EnableZuulProxy`                      | Not required — auto-configured                      |
| Eureka annotation      | `@EnableEurekaClient`                   | Not required — auto-configured                      |
| Route configuration    | `zuul.routes.<id>.*` properties         | `spring.cloud.gateway.routes[*]` YAML/properties    |
| Load balancer          | Ribbon (deprecated)                     | Spring Cloud LoadBalancer (`lb://` URI scheme)      |
| Circuit breaker        | Hystrix (deprecated)                    | Resilience4J (optional, not included here)          |
| HTTP client timeouts   | `ribbon.ConnectTimeout` / `ReadTimeout` | `spring.cloud.gateway.httpclient.*`                 |

---

## Project Structure

```
springboot-servicio-zuul-server/
├── src/
│   ├── main/
│   │   ├── java/com/formacionbdi/springboot/app/zuul/
│   │   │   ├── SpringbootServicioZuulServerApplication.java   # Application entry point
│   │   │   └── filters/
│   │   │       ├── PreElapsedTimeFilter.java    # Pre-routing filter (logs & timestamps request)
│   │   │       └── PostElapsedTimeFilter.java   # Post-routing filter (logs elapsed time)
│   │   └── resources/
│   │       └── application.yml                 # Application configuration
│   └── test/
│       └── java/com/formacionbdi/springboot/app/zuul/
│           └── SpringbootServicioZuulServerApplicationTests.java
└── pom.xml
```

---

## Configuration

All configuration lives in `src/main/resources/application.yml`.

```yaml
spring:
  application:
    name: servicio-zuul-server
  cloud:
    gateway:
      routes:
        - id: productos
          uri: lb://servicio-productos
          predicates:
            - Path=/api/productos/**
        - id: items
          uri: lb://servicio-items
          predicates:
            - Path=/api/items/**
      httpclient:
        connect-timeout: 3000
        response-timeout: 10s

server:
  port: 8090

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

### Key properties

| Property                                          | Default                          | Description                                    |
|---------------------------------------------------|----------------------------------|------------------------------------------------|
| `server.port`                                     | `8090`                           | Port the gateway listens on                    |
| `eureka.client.service-url.defaultZone`           | `http://localhost:8761/eureka`   | Eureka server URL                              |
| `spring.cloud.gateway.httpclient.connect-timeout` | `3000` ms                        | Max time to establish a connection             |
| `spring.cloud.gateway.httpclient.response-timeout`| `10s`                            | Max time to wait for a response                |

---

## Running the Service

### Prerequisites

- Java 17+
- A running **Eureka Server** (default: `http://localhost:8761`)
- Downstream services (`servicio-productos`, `servicio-items`) registered in Eureka

### Start

```bash
./mvnw spring-boot:run
```

Or build and run the JAR:

```bash
./mvnw clean package -DskipTests
java -jar target/springboot-servicio-zuul-server-0.0.1-SNAPSHOT.jar
```

---

## Routes

| Route ID    | Path Pattern          | Target Service      |
|-------------|-----------------------|---------------------|
| `productos` | `/api/productos/**`   | `servicio-productos`|
| `items`     | `/api/items/**`       | `servicio-items`    |

Requests are load-balanced across all healthy instances of the target service using the `lb://` URI scheme backed by Spring Cloud LoadBalancer.

---

## Global Filters

### `PreElapsedTimeFilter`

- **When:** Before forwarding the request to the downstream service.
- **What it does:**
  - Logs the HTTP method and request path.
  - Stores the current timestamp in the exchange attributes (`startTime`).
- **Order:** `1`

### `PostElapsedTimeFilter`

- **When:** After the downstream response has been fully received.
- **What it does:**
  - Reads the `startTime` attribute set by `PreElapsedTimeFilter`.
  - Calculates and logs the total elapsed time in both seconds and milliseconds.
- **Order:** `1`

---

## Running Tests

```bash
./mvnw test
```

The `contextLoads` test verifies that the full application context starts correctly. Eureka registration is disabled during tests via `@TestPropertySource`.
