package com.app.products.models.service;

import com.app.products.models.dao.ProductRepository;
import com.lib.commons.product.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Default implementation of {@link IProductService}.
 *
 * <p>Delegates all persistence operations to {@link ProductRepository} and
 * applies appropriate transaction boundaries for read and write operations.
 *
 * @author formacionbdi
 * @version 1.0.0
 */
@Service
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;

    /**
     * Constructs a new {@code ProductServiceImpl} with the required repository.
     *
     * @param productRepository the repository used to access product data
     */
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return (List<Product>) productRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
