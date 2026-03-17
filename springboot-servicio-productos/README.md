# springboot-servicio-productos

> **Products Microservice** — part of the `microservices-practise-1` system.

Manages the product catalog through a REST API. Registers itself with a Netflix Eureka discovery server and uses an H2 in-memory database seeded with sample data on startup.

---

## Table of Contents

- [Requirements](#requirements)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Running the Service](#running-the-service)
- [Dependencies](#dependencies)

---

## Requirements

| Tool        | Version  |
|-------------|----------|
| Java        | 17+      |
| Maven       | 3.8+     |
| Eureka Server | Running on `localhost:8761` |

---

## Technology Stack

| Technology            | Version   | Purpose                          |
|-----------------------|-----------|----------------------------------|
| Spring Boot           | 2.7.18    | Application framework            |
| Spring Data JPA       | 2.7.x     | ORM / repository abstraction     |
| Spring Web (MVC)      | 2.7.x     | REST controller support          |
| Spring Cloud Netflix  | 2021.0.8  | Eureka service discovery client  |
| H2 Database           | runtime   | In-memory database               |
| commons library       | 0.0.1     | Shared `Producto` entity         |

---

## Project Structure

```
springboot-servicio-productos/
├── src/
│   ├── main/
│   │   ├── java/com/formacionbdi/springboot/app/productos/
│   │   │   ├── SpringbootServicioProductosApplication.java   # Entry point
│   │   │   ├── controllers/
│   │   │   │   └── ProductController.java                    # REST endpoints
│   │   │   └── models/
│   │   │       ├── dao/
│   │   │       │   └── ProductRepository.java                # Spring Data repository
│   │   │       └── service/
│   │   │           ├── IProductService.java                  # Service interface
│   │   │           └── ProductServiceImpl.java               # Service implementation
│   │   └── resources/
│   │       ├── application.properties                        # Runtime configuration
│   │       └── import.sql                                    # Initial seed data
│   └── test/
│       └── java/com/formacionbdi/springboot/app/productos/
│           └── SpringbootServicioProductosApplicationTests.java
└── pom.xml
```

---

## Configuration

**`src/main/resources/application.properties`**

```properties
spring.application.name=servicio-productos
server.port=${PORT:0}                          # 0 = random port (Eureka assigns one)

eureka.instance.instance-id=${spring.application.name}:${spring.application.instance_id:${random.value}}
eureka.client.service-url.defaultZone=http://localhost:8761/eureka

spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:testdb

logging.level.com.formacionbdi=INFO
```

> **Note:** The service uses a dynamic port (`PORT:0`) so multiple instances can run simultaneously. Each instance registers with a unique ID in Eureka.

---

## API Endpoints

Base URL: `http://localhost:<dynamic-port>/products`

| Method   | Path          | Description                        | Status |
|----------|---------------|------------------------------------|--------|
| `GET`    | `/products`   | List all products                  | 200    |
| `GET`    | `/products/{id}` | Get product by ID               | 200    |
| `POST`   | `/products`   | Create a new product               | 201    |
| `PUT`    | `/products/{id}` | Update an existing product      | 200    |
| `DELETE` | `/products/{id}` | Delete a product                | 204    |

### Example — Create a product

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"nombre": "LG", "precio": 750}'
```

### Example — List all products

```bash
curl http://localhost:8080/products
```

### Response shape

```json
[
  {
    "id": 1,
    "nombre": "Panasonic",
    "precio": 800.0,
    "createAt": "2026-03-17",
    "port": 55321
  }
]
```

> Each response includes the `port` field so load-balanced callers can identify which instance responded.

---

## Running the Service

### 1. Start the Eureka Server first

Make sure `springboot-service-config-server` and the Eureka server are running on port `8761`.

### 2. Install the commons library

```bash
cd ../springboot-service-commons
./mvnw install -DskipTests
```

### 3. Start this service

```bash
./mvnw spring-boot:run
```

Or build and run the JAR:

```bash
./mvnw package -DskipTests
java -jar target/springboot-servicio-productos-0.0.1-SNAPSHOT.jar
```

### 4. Scale horizontally

Run multiple instances with different ports:

```bash
java -DPORT=8001 -jar target/springboot-servicio-productos-0.0.1-SNAPSHOT.jar
java -DPORT=8002 -jar target/springboot-servicio-productos-0.0.1-SNAPSHOT.jar
```

Both instances register automatically with Eureka and are load-balanced by upstream services.

---

## Dependencies

| Artifact                                  | Scope   | Purpose                       |
|-------------------------------------------|---------|-------------------------------|
| `spring-boot-starter-web`                 | compile | REST API layer                |
| `spring-boot-starter-data-jpa`            | compile | JPA / Hibernate ORM           |
| `spring-cloud-starter-netflix-eureka-client` | compile | Service discovery          |
| `spring-boot-devtools`                    | runtime | Live reload during development|
| `h2`                                      | runtime | In-memory database            |
| `springboot-service-commons`              | compile | Shared `Producto` entity      |
| `spring-boot-starter-test`                | test    | JUnit 5 / Spring test support |
