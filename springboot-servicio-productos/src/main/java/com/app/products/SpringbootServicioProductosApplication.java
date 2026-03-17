package com.app.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

/**
 * Entry point for the Products microservice.
 *
 * <p>This service manages the product catalog and registers itself with the
 * Eureka service discovery server. Entities are scanned from the shared
 * commons library to avoid duplication across microservices.
 *
 * @author formacionbdi
 * @version 1.0.0
 */
@SpringBootApplication
@EntityScan("com.lib.commons.product")
public class SpringbootServicioProductosApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments passed at startup
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringbootServicioProductosApplication.class, args);
    }
}
