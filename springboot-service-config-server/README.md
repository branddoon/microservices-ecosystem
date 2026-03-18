# Config Server

Centralized configuration server for the microservices ecosystem. Built with Spring Cloud Config Server, it serves externalized configuration files to all registered microservices via a Git-backed repository.

## Tech Stack

- Java 17
- Spring Boot 3.4.3
- Spring Cloud 2024.0.1 (Config Server)

## How It Works

On startup, the server clones the remote Git repository and exposes each microservice's configuration over HTTP. Clients request their configuration using the pattern:

```
GET /{application}/{profile}
GET /{application}/{profile}/{label}
```

Example: `http://localhost:8888/servicio-items/default`

## Configuration

```yaml
spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/branddoon/microservices-ecosystem.git
          default-label: main
          clone-on-start: true
          search-paths: servicio-items-config

server:
  port: 8888
```

| Property | Description |
|---|---|
| `uri` | Remote Git repository containing the config files |
| `default-label` | Default branch to use (`main`) |
| `clone-on-start` | Clones the repo at startup to fail fast on misconfiguration |
| `search-paths` | Subdirectory within the repo where config files are located |

## Running Locally

**Prerequisites:** Java 17+, Maven 3.9+

```bash
./mvnw spring-boot:run
```

The server starts on port `8888`.

## Using a Local Repository

To serve config files from a local directory instead of a remote repository, replace the `uri` with a file path:

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: file:///C:/path/to/your/local/config
```
