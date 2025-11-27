package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.Amount;
import model.Employee;
import model.Product;

/**
 * Implementacion JDBC del patron DAO para persistencia en base de datos MySQL.
 * Proporciona operaciones CRUD para el inventario de productos y autenticacion de empleados.
 * 
 * @author Marc Muntane Clara
 * @version 2.0
 */
public class DaoImplJDBC implements Dao {

    /** URL de conexion a la base de datos MySQL */
    private static final String DB_URL = "jdbc:mysql://localhost:3306/shop";
    
    /** Usuario de la base de datos */
    private static final String DB_USER = "root";
    
    /** Contrasena de la base de datos */
    private static final String DB_PASS = "";

    /** Conexion activa a la base de datos */
    private Connection connection;

    /**
     * Establece la conexion con la base de datos MySQL.
     * Si ya existe una conexion abierta, no crea una nueva.
     */
    @Override
    public void connect() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            }
        } catch (SQLException e) {
            System.err.println("Error abriendo conexion JDBC");
            e.printStackTrace();
        }
    }

    /**
     * Cierra la conexion con la base de datos.
     * Libera los recursos asociados a la conexion.
     */
    @Override
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error cerrando conexion JDBC");
                e.printStackTrace();
            } finally {
                connection = null;
            }
        }
    }

    /**
     * Obtiene un empleado de la base de datos validando sus credenciales.
     * 
     * @param employeeId identificador del empleado
     * @param password contrasena del empleado
     * @return Employee si las credenciales son validas, null en caso contrario
     */
    @Override
    public Employee getEmployee(int employeeId, String password) {
        Employee employee = null;
        final String query = "SELECT employeeId, name, password FROM employee WHERE employeeId = ? AND password = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, employeeId);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    employee = new Employee(rs.getInt("employeeId"), rs.getString("name"), rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error consultando empleado");
            e.printStackTrace();
        }
        return employee;
    }

    /**
     * Recupera todos los productos del inventario desde la tabla inventory.
     * Abre y cierra la conexion automaticamente.
     * 
     * @return ArrayList con todos los productos del inventario
     */
    @Override
    public ArrayList<Product> getInventory() {
        ArrayList<Product> inventory = new ArrayList<>();
        final String query = "SELECT id, name, wholesalerPrice, available, stock FROM inventory";
        connect();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Amount amount = new Amount(rs.getDouble("wholesalerPrice"));
                Product product = new Product(rs.getInt("id"), rs.getString("name"), amount,
                        rs.getBoolean("available"), rs.getInt("stock"));
                inventory.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Error leyendo inventario");
            e.printStackTrace();
        } finally {
            disconnect();
        }
        return inventory;
    }

    /**
     * Exporta el inventario a la tabla historica historical_inventory.
     * Utiliza batch processing para insertar multiples productos eficientemente.
     * 
     * @param products lista de productos a exportar
     * @return true si la exportacion fue exitosa, false en caso contrario
     */
    @Override
    public boolean writeInventory(ArrayList<Product> products) {
        final String query = "INSERT INTO historical_inventory (id_product, name, wholesalerPrice, available, stock, created_at)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        connect();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            for (Product product : products) {
                ps.setInt(1, product.getId());
                ps.setString(2, product.getName());
                ps.setDouble(3, product.getWholesalerPrice().getValue());
                ps.setBoolean(4, product.isAvailable());
                ps.setInt(5, product.getStock());
                ps.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (SQLException e) {
            System.err.println("Error exportando inventario historico");
            e.printStackTrace();
        } finally {
            disconnect();
        }
        return false;
    }

    /**
     * Inserta un nuevo producto en la tabla inventory.
     * 
     * @param product el producto a insertar
     */
    @Override
    public void addProduct(Product product) {
        final String query = "INSERT INTO inventory (id, name, wholesalerPrice, available, stock) VALUES (?, ?, ?, ?, ?)";
        connect();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, product.getId());
            ps.setString(2, product.getName());
            ps.setDouble(3, product.getWholesalerPrice().getValue());
            ps.setBoolean(4, product.isAvailable());
            ps.setInt(5, product.getStock());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error insertando producto");
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    /**
     * Actualiza el stock y disponibilidad de un producto existente.
     * 
     * @param product el producto con los datos actualizados
     */
    @Override
    public void updateProduct(Product product) {
        final String query = "UPDATE inventory SET stock = ?, available = ? WHERE id = ?";
        connect();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, product.getStock());
            ps.setBoolean(2, product.isAvailable());
            ps.setInt(3, product.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error actualizando producto");
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    /**
     * Elimina un producto de la tabla inventory por su identificador.
     * 
     * @param productId identificador del producto a eliminar
     */
    @Override
    public void deleteProduct(int productId) {
        final String query = "DELETE FROM inventory WHERE id = ?";
        connect();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error eliminando producto");
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }
}
