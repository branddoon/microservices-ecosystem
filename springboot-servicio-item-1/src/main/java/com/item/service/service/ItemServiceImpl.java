package com.item.service.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.item.service.models.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.item.service.models.Product;

/**
 * {@link ItemService} implementation that communicates with the Product microservice
 * using a load-balanced {@link RestTemplate}.
 *
 * <p>This implementation is an alternative to {@code ItemServiceFeign}. It is
 * registered with the qualifier {@code "itemServiceRestTemplate"} so it can be
 * selected explicitly where needed.</p>
 *
 * <p>All HTTP calls target the logical Eureka service name
 * {@code servicio-productos}; the {@code @LoadBalanced} RestTemplate resolves
 * it to a real instance at runtime.</p>
 */
@Service("itemServiceRestTemplate")
public class ItemServiceImpl implements ItemService {

	/** Load-balanced REST client for calling the Product microservice. */
	@Autowired
	@Qualifier("restClient")
	private RestTemplate restClient;

	/**
	 * {@inheritDoc}
	 *
	 * <p>Fetches the full product list from the Product service and wraps each
	 * entry in an {@link Item} with a default quantity of 1.</p>
	 */
	@Override
	public List<Item> findAll() {
		List<Product> products = Arrays.asList(
				restClient.getForObject("http://servicio-productos/list", Product[].class));
		return products.stream().map(p -> new Item(p, 1)).collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Fetches a single product by ID and wraps it in an {@link Item} with
	 * the requested quantity.</p>
	 */
	@Override
	public Item findById(Long id, Integer quantity) {
		Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put("id", id.toString());
		Product product = restClient.getForObject(
				"http://servicio-productos/detail/{id}", Product.class, pathVariables);
		return new Item(product, quantity);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Sends a POST request to the Product service to persist the given product.</p>
	 */
	@Override
	public Product save(Product product) {
		HttpEntity<Product> body = new HttpEntity<>(product);
		ResponseEntity<Product> response = restClient.exchange(
				"http://servicio-productos/create", HttpMethod.POST, body, Product.class);
		return response.getBody();
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Sends a PUT request to the Product service to update the product with
	 * the given ID.</p>
	 */
	@Override
	public Product update(Product product, Long id) {
		Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put("id", id.toString());
		HttpEntity<Product> body = new HttpEntity<>(product);
		ResponseEntity<Product> response = restClient.exchange(
				"http://servicio-productos/update/{id}", HttpMethod.PUT, body, Product.class, pathVariables);
		return response.getBody();
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Sends a DELETE request to the Product service to remove the product with
	 * the given ID.</p>
	 */
	@Override
	public void delete(Long id) {
		Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put("id", id.toString());
		restClient.delete("http://servicio-productos/delete/{id}", pathVariables);
	}
}
