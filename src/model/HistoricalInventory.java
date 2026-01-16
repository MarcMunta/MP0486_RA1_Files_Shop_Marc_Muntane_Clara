package model;

import java.sql.Timestamp;

/**
 * Clase de compatibilidad (obsoleta).
 *
 * El PDF usa la entidad {@link ProductHistory} para mapear la tabla historical_inventory.
 * Esta clase se mantiene sin anotaciones JPA para evitar conflictos de mapeo.
 */
@Deprecated
public class HistoricalInventory {

	private int id;
	private int productId;
	private String name;
	private double price;
	private boolean available;
	private int stock;
	private Timestamp createdAt;

	public HistoricalInventory() {
	}

	public static HistoricalInventory fromProduct(Product product) {
		HistoricalInventory row = new HistoricalInventory();
		row.productId = product.getId();
		row.name = product.getName();
		row.price = product.getPrice();
		row.available = product.isAvailable();
		row.stock = product.getStock();
		return row;
	}

	public int getId() {
		return id;
	}

	public int getProductId() {
		return productId;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	public boolean isAvailable() {
		return available;
	}

	public int getStock() {
		return stock;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}
}
