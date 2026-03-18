package com.item.service.models;

import java.util.Date;

/**
 * Client-side model representing a product.
 *
 * <p>This class is a plain DTO (Data Transfer Object) used by the Item microservice
 * to deserialize JSON responses from the Product microservice. It mirrors the fields
 * exposed by the product service's REST API.</p>
 */
public class Product {

	/** Unique identifier of the product. */
	private Long id;

	/** Display name of the product. */
	private String name;

	/** Unit price of the product. */
	private Double price;

	/** Timestamp indicating when the product record was created. */
	private Date createdAt;

	/**
	 * Port number of the Product microservice instance that served the response.
	 * Useful for verifying load balancing across multiple instances.
	 */
	private Integer port;

	/**
	 * Returns the unique identifier of the product.
	 *
	 * @return the product ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of the product.
	 *
	 * @param id the product ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the display name of the product.
	 *
	 * @return the product name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the display name of the product.
	 *
	 * @param name the product name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the unit price of the product.
	 *
	 * @return the product price
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * Sets the unit price of the product.
	 *
	 * @param price the product price
	 */
	public void setPrice(Double price) {
		this.price = price;
	}

	/**
	 * Returns the creation timestamp of the product record.
	 *
	 * @return the creation date
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * Sets the creation timestamp of the product record.
	 *
	 * @param createdAt the creation date
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * Returns the port of the Product service instance that handled the request.
	 *
	 * @return the service instance port
	 */
	public Integer getPort() {
		return port;
	}

	/**
	 * Sets the port of the Product service instance that handled the request.
	 *
	 * @param port the service instance port
	 */
	public void setPort(Integer port) {
		this.port = port;
	}
}
