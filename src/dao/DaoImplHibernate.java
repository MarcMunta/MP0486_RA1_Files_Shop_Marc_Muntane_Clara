package dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import model.Employee;
import model.Product;
import model.ProductHistory;
import utils.HibernateUtil;

/**
 * Implementación Hibernate del patrón DAO.
 * Mantiene la misma interfaz que DaoImplJDBC, pero usando ORM.
 */
public class DaoImplHibernate implements Dao {

	private final SessionFactory sessionFactory;

	public DaoImplHibernate() {
		this.sessionFactory = HibernateUtil.getSessionFactory();
	}

	@Override
	public void connect() {
		// SessionFactory ya está inicializado de forma perezosa en HibernateUtil.
		HibernateUtil.getSessionFactory();
	}

	@Override
	public void disconnect() {
		// No cerramos el SessionFactory aquí: sería demasiado costoso por operación.
		// Se cierra con el shutdown hook de HibernateUtil.
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		try (Session session = sessionFactory.openSession()) {
			return session
					.createQuery(
							"from Employee e where e.employeeId = :employeeId and e.password = :password",
							Employee.class)
					.setParameter("employeeId", employeeId)
					.setParameter("password", password)
					.uniqueResult();
		}
	}

	@Override
	public ArrayList<Product> getInventory() {
		try (Session session = sessionFactory.openSession()) {
			List<Product> products = session.createQuery("from Product", Product.class).list();
			return new ArrayList<>(products);
		}
	}

	@Override
	public boolean writeInventory(ArrayList<Product> inventory) {
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			for (Product product : inventory) {
				ProductHistory row = ProductHistory.fromProduct(product);
				session.persist(row);
			}
			tx.commit();
			return true;
		} catch (Exception ex) {
			if (tx != null) {
				tx.rollback();
			}
			System.err.println("Error exportando inventario histórico (Hibernate)");
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public void addProduct(Product product) {
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			session.persist(product);
			tx.commit();
		} catch (Exception ex) {
			if (tx != null) {
				tx.rollback();
			}
			System.err.println("Error insertando producto (Hibernate)");
			ex.printStackTrace();
		}
	}

	@Override
	public void updateProduct(Product product) {
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			session.merge(product);
			tx.commit();
		} catch (Exception ex) {
			if (tx != null) {
				tx.rollback();
			}
			System.err.println("Error actualizando producto (Hibernate)");
			ex.printStackTrace();
		}
	}

	@Override
	public void deleteProduct(int productId) {
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			Product product = session.get(Product.class, productId);
			if (product != null) {
				session.remove(product);
			}
			tx.commit();
		} catch (Exception ex) {
			if (tx != null) {
				tx.rollback();
			}
			System.err.println("Error eliminando producto (Hibernate)");
			ex.printStackTrace();
		}
	}
}
