package dao;

/**
 * Factoría simple para elegir implementación del DAO.
 *
 * Por defecto usa MongoDB.
 * Puedes cambiarlo con: -Dshop.dao=mongo | jdbc | file | hibernate
 */
public final class DaoFactory {

	private DaoFactory() {
	}

	public static Dao createDao() {
		String configured = System.getProperty("shop.dao", "mongo").trim().toLowerCase();
		return switch (configured) {
		case "mongo" -> new DaoImplMongoDB();
		case "jdbc" -> new DaoImplJDBC();
		case "file" -> new DaoImplFile();
		case "hibernate" -> new DaoImplHibernate();
		default -> new DaoImplMongoDB();
		};
	}
}
