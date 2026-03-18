package com.item.service.service;

import java.util.List;

import com.item.service.models.Item;
import com.item.service.models.Product;

/**
 * Service contract for item-related business operations.
 *
 * <p>Defines the operations that any item service implementation must support.
 * Two implementations are provided:</p>
 * <ul>
 *   <li>{@code ItemServiceImpl} — uses a load-balanced {@link org.springframework.web.client.RestTemplate}</li>
 *   <li>{@code ItemServiceFeign} — uses an OpenFeign declarative client</li>
 * </ul>
 */
public interface ItemService {

	/**
	 * Retrieves all items available in the system.
	 *
	 * <p>Each item is constructed by fetching all products from the Product
	 * microservice and pairing each one with a default quantity of 1.</p>
	 *
	 * @return a list of {@link Item} objects; never {@code null}
	 */
	List<Item> findAll();

	/**
	 * Retrieves a single item by product ID and applies the requested quantity.
	 *
	 * @param id       the ID of the product to look up
	 * @param quantity the number of units to associate with the item
	 * @return the matching {@link Item}
	 */
	Item findById(Long id, Integer quantity);

	/**
	 * Persists a new product via the Product microservice.
	 *
	 * @param product the product data to save
	 * @return the saved {@link Product} as returned by the Product service
	 */
	Product save(Product product);

	/**
	 * Updates an existing product via the Product microservice.
	 *
	 * @param product the updated product data
	 * @param id      the ID of the product to update
	 * @return the updated {@link Product} as returned by the Product service
	 */
	Product update(Product product, Long id);

	/**
	 * Deletes a product by its ID via the Product microservice.
	 *
	 * @param id the ID of the product to delete
	 */
	void delete(Long id);
}
