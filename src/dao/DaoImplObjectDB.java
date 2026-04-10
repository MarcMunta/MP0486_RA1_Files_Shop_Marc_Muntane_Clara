package dao;

import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import model.Employee;
import model.Product;
import utils.ObjectDbSupport;

/**
 * Implementacion DAO para autenticacion en ObjectDB.
 *
 * El inventario y las operaciones de mantenimiento se mantienen en MongoDB.
 */
public class DaoImplObjectDB implements Dao {

	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;
	private final Dao inventoryDao;

	public DaoImplObjectDB() {
		this(new DaoImplMongoDB());
	}

	DaoImplObjectDB(Dao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}

	@Override
	public void connect() {
		if (entityManagerFactory != null && entityManagerFactory.isOpen()
				&& entityManager != null && entityManager.isOpen()) {
			return;
		}

		entityManagerFactory = ObjectDbSupport.createEntityManagerFactory();
		entityManager = entityManagerFactory.createEntityManager();
		ObjectDbSupport.seedUsersIfEmpty(entityManager);
	}

	@Override
	public void disconnect() {
		if (entityManager != null && entityManager.isOpen()) {
			entityManager.close();
		}
		if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
			entityManagerFactory.close();
		}
		entityManager = null;
		entityManagerFactory = null;
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		if (entityManager == null || !entityManager.isOpen()) {
			connect();
		}

		try {
			Employee user = entityManager.find(Employee.class, employeeId);
			if (user == null || !password.equals(user.getPassword())) {
				return null;
			}
			return new Employee(user.getEmployeeId(), user.getName(), user.getPassword());
		} catch (Exception ex) {
			System.err.println("Error consultando usuario en ObjectDB");
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public ArrayList<Product> getInventory() {
		return inventoryDao.getInventory();
	}

	@Override
	public boolean writeInventory(ArrayList<Product> inventory) {
		return inventoryDao.writeInventory(inventory);
	}

	@Override
	public void addProduct(Product product) {
		inventoryDao.addProduct(product);
	}

	@Override
	public void updateProduct(Product product) {
		inventoryDao.updateProduct(product);
	}

	@Override
	public void deleteProduct(int productId) {
		inventoryDao.deleteProduct(productId);
	}
}
