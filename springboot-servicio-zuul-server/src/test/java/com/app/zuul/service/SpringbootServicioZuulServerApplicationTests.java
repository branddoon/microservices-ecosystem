package com.app.zuul.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration tests for the API Gateway application context.
 *
 * <p>These tests verify that the Spring application context starts up correctly
 * with all beans — including Spring Cloud Gateway routes and global filters —
 * properly initialized.</p>
 *
 * <p>Eureka client registration is disabled via {@link TestPropertySource} so that
 * tests can run in isolation without a running Eureka server.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
class SpringbootServicioZuulServerApplicationTests {

    /**
     * Verifies that the Spring application context loads successfully,
     * ensuring all auto-configurations, route definitions, and global filters
     * are wired without errors.
     */
    @Test
    void contextLoads() {
    }
}
