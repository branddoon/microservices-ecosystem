package com.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration smoke test that verifies the Spring application context loads
 * without errors.
 *
 * <p>Eureka registration and Config Server lookup are disabled via
 * {@code properties} so the test can run without any running infrastructure.</p>
 */
@SpringBootTest(properties = {
		"spring.cloud.config.enabled=false",
		"eureka.client.enabled=false"
})
class SpringbootServicioItem1ApplicationTests {

	/**
	 * Verifies that the Spring application context starts up successfully.
	 */
	@Test
	void contextLoads() {
	}
}
