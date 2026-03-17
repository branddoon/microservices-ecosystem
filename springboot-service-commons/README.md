# lib-commons.products

Shared library providing common JPA entities for microservices in this ecosystem. It centralizes the `Product` entity definition so that all services that need to work with product data can depend on a single, consistent model.

## Overview

| Property    | Value                        |
|-------------|------------------------------|
| Group ID    | `com.lib.commons.products`   |
| Artifact ID | `lib-commons.products`       |
| Version     | `0.0.2`                      |
| Java        | 17                           |
| Spring Boot | 4.0.3                        |

## Contents

### Entity: `Product`

**Package:** `com.lib.commons.product.entity`
**Table:** `products`

| Field      | Type      | Column      | Notes                        |
|------------|-----------|-------------|------------------------------|
| `id`       | `Long`    | `id`        | Primary key, auto-generated  |
| `name`     | `String`  | `name`      |                              |
| `price`    | `Double`  | `price`     |                              |
| `createAt` | `Date`    | `create_at` |                              |
| `port`     | `Integer` | `port`      | Port of the responding service instance |

The entity implements `Serializable` and uses Lombok `@Getter` / `@Setter` to avoid boilerplate.

## Installation

Install the library to your local Maven repository:

```bash
./mvnw install -DskipTests
```

Then add the dependency to any service that needs it:

```xml
<dependency>
    <groupId>com.lib.commons.products</groupId>
    <artifactId>lib-commons.products</artifactId>
    <version>0.0.2</version>
</dependency>
```

> **Note:** Because this library contains JPA entities but no datasource configuration of its own, the consuming service is responsible for providing and configuring the datasource. The library disables `DataSourceAutoConfiguration` on its own application class so it can be tested in isolation without a database connection.

## Dependencies

| Dependency                         | Scope   |
|------------------------------------|---------|
| `spring-boot-starter-data-jpa`     | compile |
| `lombok` 1.18.44                   | compile |
| `spring-boot-starter-test`         | test    |

## Usage example

```java
import com.lib.commons.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
```

## Project structure

```
springboot-service-commons/
├── src/
│   ├── main/
│   │   └── java/com/lib/commons/product/
│   │       ├── SpringbootServiceCommonsApplication.java
│   │       └── entity/
│   │           └── Product.java
│   └── test/
│       └── java/com/lib/commons/product/
│           └── SpringbootServiceCommonsApplicationTests.java
└── pom.xml
```

## Services that consume this library

- **springboot-servicio-productos** (`product-service`) — exposes the product catalog via REST and uses this entity as its JPA model.
