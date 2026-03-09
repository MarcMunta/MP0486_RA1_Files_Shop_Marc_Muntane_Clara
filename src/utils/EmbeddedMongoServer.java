package utils;

import java.io.IOException;

import com.mongodb.ConnectionString;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * Arranca un servidor MongoDB embebido cuando no hay una instancia local disponible.
 */
public final class EmbeddedMongoServer {

	private static MongodExecutable executable;
	private static MongodProcess process;

	private EmbeddedMongoServer() {
	}

	public static synchronized void ensureStarted(String connectionString) {
		if (process != null && process.isProcessRunning()) {
			return;
		}

		ConnectionString parsed = new ConnectionString(connectionString);
		int port = extractPort(parsed);

		try {
			MongodConfig config = MongodConfig.builder()
					.version(Version.Main.V6_0)
					.net(new Net(port, Network.localhostIsIPv6()))
					.build();

			executable = MongodStarter.getDefaultInstance().prepare(config);
			process = executable.start();
			Runtime.getRuntime().addShutdownHook(new Thread(EmbeddedMongoServer::stop));
		} catch (IOException ex) {
			throw new IllegalStateException("No se ha podido arrancar MongoDB embebido", ex);
		}
	}

	public static synchronized void stop() {
		if (process != null) {
			process.stop();
			process = null;
		}
		if (executable != null) {
			executable.stop();
			executable = null;
		}
	}

	private static int extractPort(ConnectionString connectionString) {
		String firstHost = connectionString.getHosts().isEmpty() ? "localhost:27017" : connectionString.getHosts().getFirst();
		int colon = firstHost.lastIndexOf(':');
		if (colon > -1) {
			return Integer.parseInt(firstHost.substring(colon + 1));
		}
		return 27017;
	}
}
