# Microservices Ecosystem

A hands-on microservices ecosystem built with Spring Boot and Spring Cloud. The project demonstrates common distributed systems patterns: service discovery, centralized configuration, API gateway routing, inter-service communication, fault tolerance, and shared libraries.

## Architecture

```
                        ┌──────────────────────────────────────────────────┐
                        │                  Client / Browser                │
                        └─────────────────────┬────────────────────────────┘
                                              │
                                    ┌─────────▼─────────┐
                                    │    API Gateway     │
                                    │  :8090             │
                                    │  service-gateway   │
                                    └────┬──────────┬────┘
                                         │          │
                   ┌─────────────────────▼──┐   ┌───▼─────────────────────┐
                   │    Item Service         │   │   Product Service       │
                   │    :8082                │   │   :8081                 │
                   │    item-service         │   │   product-service       │
                   └──────────┬─────────────┘   └────────────┬────────────┘
                              │  OpenFeign                    │
                              └──────────────────────────────┘
                                              │
                              ┌───────────────▼──────────────────┐
                              │                                  │
              ┌───────────────▼────────────┐   ┌────────────────▼──────────┐
              │    Eureka Server            │   │    Config Server          │
              │    :8761                    │   │    :8888                  │
              │    (Service Registry)       │   │    (Centralized Config)   │
              └─────────────────────────────┘   └───────────────────────────┘
                                                              │
                                                 ┌────────────▼────────────┐
                                                 │   Git Repository        │
                                                 │   (Config files)        │
                                                 └─────────────────────────┘
```

## Services

| Service | Directory | Port | Role |
|---|---|---|---|
| Eureka Server | `springboot-servicio-eureka-server` | 8761 | Service registry and discovery |
| Config Server | `springboot-service-config-server` | 8888 | Centralized Git-backed configuration |
| Product Service | `springboot-servicio-productos` | 8081 | Product catalog — REST + H2 database |
| Item Service | `springboot-servicio-item-1` | 8082 | Aggregator — combines products with quantity |
| API Gateway | `springboot-service-gateway-server` | 8090 | Primary gateway with circuit breaker |
| Zuul Gateway | `springboot-servicio-zuul-server` | 8090 | Alternative gateway (migrated to Spring Cloud Gateway) |
| Commons Library | `springboot-service-commons` | — | Shared domain models (`Product` entity) |
| Config Repository | `servicio-items-config` | — | YAML config files for all profiles |

## Tech Stack

| Component | Technology | Version |
|---|---|---|
| Language | Java | 17 |
| Framework | Spring Boot | 3.3.x – 4.0.x |
| Service Discovery | Spring Cloud Netflix Eureka | — |
| Configuration | Spring Cloud Config | — |
| API Gateway | Spring Cloud Gateway (reactive) | — |
| HTTP Client | OpenFeign | — |
| Circuit Breaker | Resilience4j | — |
| Database | H2 (in-memory) | — |
| Code Generation | Lombok | 1.18.x |
| Spring Cloud BOM | 2023.0.x – 2025.1.x | — |

## Startup Order

Services must be started in this order to satisfy dependencies:

```
1. Eureka Server       (other services register here)
2. Config Server       (item-service fetches config at startup)
3. Product Service     (registers with Eureka)
4. Item Service        (registers with Eureka, fetches config)
5. API Gateway         (routes requests to the above services)
```

## Gateway Routes

Both gateways expose the following routes on port `8090`:

| Path | Target Service | Load Balanced |
|---|---|---|
| `/products` | `product-service` | Yes (`lb://`) |
| `/list` | `item-service` | Yes (`lb://`) |

## Key Patterns

### Service Discovery
Eureka Server runs at `:8761`. Every microservice registers itself on startup and discovers others by logical name (e.g., `lb://product-service`), removing the need for hardcoded URLs.

### Centralized Configuration
Config Server reads YAML files from a Git repository and serves them to clients at startup. The Item Service uses Spring Cloud Config Client with bootstrap to pull its configuration before the application context loads.

Config files in `servicio-items-config/`:
- `item-service.yml` — default profile
- `item-service-dev.yml` — development profile
- `item-service-prod.yml` — production profile

### Inter-Service Communication
Item Service calls Product Service using **OpenFeign** — a declarative HTTP client that resolves the target address via Eureka. No hardcoded hosts or ports are used.

### Fault Tolerance
Both the Item Service and API Gateway use **Resilience4j** circuit breakers:

| Property | Value |
|---|---|
| Sliding window size | 6 |
| Failure rate threshold | 50% |
| Wait duration (open state) | 20s |
| Slow call duration threshold | 2s |

The gateway falls back to `forward:/fallback/orders` when the circuit is open.

### Shared Library
`springboot-service-commons` is a plain Maven library (not a deployable service) that holds the `Product` JPA entity. Both `product-service` and `item-service` depend on it via `lib-commons.products:0.0.2`, keeping the domain model in one place.

## Running Locally

**Prerequisites:** Java 17+, Maven 3.9+

```bash
# 1. Eureka Server
cd springboot-servicio-eureka-server
./mvnw spring-boot:run

# 2. Config Server
cd springboot-service-config-server
./mvnw spring-boot:run

# 3. Product Service
cd springboot-servicio-productos
./mvnw spring-boot:run

# 4. Item Service
cd springboot-servicio-item-1
./mvnw spring-boot:run

# 5. API Gateway
cd springboot-service-gateway-server
./mvnw spring-boot:run
```

After all services are running, verify the registry at `http://localhost:8761`.

## Project Structure

```
microservices-practise-1/
├── springboot-servicio-eureka-server/     # Service registry
├── springboot-service-config-server/      # Centralized config
├── springboot-servicio-productos/         # Product catalog service
├── springboot-servicio-item-1/            # Item aggregator service
├── springboot-service-gateway-server/     # API Gateway (primary)
├── springboot-servicio-zuul-server/       # API Gateway (alternative)
├── springboot-service-commons/            # Shared library
└── servicio-items-config/                 # Git-backed config files
```
