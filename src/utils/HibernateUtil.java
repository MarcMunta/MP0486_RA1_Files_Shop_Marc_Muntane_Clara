package utils;

import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Inicializa y expone un SessionFactory único para Hibernate.
 */
public final class HibernateUtil {

	private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

	private HibernateUtil() {
	}

	public static SessionFactory getSessionFactory() {
		return SESSION_FACTORY;
	}

	public static void shutdown() {
		if (SESSION_FACTORY != null) {
			SESSION_FACTORY.close();
		}
	}

	private static SessionFactory buildSessionFactory() {
		try {
			// Carga configuración desde hibernate.cfg.xml
			Configuration configuration;
			try {
				configuration = new Configuration().configure();
			} catch (Exception ex) {
				// Fallback útil cuando src/main/resources no está en el classpath (sin Maven)
				configuration = new Configuration().configure(new File("src/main/resources/hibernate.cfg.xml"));
			}
			SessionFactory sessionFactory = configuration.buildSessionFactory();

			Runtime.getRuntime().addShutdownHook(new Thread(HibernateUtil::shutdown));
			return sessionFactory;
		} catch (Exception ex) {
			throw new ExceptionInInitializerError("Error inicializando Hibernate: " + ex.getMessage());
		}
	}
}
