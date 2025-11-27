package model;

/**
 * Clase que representa un producto del inventario de la tienda.
 * Contiene información sobre precio, stock y disponibilidad.
 * 
 * @author Marc Muntané Clarà
 * @version 2.0
 */
public class Product {
	
	/** Identificador único del producto */
	private int id;
	
	/** Nombre del producto */
	private String name;
	
	/** Precio de venta al público (wholesalerPrice * 2) */
	private Amount publicPrice;
	
	/** Precio mayorista de compra */
	private Amount wholesalerPrice;
	
	/** Indica si el producto está disponible para la venta */
	private boolean available;
	
	/** Cantidad de unidades en stock */
	private int stock;
	
	/** Contador total de productos creados */
	private static int totalProducts;

	/** Tasa de descuento para productos próximos a caducar (40% de descuento) */
	public final static double EXPIRATION_RATE = 0.60;

	/**
	 * Constructor que genera automáticamente el ID del producto.
	 * 
	 * @param name nombre del producto
	 * @param wholesalerPrice precio mayorista
	 * @param available disponibilidad inicial
	 * @param stock cantidad inicial en stock
	 */
	public Product(String name, Amount wholesalerPrice, boolean available, int stock) {
		super();
		this.id = totalProducts + 1;
		this.name = name;
		this.wholesalerPrice = wholesalerPrice;
		this.publicPrice = new Amount(wholesalerPrice.getValue() * 2);
		this.available = available;
		this.stock = stock;
		totalProducts++;
	}

	/**
	 * Constructor con ID especificado (usado para cargar desde BD).
	 * 
	 * @param id identificador único del producto
	 * @param name nombre del producto
	 * @param wholesalerPrice precio mayorista
	 * @param available disponibilidad
	 * @param stock cantidad en stock
	 */
	public Product(int id, String name, Amount wholesalerPrice, boolean available, int stock) {
		super();
		this.id = id;
		this.name = name;
		this.wholesalerPrice = wholesalerPrice;
		this.publicPrice = new Amount(wholesalerPrice.getValue() * 2);
		this.available = available;
		this.stock = stock;
		totalProducts++;
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
		return publicPrice;
	}

	/**
	 * Establece el precio de venta al público.
	 * @param publicPrice nuevo precio público
	 */
	public void setPublicPrice(Amount publicPrice) {
		this.publicPrice = publicPrice;
	}

	/**
	 * Obtiene el precio mayorista.
	 * @return precio mayorista
	 */
	public Amount getWholesalerPrice() {
		return wholesalerPrice;
	}

	/**
	 * Establece el precio mayorista.
	 * @param wholesalerPrice nuevo precio mayorista
	 */
	public void setWholesalerPrice(Amount wholesalerPrice) {
		this.wholesalerPrice = wholesalerPrice;
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
		this.publicPrice.setValue(this.getPublicPrice().getValue() * EXPIRATION_RATE);
	}

	/**
	 * Representación en cadena del producto.
	 * @return cadena con los datos del producto
	 */
	@Override
	public String toString() {
		return "Product [name=" + name + ", publicPrice=" + publicPrice + ", wholesalerPrice=" + wholesalerPrice
				+ ", available=" + available + ", stock=" + stock + "]";
	}

}
