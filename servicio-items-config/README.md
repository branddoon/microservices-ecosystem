# servicio-items-config

Repositorio de configuración centralizada para los microservicios `servicio-items` y `servicio-productos`, consumido por **Spring Cloud Config Server**.

## Archivos de configuración

| Archivo | Aplicación | Perfil |
|---|---|---|
| `servicio-items.yml` | servicio-items | default |
| `servicio-items-dev.yml` | servicio-items | dev |
| `servicio-items-prod.yml` | servicio-items | prod |
| `servicio-productos-dev.yml` | servicio-productos | dev |

## Cómo consume un microservicio estas propiedades

Cada microservicio debe apuntar al Config Server en su `bootstrap.properties` o `bootstrap.yml`:

```yaml
spring:
  application:
    name: servicio-items        # debe coincidir con el nombre del archivo de config
  cloud:
    config:
      uri: http://localhost:8888  # URL del Config Server
```

Al arrancar, el microservicio solicita al Config Server las propiedades correspondientes a su `spring.application.name` y al perfil activo (`spring.profiles.active`).

## Activar perfil DEV o PROD

### Opción 1 — variable de entorno

```bash
export SPRING_PROFILES_ACTIVE=dev   # o prod
```

### Opción 2 — argumento al ejecutar el JAR

```bash
java -jar servicio-items.jar --spring.profiles.active=prod
```

### Opción 3 — `application.yml` del microservicio

```yaml
spring:
  profiles:
    active: dev
```

## Orden de precedencia de propiedades

Spring Cloud Config aplica las propiedades en el siguiente orden (mayor precedencia primero):

1. `servicio-items-{perfil}.yml` — propiedades específicas del perfil
2. `servicio-items.yml` — propiedades por defecto (todos los perfiles)

## Endpoints útiles del Config Server

```
GET http://localhost:8888/servicio-items/default
GET http://localhost:8888/servicio-items/dev
GET http://localhost:8888/servicio-items/prod
GET http://localhost:8888/servicio-productos/dev
```
