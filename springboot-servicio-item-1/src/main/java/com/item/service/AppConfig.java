package com.item.service;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

/**
 * Application-level Spring configuration.
 *
 * <p>Registers the load-balanced {@link RestTemplate} bean used by
 * {@code ItemServiceImpl} and provides a default Resilience4j circuit-breaker
 * customizer used when circuit breakers are created programmatically via
 * {@link org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory}.</p>
 *
 * <p><strong>Note:</strong> when circuit breakers are activated through the
 * {@code @CircuitBreaker} annotation, their configuration is read from
 * {@code application.yml}, not from this class.</p>
 */
@Configuration
public class AppConfig {

	/**
	 * Creates and registers a load-balanced {@link RestTemplate} bean.
	 *
	 * <p>The {@code @LoadBalanced} annotation makes the template aware of Eureka,
	 * so logical service names (e.g. {@code http://servicio-productos/...}) are
	 * resolved to real instances via client-side load balancing.</p>
	 *
	 * @return a new {@link RestTemplate} configured for load-balanced calls
	 */
	@Bean("restClient")
	@LoadBalanced
	public RestTemplate registerRestTemplate() {
		return new RestTemplate();
	}

	/**
	 * Provides the default Resilience4j circuit-breaker customizer used by the
	 * {@link org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory}.
	 *
	 * <p>Configuration summary:</p>
	 * <ul>
	 *   <li><b>slidingWindowSize(10)</b> – the last 10 calls are used to calculate
	 *       the failure/slow-call rate.</li>
	 *   <li><b>failureRateThreshold(50)</b> – the circuit opens when ≥ 50 % of
	 *       calls fail.</li>
	 *   <li><b>waitDurationInOpenState(10 s)</b> – the circuit stays open for 10
	 *       seconds before transitioning to half-open.</li>
	 *   <li><b>permittedNumberOfCallsInHalfOpenState(5)</b> – only 5 calls are
	 *       allowed while the circuit is half-open.</li>
	 *   <li><b>slowCallRateThreshold(50)</b> – the circuit opens when ≥ 50 % of
	 *       calls are considered slow.</li>
	 *   <li><b>slowCallDurationThreshold(2 s)</b> – a call lasting more than 2 s
	 *       is classified as slow.</li>
	 *   <li><b>timeoutDuration(6 s)</b> – requests are aborted after 6 seconds.</li>
	 * </ul>
	 *
	 * @return a {@link Customizer} that applies the default circuit-breaker settings
	 */
	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
		return factory -> factory.configureDefault(id ->
				new Resilience4JConfigBuilder(id)
						.circuitBreakerConfig(CircuitBreakerConfig.custom()
								.slidingWindowSize(10)
								.failureRateThreshold(50)
								.waitDurationInOpenState(Duration.ofSeconds(10L))
								.permittedNumberOfCallsInHalfOpenState(5)
								.slowCallRateThreshold(50)
								.slowCallDurationThreshold(Duration.ofSeconds(2L))
								.build())
						.timeLimiterConfig(TimeLimiterConfig.custom()
								.timeoutDuration(Duration.ofSeconds(6L))
								.build())
						.build()
		);
	}
}
