package model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

/**
 * Entidad para la tabla historical_inventory (snapshot histórico del inventario).
 *
 * Formato esperado (PDF): id, available, created_at, id_product, name, price, stock.
 */
@Entity
@Table(name = "historical_inventory")
public class ProductHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "id_product", nullable = false)
	private int idProduct;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "price", nullable = false)
	private double price;

	@Column(name = "available")
	private boolean available;

	@Column(name = "stock")
	private int stock;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private Timestamp createdAt;

	public ProductHistory() {
	}

	public static ProductHistory fromProduct(Product product) {
		ProductHistory row = new ProductHistory();
		row.idProduct = product.getId();
		row.name = product.getName();
		row.price = product.getPrice();
		row.available = product.isAvailable();
		row.stock = product.getStock();
		return row;
	}

	public int getId() {
		return id;
	}

	public int getIdProduct() {
		return idProduct;
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
