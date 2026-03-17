# Eureka Service II

Service registry and discovery server based on **Netflix Eureka**, part of a microservices architecture with Spring Cloud.

## Description

This service acts as a **Eureka Server**: a central registry where microservices register themselves and discover each other. On startup, Eureka clients report their location (host/port) to this server, and other services can query it to locate available instances without needing to know their addresses statically.

## Technologies

| Technology           | Version   |
|----------------------|-----------|
| Java                 | 17        |
| Spring Boot          | 4.0.3     |
| Spring Cloud         | 2025.1.0  |
| Netflix Eureka Server| (included in Spring Cloud) |

## Configuration

The service is configured in `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: eureka-service-II

server:
  port: 8761

eureka:
  client:
    register-with-eureka: false  # Does not register itself
    fetch-registry: false        # Does not fetch the registry from other servers
```

- **Port:** `8761` (standard Eureka port)
- The server **does not register itself** nor fetches the registry, which is the typical behavior for a standalone server.

## Running the project

### Prerequisites

- Java 17+
- Maven 3.6+

### Build and run

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run
```

Or directly with the generated JAR:

```bash
java -jar target/springboot-servicio-eureka-server-0.0.1-SNAPSHOT.jar
```

### Eureka Dashboard

Once started, access the admin dashboard at:

```
http://localhost:8761
```

From there you can see all registered microservices, their status, and active instances.

## Project structure

```
springboot-servicio-eureka-server/
├── src/
│   └── main/
│       ├── java/com/formacionbdi/springboot/app/eureka/
│       │   └── SpringbootServicioEurekaServerApplication.java
│       └── resources/
│           └── application.yml
└── pom.xml
```

## Role in the microservices architecture

```
                    +------------------+
                    |  Eureka Server   |  <-- this service
                    |   :8761          |
                    +--------+---------+
                             |
          +------------------+------------------+
          |                  |                  |
   +-----------+      +-----------+      +-----------+
   | Productos |      |   Items   |      |  Gateway  |
   |  Service  |      |  Service  |      |  Server   |
   +-----------+      +-----------+      +-----------+
```

Client microservices register automatically on startup and query the server to resolve the addresses of other services, enabling client-side load balancing.
