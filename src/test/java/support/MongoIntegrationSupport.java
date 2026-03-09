package support;

import java.io.IOException;
import java.net.ServerSocket;

import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * Base para pruebas de integracion con MongoDB embebido.
 */
public abstract class MongoIntegrationSupport {

	private static MongodExecutable mongodExecutable;
	private static MongodProcess mongodProcess;

	protected static int mongoPort;
	protected static String connectionString;
	protected static String databaseName;

	@BeforeAll
	static void startMongoServer() throws IOException {
		mongoPort = randomPort();
		connectionString = "mongodb://localhost:" + mongoPort;
		databaseName = "shop_test_" + System.nanoTime();

		MongodConfig config = MongodConfig.builder()
				.version(Version.Main.V6_0)
				.net(new Net(mongoPort, Network.localhostIsIPv6()))
				.build();

		mongodExecutable = MongodStarter.getDefaultInstance().prepare(config);
		mongodProcess = mongodExecutable.start();

		System.setProperty("shop.dao", "mongo");
		System.setProperty("shop.mongo.connectionString", connectionString);
		System.setProperty("shop.mongo.database", databaseName);
		System.setProperty("shop.mongo.autoSeed", "true");
	}

	@BeforeEach
	void resetDatabase() {
		try (MongoClient client = MongoClients.create(connectionString)) {
			client.getDatabase(databaseName).drop();
		}
	}

	@AfterAll
	static void stopMongoServer() {
		System.clearProperty("shop.dao");
		System.clearProperty("shop.mongo.connectionString");
		System.clearProperty("shop.mongo.database");
		System.clearProperty("shop.mongo.autoSeed");

		if (mongodProcess != null) {
			mongodProcess.stop();
		}
		if (mongodExecutable != null) {
			mongodExecutable.stop();
		}
	}

	protected MongoCollection<Document> collection(String name) {
		MongoClient client = MongoClients.create(connectionString);
		MongoDatabase database = client.getDatabase(databaseName);
		return database.getCollection(name);
	}

	protected Document findFirst(String collectionName, String field, Object value) {
		try (MongoClient client = MongoClients.create(connectionString)) {
			return client.getDatabase(databaseName).getCollection(collectionName)
					.find(new Document(field, value))
					.first();
		}
	}

	private static int randomPort() throws IOException {
		try (ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		}
	}
}
