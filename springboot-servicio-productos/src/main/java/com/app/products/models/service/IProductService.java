package com.app.products.models.service;

import com.lib.commons.product.entity.Product;
import java.util.List;

/**
 * Service contract for product business operations.
 *
 * <p>Defines the CRUD operations available for managing {@link Product} entities
 * within the products microservice.
 *
 * @author formacionbdi
 * @version 1.0.0
 */
public interface IProductService {

    /**
     * Retrieves all products from the data store.
     *
     * @return a {@link List} containing all {@link Product} entities; never {@code null}
     */
    List<Product> findAll();

    /**
     * Retrieves a single product by its identifier.
     *
     * @param id the unique identifier of the product
     * @return the matching {@link Product}, or {@code null} if not found
     */
    Product findById(Long id);

    /**
     * Persists a new or existing product.
     *
     * @param product the {@link Product} to save; must not be {@code null}
     * @return the saved {@link Product} including any generated values (e.g. id)
     */
    Product save(Product product);

    /**
     * Removes a product by its identifier.
     *
     * @param id the unique identifier of the product to delete
     */
    void deleteById(Long id);
}
