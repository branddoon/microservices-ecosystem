package com.app.gtw.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Cloud API Gateway Server.
 *
 * <p>This service acts as a single entry point for all microservice clients,
 * routing requests to the appropriate downstream services registered with
 * Eureka Discovery Server. It also applies global and route-specific filters,
 * and integrates circuit-breaker functionality via Resilience4j.
 *
 * <p>Eureka client auto-configuration is handled by Spring Cloud 2022+ and no
 * longer requires the {@code @EnableEurekaClient} annotation.
 *
 * @author Brandon
 * @version 1.0.0
 */
@SpringBootApplication
public class SpringbootServiceGatewayServerApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments passed at startup
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringbootServiceGatewayServerApplication.class, args);
    }
}
