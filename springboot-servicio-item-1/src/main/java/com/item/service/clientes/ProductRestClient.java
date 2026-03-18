package com.item.service.clientes;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.item.service.models.Product;

/**
 * OpenFeign declarative REST client for the Product microservice.
 *
 * <p>Spring Cloud OpenFeign generates a proxy implementation of this interface at
 * startup. The {@code name} attribute must match the Eureka service ID under which
 * the Product microservice is registered ({@code servicio-productos}).</p>
 *
 * <p>Endpoint paths must align with those exposed by the Product microservice.</p>
 */
@FeignClient(name = "product-service")
public interface ProductRestClient {

	/**
	 * Retrieves all products from the Product microservice.
	 *
	 * @return a list of all {@link Product} objects
	 */
	@GetMapping("/products")
	List<Product> findAll();

	/**
	 * Retrieves a single product by its ID.
	 *
	 * @param id the product ID
	 * @return the matching {@link Product}
	 */
	@GetMapping("/products/{id}")
	Product findById(@PathVariable Long id);

	/**
	 * Creates a new product in the Product microservice.
	 *
	 * @param product the product data to persist
	 * @return the created {@link Product} with its server-assigned ID
	 */
	@PostMapping("/products")
	Product create(@RequestBody Product product);

	/**
	 * Updates an existing product in the Product microservice.
	 *
	 * @param product the updated product data
	 * @param id      the ID of the product to update
	 * @return the updated {@link Product}
	 */
	@PutMapping("/products/{id}")
	Product update(@RequestBody Product product, @PathVariable Long id);

	/**
	 * Deletes a product by its ID from the Product microservice.
	 *
	 * @param id the ID of the product to delete
	 */
	@DeleteMapping("/products/{id}")
	void delete(@PathVariable Long id);
}
