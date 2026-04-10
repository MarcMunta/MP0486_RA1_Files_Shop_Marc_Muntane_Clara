package dao;

/**
 * Factoría simple para elegir implementación del DAO.
 *
 * Por defecto usa MongoDB.
	* Puedes cambiarlo con: -Dshop.dao=mongo | jdbc | file | hibernate | objectdb
 */
public final class DaoFactory {

	private DaoFactory() {
	}

	public static Dao createDao() {
		String configured = System.getProperty("shop.dao", "mongo").trim().toLowerCase();
		return fromAlias(configured, "mongo");
	}

	/**
	 * DAO específico para autenticación.
	 * Por defecto usa ObjectDB para cumplir el requisito funcional de login.
	 */
	public static Dao createLoginDao() {
		String configured = System.getProperty("shop.login.dao", "objectdb").trim().toLowerCase();
		return fromAlias(configured, "objectdb");
	}

	private static Dao fromAlias(String configured, String fallback) {
		return switch (configured) {
		case "mongo" -> new DaoImplMongoDB();
		case "jdbc" -> new DaoImplJDBC();
		case "file" -> new DaoImplFile();
		case "hibernate" -> new DaoImplHibernate();
		case "objectdb" -> new DaoImplObjectDB();
		default -> fromAlias(fallback, fallback);
		};
	}
}
