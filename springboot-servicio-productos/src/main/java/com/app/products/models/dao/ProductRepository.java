package com.app.products.models.dao;

import com.lib.commons.product.entity.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Product} entities.
 *
 * <p>Extends {@link CrudRepository} to provide standard CRUD operations
 * against the underlying H2 in-memory database.
 *
 * @author formacionbdi
 * @version 1.0.0
 */
@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
}
