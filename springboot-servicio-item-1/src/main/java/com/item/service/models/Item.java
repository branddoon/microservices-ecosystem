package com.item.service.models;

/**
 * Represents a shopping-cart line item.
 *
 * <p>An {@code Item} pairs a {@link Product} retrieved from the Product microservice
 * with a requested quantity. It also computes the line-item total on the fly.</p>
 */
public class Item {

	/** The product associated with this item. */
	private Product product;

	/** The quantity of the product requested. */
	private Integer quantity;

	/**
	 * Default no-argument constructor required for JSON deserialization.
	 */
	public Item() {
	}

	/**
	 * Creates a fully initialised {@code Item}.
	 *
	 * @param product  the product to associate with this item
	 * @param quantity the number of units requested
	 */
	public Item(Product product, Integer quantity) {
		this.product = product;
		this.quantity = quantity;
	}

	/**
	 * Returns the product associated with this item.
	 *
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * Sets the product associated with this item.
	 *
	 * @param product the product
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	/**
	 * Returns the quantity of units requested for this item.
	 *
	 * @return the quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}

	/**
	 * Sets the quantity of units requested for this item.
	 *
	 * @param quantity the quantity
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	/**
	 * Calculates and returns the total price for this line item.
	 *
	 * <p>Computed as {@code product.price × quantity}.</p>
	 *
	 * @return the total price
	 */
	public Double getTotal() {
		return product.getPrice() * quantity.doubleValue();
	}
}
