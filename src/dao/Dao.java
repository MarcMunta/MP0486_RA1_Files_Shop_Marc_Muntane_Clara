package dao;

import java.util.ArrayList;

import model.Employee;
import model.Product;

/**
 * Interfaz DAO (Data Access Object) que define las operaciones de persistencia.
 * Abstrae el acceso a datos permitiendo diferentes implementaciones (JDBC, File, etc).
 * 
 * @author Marc Muntané Clarà
 * @version 2.0
 */
public interface Dao {
	
	/**
	 * Establece la conexión con el origen de datos.
	 */
	public void connect();

	/**
	 * Cierra la conexión con el origen de datos.
	 */
	public void disconnect();

	/**
	 * Obtiene un empleado validando sus credenciales.
	 * 
	 * @param employeeId identificador del empleado
	 * @param password contraseña del empleado
	 * @return Employee si las credenciales son válidas, null en caso contrario
	 */
	public Employee getEmployee(int employeeId, String password);
	
	/**
	 * Recupera todos los productos del inventario.
	 * 
	 * @return ArrayList con todos los productos disponibles
	 */
	public ArrayList<Product> getInventory();

	/**
	 * Exporta el inventario a almacenamiento histórico.
	 * 
	 * @param inventory lista de productos a exportar
	 * @return true si la operación fue exitosa, false en caso contrario
	 */
	public boolean writeInventory(ArrayList<Product> inventory);
	
	/**
	 * Añade un nuevo producto al almacenamiento.
	 * 
	 * @param product el producto a insertar
	 */
	public void addProduct(Product product);
	
	/**
	 * Actualiza los datos de un producto existente.
	 * 
	 * @param product el producto con los datos actualizados
	 */
	public void updateProduct(Product product);
	
	/**
	 * Elimina un producto del almacenamiento por su identificador.
	 * 
	 * @param productId identificador único del producto a eliminar
	 */
	public void deleteProduct(int productId);
}