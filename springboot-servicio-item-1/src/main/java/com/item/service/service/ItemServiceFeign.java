package com.item.service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.item.service.clientes.ProductRestClient;
import com.item.service.models.Item;
import com.item.service.models.Product;

/**
 * {@link ItemService} implementation that communicates with the Product microservice
 * using the OpenFeign declarative REST client {@link ProductRestClient}.
 *
 * <p>This is the <em>preferred</em> implementation and is injected into
 * {@code ItemController} via the qualifier {@code "itemServiceFeign"}.</p>
 */
@Service("itemServiceFeign")
public class ItemServiceFeign implements ItemService {

	/** OpenFeign proxy for the Product microservice REST API. */
	@Autowired
	private ProductRestClient productRestClient;

	/**
	 * {@inheritDoc}
	 *
	 * <p>Delegates to {@link ProductRestClient#findAll()} and wraps each product
	 * in an {@link Item} with a default quantity of 1.</p>
	 */
	@Override
	public List<Item> findAll() {
		return productRestClient.findAll()
				.stream()
				.map(p -> new Item(p, 1))
				.collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Delegates to {@link ProductRestClient#findById(Long)} and wraps the result
	 * in an {@link Item} with the requested quantity.</p>
	 */
	@Override
	public Item findById(Long id, Integer quantity) {
		return new Item(productRestClient.findById(id), quantity);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Delegates to {@link ProductRestClient#create(Product)}.</p>
	 */
	@Override
	public Product save(Product product) {
		return productRestClient.create(product);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Delegates to {@link ProductRestClient#update(Product, Long)}.</p>
	 */
	@Override
	public Product update(Product product, Long id) {
		return productRestClient.update(product, id);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Delegates to {@link ProductRestClient#delete(Long)}.</p>
	 */
	@Override
	public void delete(Long id) {
		productRestClient.delete(id);
	}
}
