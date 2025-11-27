package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import dao.DaoImplJDBC;
import model.Amount;
import model.Client;
import model.Employee;
import model.Product;
import model.Sale;

/**
 * Clase principal que representa la tienda.
 * Gestiona el inventario de productos, las ventas y la caja.
 * Utiliza persistencia JDBC para sincronizar datos con la base de datos MySQL.
 * 
 * @author Marc Muntané Clarà
 * @version 2.0
 */
public class Shop {
	
	// ==================== ATRIBUTOS ====================
	
	/** Dinero disponible en caja */
	private Amount cash = new Amount(100.00);
	
	/** Lista de productos del inventario */
	private ArrayList<Product> inventory;
	
	/** Contador de productos en inventario */
	private int productCount;
	
	/** Lista de ventas realizadas */
	private ArrayList<Sale> sales;
	
	/** Contador de ventas */
	private int saleCount;
	
	/** DAO JDBC para persistencia de datos en base de datos */
	private final DaoImplJDBC dao = new DaoImplJDBC();

	/** Tasa de impuestos aplicada a las ventas */
	private static final double TAX_RATE = 1.04;
	
	/** Número máximo de productos permitidos en inventario */
	private static final int MAX_INVENTORY_SIZE = 10;

	// ==================== CONSTRUCTOR ====================
	
	/**
	 * Constructor por defecto.
	 * Inicializa las listas de inventario y ventas vacías.
	 */
	public Shop() {
		this.inventory = new ArrayList<>();
		this.sales = new ArrayList<>();
	}
	
	

	// ==================== GETTERS Y SETTERS ====================
	
	/**
	 * Obtiene el dinero actual en caja.
	 * @return Amount con el valor de caja
	 */
	public Amount getCash() {
		return this.cash;
	}

	/**
	 * Establece el dinero en caja.
	 * @param cash nuevo valor de caja
	 */
	public void setCash(Amount cash) {
		this.cash = cash;
	}

	/**
	 * Obtiene la lista completa del inventario.
	 * @return ArrayList con todos los productos
	 */
	public ArrayList<Product> getInventory() {
		return this.inventory;
	}

	/**
	 * Establece el inventario completo.
	 * @param inventory lista de productos a establecer
	 */
	public void setInventory(ArrayList<Product> inventory) {
		this.inventory = inventory;
	}

	/**
	 * Obtiene el número de productos en inventario.
	 * @return cantidad de productos
	 */
	public int getProductCount() {
		return this.productCount;
	}

	/**
	 * Establece el contador de productos.
	 * @param productCount nuevo contador
	 */
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

	/**
	 * Obtiene la lista de ventas realizadas.
	 * @return ArrayList con todas las ventas
	 */
	public ArrayList<Sale> getSales() {
		return this.sales;
	}

	/**
	 * Establece la lista de ventas.
	 * @param sales lista de ventas a establecer
	 */
	public void setSales(ArrayList<Sale> sales) {
		this.sales = sales;
	}

	/**
	 * Obtiene el número de ventas realizadas.
	 * @return cantidad de ventas
	 */
	public int getSaleCount() {
		return this.saleCount;
	}

	/**
	 * Establece el contador de ventas.
	 * @param saleCount nuevo contador
	 */
	public void setSaleCount(int saleCount) {
		this.saleCount = saleCount;
	}



	// ==================== MÉTODO PRINCIPAL ====================
	
	/**
	 * Punto de entrada de la aplicación.
	 * Inicializa la tienda, carga el inventario y muestra el menú principal.
	 * @param args argumentos de línea de comandos (no utilizados)
	 */
	public static void main(String[] args) {
		Shop shop = new Shop();

		// Cargar inventario desde base de datos
		shop.initializeInventory();
		
		// Iniciar sesión de empleado
		shop.authenticateEmployee();

		// Configurar scanner para entrada de usuario
		Scanner inputScanner = new Scanner(System.in);
		int menuOption;
		boolean exitRequested = false;

		do {
			System.out.println("\n");
			System.out.println("===========================");
			System.out.println("Menu principal miTienda.com");
			System.out.println("===========================");
			System.out.println("1) Contar caja");
			System.out.println("2) Añadir producto");
			System.out.println("3) Añadir stock");
			System.out.println("4) Marcar producto proxima caducidad");
			System.out.println("5) Ver inventario");
			System.out.println("6) Venta");
			System.out.println("7) Ver ventas");
			System.out.println("8) Ver venta total");
			System.out.println("9) Eliminar producto");
			System.out.println("10) Salir programa");
			System.out.print("Seleccione una opción: ");
			menuOption = inputScanner.nextInt();

			switch (menuOption) {
			case 1:
				shop.showCash();
				break;

			case 2:
				shop.addProduct();
				break;

			case 3:
				shop.addStock();
				break;

			case 4:
				shop.setExpired();
				break;

			case 5:
				shop.showInventory();
				break;

			case 6:
				shop.sale();
				break;

			case 7:
				shop.showSales();
				break;

			case 8:
				shop.showSalesAmount();
				break;

			case 9:
				shop.removeProduct();
				break;

			case 10:
				System.out.println("Cerrando programa ...");
				exitRequested = true;
				break;
			}

		} while (!exitRequested);

	}

	/**
	 * Autentica al empleado solicitando credenciales por consola.
	 * El proceso se repite hasta que las credenciales sean válidas.
	 */
	private void authenticateEmployee() {
		// Crear instancia temporal de empleado para validación
		Employee employee = new Employee("test");
		boolean isAuthenticated = false;
		
		do {
			Scanner credentialScanner = new Scanner(System.in);
			System.out.println("Introduzca numero de empleado: ");
			int employeeId = credentialScanner.nextInt();
			
			System.out.println("Introduzca contraseña: ");
			String password = credentialScanner.next();
			
			isAuthenticated = employee.login(employeeId, password);
			if (isAuthenticated) {
				System.out.println("Login correcto ");
			} else {
				System.out.println("Usuario o password incorrectos ");
			}
		} while (!isAuthenticated);
	}

	/**
	 * Inicializa el inventario de la tienda.
	 * Carga los productos desde la base de datos SQL mediante el repositorio JDBC.
	 */
	public void initializeInventory() {
		// Leer inventario desde base de datos SQL
		this.fetchInventoryFromDatabase();
	}

	/**
	 * Recupera el inventario desde la tabla SQL para garantizar que la aplicación
	 * arranca sincronizada con la base de datos.
	 * Actualiza la lista de inventario y el contador de productos.
	 */
	private void fetchInventoryFromDatabase() {
		setInventory(this.dao.getInventory());
		this.productCount = inventory.size();
	}
	
	/**
	 * Exporta el inventario actual a la tabla histórica de la base de datos.
	 * @return true si la exportación fue exitosa, false en caso contrario
	 */
	public Boolean exportInventoryToDatabase() {
		return this.dao.writeInventory(inventory);
	}


	/**
	 * show current total cash
	 */
	private void showCash() {
		System.out.println("Dinero actual: " + cash);
	}

	/**addProduct
	 * add a new product to inventory getting data from console
	 */
	public void addProduct() {
		if (isInventoryFull()) {
			System.out.println("No se pueden añadir más productos");
			return;
		}
		Scanner scanner = new Scanner(System.in);
		System.out.print("Nombre: ");
		String name = scanner.nextLine();
		System.out.print("Precio mayorista: ");
		double wholesalerPrice = scanner.nextDouble();
		System.out.print("Stock: ");
		int stock = scanner.nextInt();

		addProduct(new Product(getNextProductId(), name, new Amount(wholesalerPrice), true, stock));
	}

	/**
	 * Elimina un producto del inventario solicitando el nombre por consola.
	 * El producto se elimina tanto de la memoria como de la base de datos.
	 */
	public void removeProduct() {
		if (inventory.size() == 0) {
			System.out.println("No se pueden eliminar productos, inventario vacio");
			return;
		}
		Scanner productScanner = new Scanner(System.in);
		System.out.print("Seleccione un nombre de producto: ");
		String productName = productScanner.next();
		Product product = findProduct(productName);

		if (product != null) {
			// Eliminar de inventario local y base de datos
			if (inventory.remove(product)) {
				this.dao.deleteProduct(product.getId());
				this.productCount = inventory.size();
				System.out.println("El producto " + productName + " ha sido eliminado");

			} else {
				System.out.println("No se ha encontrado el producto con nombre " + productName);
			}
		} else {
			System.out.println("No se ha encontrado el producto con nombre " + productName);
		}
	}

	/**
	 * Añade stock a un producto específico.
	 * Solicita el nombre del producto y la cantidad a añadir por consola.
	 * Persiste el cambio en la base de datos.
	 */
	public void addStock() {
		Scanner stockScanner = new Scanner(System.in);
		System.out.print("Seleccione un nombre de producto: ");
		String productName = stockScanner.next();
		Product product = findProduct(productName);

		if (product != null) {
			// Solicitar cantidad a añadir
			System.out.print("Seleccione la cantidad a añadir: ");
			int stockQuantity = stockScanner.nextInt();
			// Actualizar stock y persistir en base de datos
			product.setStock(product.getStock() + stockQuantity);
			this.dao.updateProduct(product);
			System.out.println("El stock del producto " + productName + " ha sido actualizado a " + product.getStock());

		} else {
			System.out.println("No se ha encontrado el producto con nombre " + productName);
		}
	}

	/**
	 * Marca un producto como próximo a caducar.
	 * Aplica descuento automático y persiste el cambio en la base de datos.
	 */
	private void setExpired() {
		Scanner expiryScanner = new Scanner(System.in);
		System.out.print("Seleccione un nombre de producto: ");
		String productName = expiryScanner.next();

		Product product = findProduct(productName);

		if (product != null) {
			// Aplicar descuento por caducidad y persistir
			product.expire();
			this.dao.updateProduct(product);
			System.out.println("El precio del producto " + productName + " ha sido actualizado a " + product.getPublicPrice());
		}
	}

	/**
	 * show all inventory
	 */
	public void showInventory() {
		System.out.println("Contenido actual de la tienda:");
		for (Product product : inventory) {
			if (product != null) {
				System.out.println(product);
			}
		}
	}

	/**
	 * Realiza una venta de productos a un cliente.
	 * Permite añadir múltiples productos al carrito y procesa el pago.
	 * Actualiza el stock en memoria y en la base de datos.
	 */
	public void sale() {
		// Solicitar nombre del cliente
		Scanner saleScanner = new Scanner(System.in);
		System.out.println("Realizar venta, escribir nombre cliente");
		String clientName = saleScanner.nextLine();
		Client client = new Client(clientName);

		// Carrito de compra para almacenar productos
		ArrayList<Product> shoppingCart = new ArrayList<Product>();
		int cartItemCount = 0;

		Amount totalAmount = new Amount(0.0);
		String productName = "";
		while (!productName.equals("0")) {
			System.out.println("Introduce el nombre del producto, escribir 0 para terminar:");
			productName = saleScanner.nextLine();

			if (productName.equals("0")) {
				break;
			}
			Product product = findProduct(productName);
			boolean isProductAvailable = false;

			if (product != null && product.isAvailable()) {
				isProductAvailable = true;
				totalAmount.setValue(totalAmount.getValue() + product.getPublicPrice().getValue());
				product.setStock(product.getStock() - 1);
				shoppingCart.add(product);
				cartItemCount++;
				// Si no hay más stock, marcar como no disponible
				if (product.getStock() == 0) {
					product.setAvailable(false);
				}
				// Persistir cambios en base de datos
				this.dao.updateProduct(product);
				System.out.println("Producto añadido con éxito");
			}

			if (!isProductAvailable) {
				System.out.println("Producto no encontrado o sin stock");
			}
		}

		totalAmount.setValue(totalAmount.getValue() * TAX_RATE);
		// show cost total
		System.out.println("Venta realizada con éxito, total: " + totalAmount);
		
		// make payment
		if(!client.pay(totalAmount)) {
			System.out.println("Cliente debe: " + client.getBalance());;
		}

		// create sale
		Sale sale = new Sale(client, shoppingCart, totalAmount);

		// add to shop
		sales.add(sale);
//		numberSales++;

		// add to cash
		cash.setValue(cash.getValue() + totalAmount.getValue());
	}

	/**
	 * show all sales
	 */
	private void showSales() {
		System.out.println("Lista de ventas:");
		for (Sale sale : sales) {
			if (sale != null) {
				System.out.println(sale);
			}
		}
		
		// ask for client name
		Scanner sc = new Scanner(System.in);
		System.out.println("Exportar fichero ventas? S / N");
		String option = sc.nextLine();
		if ("S".equalsIgnoreCase(option)) {
			this.writeSales();
		} 
		
	}

	/**
	 * write in file the sales done
	 */
	private void writeSales() {
		// define file name based on date
		LocalDate myObj = LocalDate.now();
		String fileName = "sales_" + myObj.toString() + ".txt";
		
		// locate file, path and name
		File f = new File(System.getProperty("user.dir") + File.separator + "files" + File.separator + fileName);
				
		try {
			// wrap in proper classes
			FileWriter fw;
			fw = new FileWriter(f, true);
			PrintWriter pw = new PrintWriter(fw);
			
			// write line by line
			int counterSale=1;
			for (Sale sale : sales) {				
				// format first line TO BE -> 1;Client=PERE;Date=29-02-2024 12:49:50;
				StringBuilder firstLine = new StringBuilder(counterSale+";Client="+sale.getClient()+";Date=" + sale.formatDate()+";");
				pw.write(firstLine.toString());
				fw.write("\n");
				
				// format second line TO BE -> 1;Products=Manzana,20.0€;Fresa,10.0€;Hamburguesa,60.0€;
				// build products line
				StringBuilder productLine= new StringBuilder();
				for (Product product : sale.getProducts()) {
					productLine.append(product.getName()+ "," + product.getPublicPrice()+";");
				}
				StringBuilder secondLine = new StringBuilder(counterSale+ ";" + "Products=" + productLine +";");						                                                
				pw.write(secondLine.toString());	
				fw.write("\n");
				
				// format third line TO BE -> 1;Amount=93.60€;
				StringBuilder thirdLine = new StringBuilder(counterSale+ ";" + "Amount=" + sale.getAmount() +";");						                                                
				pw.write(thirdLine.toString());	
				fw.write("\n");
				
				// increment counter sales
				counterSale++;
			}
			// close files
			pw.close();
			fw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * show total amount all sales
	 */
	private void showSalesAmount() {
		Amount totalAmount = new Amount(0.0);
		for (Sale sale : sales) {
			if (sale != null) {
				totalAmount.setValue(totalAmount.getValue() + sale.getAmount().getValue());
			}
		}
		System.out.println("Total cantidad ventas:");
		System.out.println(totalAmount);
	}

	/**
	 * Añade un producto al inventario y lo persiste en la base de datos.
	 * Verifica que no se haya alcanzado el límite máximo de productos.
	 * 
	 * @param product el producto a añadir
	 */
	public void addProduct(Product product) {
		if (isInventoryFull()) {
			System.out.println("No se pueden añadir más productos, se ha alcanzado el máximo de " + inventory.size());
			return;
		}
		// Añadir a inventario local y persistir en BD
		inventory.add(product);
		dao.addProduct(product);
		productCount = inventory.size();
	}
	
	

	/**
	 * Verifica si el inventario está lleno.
	 * @return true si se alcanzó el límite máximo de productos
	 */
	public boolean isInventoryFull() {
		return productCount >= MAX_INVENTORY_SIZE;
	}

	/**
	 * find product by name
	 * 
	 * @param product name
	 */
	public Product findProduct(String name) {
		for (int i = 0; i < inventory.size(); i++) {
			if (inventory.get(i) != null && inventory.get(i).getName().equalsIgnoreCase(name)) {
				return inventory.get(i);
			}
		}
		return null;

	}
	
	/**
	 * Actualiza un producto en el inventario y persiste los cambios.
	 * 
	 * @param product el producto con los datos actualizados
	 */
	public void updateProduct(Product product) {
		this.dao.updateProduct(product);
	}
	
	/**
	 * Elimina un producto del inventario por su identificador.
	 * El producto se elimina tanto de memoria como de la base de datos.
	 * 
	 * @param id identificador único del producto a eliminar
	 */
	public void deleteProduct(int id) {
		this.dao.deleteProduct(id);
		inventory.removeIf(product -> product.getId() == id);
		productCount = inventory.size();
	}

	/**
	 * Genera el próximo identificador disponible tomando como referencia los
	 * productos cargados actualmente.
	 */
	public int getNextProductId() {
		return inventory.stream().mapToInt(Product::getId).max().orElse(0) + 1;
	}
}
