package com.item.service.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.item.service.models.Item;
import com.item.service.models.Product;
import com.item.service.service.ItemService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

/**
 * REST controller for the Item microservice.
 *
 * <p>Exposes endpoints to list, retrieve, create, update, and delete items.
 * An {@link Item} is a composition of a {@link Product} (fetched from the Product
 * microservice) and a requested quantity.</p>
 *
 * <p>Three circuit-breaker strategies are demonstrated:</p>
 * <ol>
 *   <li>{@link CircuitBreakerFactory} — programmatic, configured in {@code AppConfig}.</li>
 *   <li>{@link CircuitBreaker} annotation — declarative, configured in {@code application.yml}.</li>
 *   <li>{@link TimeLimiter} annotation — timeout-only, async via {@link CompletableFuture}.</li>
 *   <li>Combined {@link CircuitBreaker} + {@link TimeLimiter} — both fault-tolerance
 *       mechanisms active simultaneously.</li>
 * </ol>
 *
 * <p>{@code @RefreshScope} ensures that beans (and their injected values) are
 * refreshed when a {@code /actuator/refresh} POST is received, picking up the
 * latest configuration from the Config Server.</p>
 */
@RefreshScope
@RestController
public class ItemController {

	/** Logger instance for this controller. */
	private static final Logger log = LoggerFactory.getLogger(ItemController.class);

	/** Spring {@link Environment} used to read active profiles and config properties. */
	@Autowired
	private Environment env;

	/**
	 * Programmatic circuit-breaker factory.
	 * Configuration for circuit breakers created here comes from {@code AppConfig}.
	 */
	@Autowired
	private CircuitBreakerFactory<?, ?> cbFactory;

	/**
	 * Item service implementation backed by OpenFeign.
	 * The {@code @Qualifier} selects the Feign implementation over the RestTemplate one.
	 */
	@Autowired
	@Qualifier("itemServiceFeign")
	private ItemService itemService;

	/**
	 * A text value injected from the external Config Server.
	 * The property key {@code configuracion.texto} is defined in the remote
	 * configuration repository.
	 */
	@Value("${configuration.text}")
	private String configText;

	/**
	 * Returns a list of all available items.
	 *
	 * <p>Optional query and header parameters are logged for debugging purposes
	 * but do not affect the result.</p>
	 *
	 * @param name  optional filter name (currently logged only)
	 * @param token optional request token from the {@code token-request} header
	 * @return list of all {@link Item} objects
	 */
	@GetMapping("/list")
	public List<Item> list(
			@RequestParam(name = "name", required = false) String name,
			@RequestHeader(name = "token-request", required = false) String token) throws InterruptedException {
		log.debug("Request param name: {}", name);
		log.debug("Request header token-request: {}", token);
		Thread.sleep(10000);
		return itemService.findAll();
	}

	/**
	 * Retrieves a single item using the programmatic {@link CircuitBreakerFactory}.
	 *
	 * <p>If the downstream call fails, {@link #fallbackMethod(Long, Integer, Throwable)}
	 * is invoked to return a default item.</p>
	 *
	 * <p>The circuit-breaker configuration (sliding window, thresholds, etc.) comes
	 * from {@code AppConfig#defaultCustomizer()}.</p>
	 *
	 * @param id       the product ID
	 * @param quantity the quantity to associate with the item
	 * @return the {@link Item}, or a fallback value on failure
	 */
	@GetMapping("/detail/{id}/quantity/{quantity}")
	public Item detail(@PathVariable Long id, @PathVariable Integer quantity) {
		return cbFactory.create("items")
				.run(() -> itemService.findById(id, quantity),
						e -> fallbackMethod(id, quantity, e));
	}

	/**
	 * Retrieves a single item using the {@link CircuitBreaker} annotation.
	 *
	 * <p>The circuit-breaker configuration is read from {@code application.yml}
	 * (instance {@code items}, base config {@code default}).
	 * Falls back to {@link #fallbackMethod(Long, Integer, Throwable)} on failure.</p>
	 *
	 * @param id       the product ID
	 * @param quantity the quantity to associate with the item
	 * @return the {@link Item}, or a fallback value on failure
	 */
	@CircuitBreaker(name = "items", fallbackMethod = "fallbackMethod")
	@GetMapping("/detail2/{id}/quantity/{quantity}")
	public Item detail2(@PathVariable Long id, @PathVariable Integer quantity) {
		return itemService.findById(id, quantity);
	}

	/**
	 * Retrieves a single item applying only a timeout via {@link TimeLimiter}.
	 *
	 * <p>The call is wrapped in a {@link CompletableFuture} so that the
	 * time-limiter can interrupt it if the configured timeout elapses.
	 * No circuit-breaker state is maintained here.
	 * Falls back to {@link #fallbackMethod2(Long, Integer, Throwable)} on timeout.</p>
	 *
	 * @param id       the product ID
	 * @param quantity the quantity to associate with the item
	 * @return a {@link CompletableFuture} that resolves to the {@link Item}
	 */
	@TimeLimiter(name = "items", fallbackMethod = "fallbackMethod2")
	@GetMapping("/detail3/{id}/quantity/{quantity}")
	public CompletableFuture<Item> detail3(@PathVariable Long id, @PathVariable Integer quantity) {
		return CompletableFuture.supplyAsync(() -> itemService.findById(id, quantity));
	}

	/**
	 * Retrieves a single item combining {@link CircuitBreaker} and {@link TimeLimiter}.
	 *
	 * <p>This variant guarantees fault tolerance for both downstream exceptions
	 * <em>and</em> timeouts. The fallback method must be specified only on
	 * {@code @CircuitBreaker}; the {@code @TimeLimiter} delegates to it.
	 * Falls back to {@link #fallbackMethod2(Long, Integer, Throwable)}.</p>
	 *
	 * @param id       the product ID
	 * @param quantity the quantity to associate with the item
	 * @return a {@link CompletableFuture} that resolves to the {@link Item}
	 */
	@CircuitBreaker(name = "items", fallbackMethod = "fallbackMethod2")
	@TimeLimiter(name = "items")
	@GetMapping("/detail4/{id}/quantity/{quantity}")
	public CompletableFuture<Item> detail4(@PathVariable Long id, @PathVariable Integer quantity) {
		return CompletableFuture.supplyAsync(() -> itemService.findById(id, quantity));
	}

	/**
	 * Synchronous fallback method returned when a circuit-breaker trips or the
	 * downstream call fails.
	 *
	 * <p>Returns a hard-coded {@link Item} so that the caller always receives a
	 * usable response even when the Product microservice is unavailable.</p>
	 *
	 * @param id       the product ID that was requested
	 * @param quantity the quantity that was requested
	 * @param e        the exception that caused the fallback to be triggered
	 * @return a default {@link Item} with placeholder product data
	 */
	public Item fallbackMethod(Long id, Integer quantity, Throwable e) {
		log.error("Circuit breaker fallback triggered: {}", e.getMessage());
		Item item = new Item();
		Product product = new Product();
		item.setQuantity(quantity);
		product.setId(id);
		product.setName("Sony Camera");
		product.setPrice(500.0);
		item.setProduct(product);
		return item;
	}

	/**
	 * Asynchronous fallback method returned when a circuit-breaker or time-limiter
	 * triggers on an async endpoint.
	 *
	 * <p>Returns the same default {@link Item} as {@link #fallbackMethod} but
	 * wrapped in a {@link CompletableFuture} as required by the async endpoints.</p>
	 *
	 * @param id       the product ID that was requested
	 * @param quantity the quantity that was requested
	 * @param e        the exception that caused the fallback to be triggered
	 * @return a {@link CompletableFuture} resolving to a default {@link Item}
	 */
	public CompletableFuture<Item> fallbackMethod2(Long id, Integer quantity, Throwable e) {
		log.error("Async circuit breaker fallback triggered: {}", e.getMessage());
		Item item = new Item();
		Product product = new Product();
		item.setQuantity(quantity);
		product.setId(id);
		product.setName("Sony Camera");
		product.setPrice(500.0);
		item.setProduct(product);
		return CompletableFuture.supplyAsync(() -> item);
	}

	/**
	 * Returns the active configuration values loaded from the Config Server.
	 *
	 * <p>When the active Spring profile is {@code dev}, additional author
	 * metadata is included in the response.</p>
	 *
	 * @param port the server port, injected from {@code server.port}
	 * @return a {@link ResponseEntity} containing a map of configuration values
	 */
	@GetMapping("/config")
	public ResponseEntity<Map<String, String>> getConfig(@Value("${server.port}") String port) {
		log.info("Config text value: {}", configText);
		Map<String, String> response = new HashMap<>();
		response.put("text", configText);
		response.put("port", port);

		if (env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equals("dev")) {
			response.put("author.name", env.getProperty("configuracion.autor.nombre"));
			response.put("author.email", env.getProperty("configuracion.autor.email"));
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Creates a new product via the Product microservice.
	 *
	 * @param product the product payload from the request body
	 * @return the created {@link Product}
	 */
	@PostMapping("/create")
	@ResponseStatus(HttpStatus.CREATED)
	public Product create(@RequestBody Product product) {
		return itemService.save(product);
	}

	/**
	 * Updates an existing product via the Product microservice.
	 *
	 * @param product the updated product payload from the request body
	 * @param id      the ID of the product to update
	 * @return the updated {@link Product}
	 */
	@PutMapping("/update/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Product update(@RequestBody Product product, @PathVariable Long id) {
		return itemService.update(product, id);
	}

	/**
	 * Deletes a product by ID via the Product microservice.
	 *
	 * @param id the ID of the product to delete
	 */
	@DeleteMapping("/delete/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		itemService.delete(id);
	}
}
