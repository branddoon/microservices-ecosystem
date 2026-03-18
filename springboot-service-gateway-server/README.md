# Spring Boot Service — API Gateway Server

A **Spring Cloud Gateway** service that acts as the single entry point for all microservice clients.
It handles routing, load balancing, global and route-specific filtering, and circuit-breaking via Resilience4j.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Filters](#filters)
  - [ExampleGlobalFilter](#exampleglobalfilter)
  - [ExampleCookieGatewayFilterFactory](#examplecookiegatewayfilterfactory)
- [Circuit Breaker](#circuit-breaker)
- [Running the Application](#running-the-application)
- [Prerequisites](#prerequisites)

---

## Overview

The API Gateway receives all incoming HTTP requests and routes them to the appropriate downstream
microservice discovered through **Eureka Service Discovery**. It enforces cross-cutting concerns
(token propagation, cookie injection, circuit breaking) without touching any individual service.

---

## Tech Stack

| Technology              | Version     |
|-------------------------|-------------|
| Java                    | 17          |
| Spring Boot             | 3.3.5       |
| Spring Cloud            | 2023.0.3    |
| Spring Cloud Gateway    | 4.x         |
| Netflix Eureka Client   | 4.x         |
| Resilience4j            | 2.x         |
| Project Reactor         | 3.x         |
| Maven                   | 3.9.6       |

---

## Architecture

```
Client
  │
  ▼
┌─────────────────────────────────────┐
│       API Gateway  (:8090)          │
│                                     │
│  ┌─────────────────────────────┐    │
│  │  ExampleGlobalFilter        │    │  ← runs on every route
│  └─────────────────────────────┘    │
│                                     │
│  Route: /api/products/**            │
│  ├─ CircuitBreaker (products)       │
│  ├─ StripPrefix=2                   │
│  └─ ExampleCookieFilter             │
│                                     │
│  Route: /api/items/**               │
│  └─ StripPrefix=2                   │
└─────────────────────────────────────┘
         │                   │
         ▼                   ▼
  servicio-productos   servicio-items
  (lb:// Eureka)       (lb:// Eureka)
```

---

## Project Structure

```
springboot-service-gateway-server/
├── src/
│   ├── main/
│   │   ├── java/com/formacionbdi/springboot/app/gateway/
│   │   │   ├── SpringbootServiceGatewayServerApplication.java   # Main entry point
│   │   │   └── filters/
│   │   │       ├── ExampleGlobalFilter.java                     # Global pre/post filter
│   │   │       └── factory/
│   │   │           └── ExampleCookieGatewayFilterFactory.java   # Route-scoped filter factory
│   │   └── resources/
│   │       └── application.yml                                  # Gateway + Resilience4j config
│   └── test/
│       └── java/com/formacionbdi/springboot/app/gateway/
│           └── SpringbootServiceGatewayServerApplicationTests.java
├── pom.xml
└── README.md
```

---

## Configuration

Key properties in `application.yml`:

| Property                              | Default Value              | Description                              |
|---------------------------------------|----------------------------|------------------------------------------|
| `spring.application.name`            | `service-gateway-server`   | Service name registered with Eureka      |
| `server.port`                         | `8090`                     | Gateway listening port                   |
| `eureka.client.service-url.defaultZone` | `http://localhost:8761/eureka` | Eureka Server URL                  |

---

## Filters

### ExampleGlobalFilter

**Type:** Global (applies to every route)
**Order:** 1

| Stage | Behavior |
|-------|----------|
| Pre   | Adds a `token: 123456` header to every forwarded request |
| Post  | Copies the `token` header value into the response headers; appends a `color=blue` cookie |

### ExampleCookieGatewayFilterFactory

**Type:** Route-scoped
**YAML name:** `ExampleCookie`

Configured per route using the shortcut notation:

```yaml
filters:
  - ExampleCookie=<message>, <cookieName>, <cookieValue>
```

| Stage | Behavior |
|-------|----------|
| Pre   | Logs the configured `message` |
| Post  | Appends a `<cookieName>=<cookieValue>` cookie to the response |

---

## Circuit Breaker

Configured via Resilience4j on the `products` circuit breaker instance:

| Parameter                              | Value  |
|----------------------------------------|--------|
| `sliding-window-size`                  | 6      |
| `failure-rate-threshold`               | 50 %   |
| `wait-duration-in-open-state`          | 20 s   |
| `permitted-calls-in-half-open-state`   | 4      |
| `slow-call-rate-threshold`             | 50 %   |
| `slow-call-duration-threshold`         | 2 s    |
| `timeout-duration` (TimeLimiter)       | 2 s    |

The circuit breaker activates on HTTP `500` responses from the products service.

---

## Running the Application

**1. Start Eureka Server first** (port `8761` by default).

**2. Start downstream services** (`servicio-productos`, `servicio-items`) so they register with Eureka.

**3. Run the gateway:**

```bash
./mvnw spring-boot:run
```

Or build a JAR and run it:

```bash
./mvnw clean package -DskipTests
java -jar target/springboot-service-gateway-server-0.0.1-SNAPSHOT.jar
```

The gateway will be available at `http://localhost:8090`.

---

## Prerequisites

- Java 17+
- Maven 3.9+ (or use the included `mvnw` wrapper)
- Eureka Server running on port `8761`
- Downstream microservices registered with Eureka
