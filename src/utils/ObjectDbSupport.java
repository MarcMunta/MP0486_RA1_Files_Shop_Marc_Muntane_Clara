package utils;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import model.Employee;

/**
 * Utilidades compartidas para la autenticacion en ObjectDB.
 */
public final class ObjectDbSupport {

	private static final String DEFAULT_USERS_DB = "objects/users.odb";

	private ObjectDbSupport() {
	}

	public static String getUsersDatabasePath() {
		return System.getProperty("shop.objectdb.usersPath", DEFAULT_USERS_DB);
	}

	public static String getUsersDatabaseUrl() {
		Path dbPath = ensureDatabasePath();
		String normalized = dbPath.toAbsolutePath().normalize().toString().replace('\\', '/');
		return "objectdb:" + normalized;
	}

	public static EntityManagerFactory createEntityManagerFactory() {
		return Persistence.createEntityManagerFactory(getUsersDatabaseUrl());
	}

	public static void seedUsersIfEmpty(EntityManager entityManager) {
		Employee admin = entityManager.find(Employee.class, 1);
		if (admin != null) {
			return;
		}

		entityManager.getTransaction().begin();
		try {
			entityManager.persist(new Employee(1, "Admin", "1234"));
			entityManager.persist(new Employee(123, "Test", "test"));
			entityManager.getTransaction().commit();
		} catch (RuntimeException ex) {
			if (entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().rollback();
			}
			throw ex;
		}
	}

	private static Path ensureDatabasePath() {
		Path dbPath = Path.of(getUsersDatabasePath());
		Path parent = dbPath.getParent();
		if (parent != null) {
			try {
				Files.createDirectories(parent);
			} catch (Exception ex) {
				throw new IllegalStateException("No se pudo crear la carpeta de ObjectDB: " + parent, ex);
			}
		}
		return dbPath;
	}
}
