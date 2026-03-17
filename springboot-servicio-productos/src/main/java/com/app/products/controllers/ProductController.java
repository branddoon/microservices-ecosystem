package com.app.products.controllers;

import com.app.products.models.service.IProductService;
import com.lib.commons.product.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Objects;

/**
 * REST controller that exposes CRUD endpoints for the product catalog.
 *
 * <p>All responses include the server port so that upstream load-balanced
 * callers can verify which instance handled the request.
 *
 * <p>Base URL: {@code /products}
 *
 * @author formacionbdi
 * @version 1.0.0
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final Environment env;
    private final Integer port;
    private final IProductService productService;

    /**
     * Constructs a new {@code ProductController}.
     *
     * @param env            the Spring {@link Environment} used to resolve runtime properties
     * @param port           the configured server port, injected from {@code server.port}
     * @param productService the service handling product business logic
     */
    public ProductController(Environment env,
                             @Value("${server.port}") Integer port,
                             IProductService productService) {
        this.env = env;
        this.port = port;
        this.productService = productService;
    }

    /**
     * Returns all products, each stamped with the port of the responding instance.
     *
     * @return a {@link List} of all {@link Product} entities
     */
    @GetMapping
    public List<Product> list() {
        log.info("Listing all products - serving from port: {}", port);
        return productService.findAll().stream()
                .peek(product -> product.setPort(port))
                .toList();
    }

    /**
     * Returns a single product by its identifier, stamped with the current server port.
     *
     * @param id the unique identifier of the product
     * @return the matching {@link Product}
     */
    @GetMapping("/{id}")
    public Product detail(@PathVariable Long id) {
        Product product = productService.findById(id);
        product.setPort(Integer.parseInt(Objects.requireNonNull(env.getProperty("local.server.port"))));
        return product;
    }

    /**
     * Creates a new product and returns the persisted entity.
     *
     * @param product the product data to persist; must not be {@code null}
     * @return the created {@link Product} with generated fields populated
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product product) {
        return productService.save(product);
    }

    /**
     * Updates an existing product identified by {@code id}.
     *
     * <p>Only the {@code nombre} (name) and {@code precio} (price) fields are updated.
     *
     * @param product the request body containing the updated field values
     * @param id      the identifier of the product to update
     * @return the updated {@link Product}
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Product update(@RequestBody Product product, @PathVariable Long id) {
        Product existing = productService.findById(id);
        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        return productService.save(existing);
    }

    /**
     * Deletes a product by its identifier.
     *
     * @param id the identifier of the product to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.deleteById(id);
    }
}
