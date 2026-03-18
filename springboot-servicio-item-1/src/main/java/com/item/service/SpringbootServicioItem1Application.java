package com.item.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Entry point of the Item microservice.
 *
 * <p>This service acts as an aggregator, combining product data retrieved from the
 * Product microservice with quantity information to produce {@code Item} objects.
 * It registers itself with the Eureka discovery server and uses OpenFeign as the
 * declarative HTTP client for inter-service communication.</p>
 *
 * <p>Fault tolerance is provided by Resilience4j circuit breakers and time limiters,
 * configured both programmatically (via {@code AppConfig}) and declaratively
 * (via {@code application.yml}).</p>
 */
@EnableFeignClients
@SpringBootApplication
public class SpringbootServicioItem1Application {

	/**
	 * Application entry point.
	 *
	 * @param args command-line arguments passed to the Spring application
	 */
	public static void main(String[] args) {
		SpringApplication.run(SpringbootServicioItem1Application.class, args);
	}
}
