package utils;

import static com.mongodb.client.model.Indexes.ascending;

import java.util.List;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

/**
 * Utilidades compartidas para la persistencia en MongoDB.
 */
public final class MongoSupport {

	public static final String INVENTORY_COLLECTION = "inventory";
	public static final String HISTORY_COLLECTION = "historical_inventory";
	public static final String USERS_COLLECTION = "users";

	private static final String DEFAULT_CONNECTION = "mongodb://localhost:27017";
	private static final String DEFAULT_DATABASE = "shop";
	private static final String DEFAULT_CURRENCY = "\u20ac";

	private MongoSupport() {
	}

	public static MongoClient createClient() {
		ConnectionString connectionString = new ConnectionString(getConnectionString());
		MongoClientSettings settings = MongoClientSettings.builder()
				.applyConnectionString(connectionString)
				.applyToClusterSettings(builder -> builder.serverSelectionTimeout(3, java.util.concurrent.TimeUnit.SECONDS))
				.build();
		return MongoClients.create(settings);
	}

	public static String getConnectionString() {
		return System.getProperty("shop.mongo.connectionString", DEFAULT_CONNECTION);
	}

	public static String getDatabaseName() {
		return System.getProperty("shop.mongo.database", DEFAULT_DATABASE);
	}

	public static boolean isAutoSeedEnabled() {
		return Boolean.parseBoolean(System.getProperty("shop.mongo.autoSeed", "true"));
	}

	public static boolean isEmbeddedFallbackEnabled() {
		return Boolean.parseBoolean(System.getProperty("shop.mongo.embedded", "true"));
	}

	public static boolean isLocalConnection() {
		ConnectionString connectionString = new ConnectionString(getConnectionString());
		if (connectionString.getHosts().isEmpty()) {
			return true;
		}
		String host = connectionString.getHosts().getFirst();
		return host.startsWith("localhost") || host.startsWith("127.0.0.1");
	}

	public static void initializeDatabase(MongoDatabase database) {
		MongoCollection<Document> inventory = database.getCollection(INVENTORY_COLLECTION);
		MongoCollection<Document> users = database.getCollection(USERS_COLLECTION);

		inventory.createIndex(ascending("id"), new IndexOptions().unique(true));
		users.createIndex(ascending("employeeId"), new IndexOptions().unique(true));

		if (isAutoSeedEnabled()) {
			seedInventoryIfEmpty(inventory);
			seedUsersIfEmpty(users);
		}
	}

	private static void seedInventoryIfEmpty(MongoCollection<Document> collection) {
		if (collection.countDocuments() > 0) {
			return;
		}

		List<Document> documents = List.of(
				inventoryDocument(1, "Manzana", 10.0, true, 50),
				inventoryDocument(2, "Pera", 15.0, true, 30),
				inventoryDocument(3, "Hamburguesa", 25.0, true, 20),
				inventoryDocument(4, "Fresa", 8.0, true, 40),
				inventoryDocument(5, "Leche", 5.0, true, 100));
		collection.insertMany(documents);
	}

	private static void seedUsersIfEmpty(MongoCollection<Document> collection) {
		if (collection.countDocuments() > 0) {
			return;
		}

		collection.insertMany(List.of(
				new Document("employeeId", 1).append("name", "Admin").append("password", "1234"),
				new Document("employeeId", 123).append("name", "Test").append("password", "test")));
	}

	public static Document inventoryDocument(int id, String name, double price, boolean available, int stock) {
		return new Document("id", id)
				.append("name", name)
				.append("wholesalePrice", priceDocument(price))
				.append("available", available)
				.append("stock", stock);
	}

	public static Document priceDocument(double value) {
		return new Document("value", value).append("currency", DEFAULT_CURRENCY);
	}
}
