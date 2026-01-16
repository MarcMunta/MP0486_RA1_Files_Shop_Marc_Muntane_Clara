package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Clase que representa un producto del inventario de la tienda.
 * Contiene información sobre precio, stock y disponibilidad.
 * 
 * @author Marc Muntané Clarà
 * @version 2.0
 */
@Entity
@Table(name = "inventory")
public class Product {
	
	/** Identificador único del producto */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	/** Nombre del producto */
	@Column(name = "name", nullable = false, length = 100)
	private String name;
	
	/** Precio del producto (persistido como columna price) */
	@Column(name = "price", nullable = false)
	private double price;

	/** Precio formateado para UI (no persistido) */
	@Transient
	private Amount publicPrice;
	
	/** Indica si el producto está disponible para la venta */
	@Column(name = "available")
	private boolean available;
	
	/** Cantidad de unidades en stock */
	@Column(name = "stock")
	private int stock;
	
	/** Contador total de productos creados */
	private static int totalProducts;

	/** Tasa de descuento para productos próximos a caducar (40% de descuento) */
	public final static double EXPIRATION_RATE = 0.60;

	/**
	 * Constructor vacío requerido por Hibernate/JPA.
	 */
	public Product() {
	}

	/**
	 * Constructor que genera automáticamente el ID del producto.
	 * 
	 * @param name nombre del producto
	 * @param price precio del producto
	 * @param available disponibilidad inicial
	 * @param stock cantidad inicial en stock
	 */
	public Product(String name, double price, boolean available, int stock) {
		super();
		this.name = name;
		this.price = price;
		this.publicPrice = new Amount(price);
		this.available = available;
		this.stock = stock;
		totalProducts++;
	}

	/**
	 * Constructor con ID especificado (usado para cargar desde BD).
	 * 
	 * @param id identificador único del producto
	 * @param name nombre del producto
	 * @param price precio del producto
	 * @param available disponibilidad
	 * @param stock cantidad en stock
	 */
	public Product(int id, String name, double price, boolean available, int stock) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.publicPrice = new Amount(price);
		this.available = available;
		this.stock = stock;
		totalProducts = Math.max(totalProducts, this.id);
	}

	/**
	 * Obtiene el identificador del producto.
	 * @return id del producto
	 */
	public int getId() {
		return id;
	}

	/**
	 * Establece el identificador del producto.
	 * @param id nuevo identificador
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Obtiene el nombre del producto.
	 * @return nombre del producto
	 */
	public String getName() {
		return name;
	}

	/**
	 * Establece el nombre del producto.
	 * @param name nuevo nombre
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Obtiene el precio de venta al público.
	 * @return precio público
	 */
	public Amount getPublicPrice() {
		if (publicPrice == null) {
			publicPrice = new Amount(price);
		}
		return publicPrice;
	}

	/**
	 * Establece el precio de venta al público.
	 * @param publicPrice nuevo precio público
	 */
	public void setPublicPrice(Amount publicPrice) {
		this.publicPrice = publicPrice;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
		this.publicPrice = new Amount(price);
	}

	@PostLoad
	private void onLoad() {
		this.publicPrice = new Amount(price);
		totalProducts = Math.max(totalProducts, this.id);
	}

	/**
	 * Verifica si el producto está disponible.
	 * @return true si está disponible
	 */
	public boolean isAvailable() {
		return available;
	}

	/**
	 * Establece la disponibilidad del producto.
	 * @param available nueva disponibilidad
	 */
	public void setAvailable(boolean available) {
		this.available = available;
	}

	/**
	 * Obtiene el stock actual del producto.
	 * @return cantidad en stock
	 */
	public int getStock() {
		return stock;
	}

	/**
	 * Establece el stock del producto.
	 * @param stock nueva cantidad en stock
	 */
	public void setStock(int stock) {
		this.stock = stock;
	}

	/**
	 * Obtiene el total de productos creados.
	 * @return contador total de productos
	 */
	public static int getTotalProducts() {
		return totalProducts;
	}

	/**
	 * Establece el contador total de productos.
	 * @param totalProducts nuevo contador
	 */
	public static void setTotalProducts(int totalProducts) {
		Product.totalProducts = totalProducts;
	}

	/**
	 * Aplica descuento por caducidad próxima.
	 * Reduce el precio público según EXPIRATION_RATE (40% descuento).
	 */
	public void expire() {
		setPrice(getPrice() * EXPIRATION_RATE);
	}

	/**
	 * Representación en cadena del producto.
	 * @return cadena con los datos del producto
	 */
	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", price=" + getPublicPrice() + ", available=" + available
				+ ", stock=" + stock + "]";
	}

}
