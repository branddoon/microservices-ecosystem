package com.app.zuul.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the API Gateway microservice.
 *
 * <p>This service acts as an API Gateway using <strong>Spring Cloud Gateway</strong>,
 * routing client requests to the appropriate downstream microservices registered in Eureka.
 * It replaces the legacy Netflix Zuul proxy, which was removed from Spring Cloud 2021.0.x onwards.</p>
 *
 * <p>Key capabilities provided by this gateway:</p>
 * <ul>
 *   <li>Dynamic route resolution via Eureka service discovery (load-balanced {@code lb://} URIs).</li>
 *   <li>Pre-routing filter that records the start time of every request.</li>
 *   <li>Post-routing filter that logs the total elapsed time for every request.</li>
 * </ul>
 *
 * <p><strong>Note:</strong> {@code @EnableEurekaClient} and {@code @EnableZuulProxy} are no longer
 * required. Spring Boot auto-configuration handles Eureka registration, and Spring Cloud Gateway
 * is enabled automatically when the dependency is on the classpath.</p>
 *
 * @author formacionbdi
 * @version 2.0.0
 */
@SpringBootApplication
public class SpringbootServicioZuulServerApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments passed to the application at startup
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringbootServicioZuulServerApplication.class, args);
    }
}
