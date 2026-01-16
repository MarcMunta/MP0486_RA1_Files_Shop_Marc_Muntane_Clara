package dao;

/**
 * Factoría simple para elegir implementación del DAO.
 *
 * Por defecto usa Hibernate.
 * Puedes cambiarlo con: -Dshop.dao=jdbc | file | hibernate
 */
public final class DaoFactory {

	private DaoFactory() {
	}

	public static Dao createDao() {
		String configured = System.getProperty("shop.dao", "hibernate").trim().toLowerCase();
		return switch (configured) {
		case "jdbc" -> new DaoImplJDBC();
		case "file" -> new DaoImplFile();
		case "hibernate" -> new DaoImplHibernate();
		default -> new DaoImplHibernate();
		};
	}
}
