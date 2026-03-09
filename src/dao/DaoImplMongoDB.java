package dao;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;

import model.Employee;
import model.Product;
import utils.EmbeddedMongoServer;
import utils.MongoSupport;

/**
 * Implementacion DAO para MongoDB.
 */
public class DaoImplMongoDB implements Dao {

	private MongoClient mongoClient;
	private MongoDatabase database;
	private MongoCollection<Document> inventoryCollection;
	private MongoCollection<Document> historyCollection;
	private MongoCollection<Document> usersCollection;

	@Override
	public void connect() {
		if (mongoClient != null) {
			return;
		}

		initializeClient();
		try {
			database.runCommand(new Document("ping", 1));
		} catch (Exception ex) {
			disconnect();
			if (MongoSupport.isEmbeddedFallbackEnabled() && MongoSupport.isLocalConnection()) {
				EmbeddedMongoServer.ensureStarted(MongoSupport.getConnectionString());
				initializeClient();
				database.runCommand(new Document("ping", 1));
			} else {
				throw ex;
			}
		}

		MongoSupport.initializeDatabase(database);
		inventoryCollection = database.getCollection(MongoSupport.INVENTORY_COLLECTION);
		historyCollection = database.getCollection(MongoSupport.HISTORY_COLLECTION);
		usersCollection = database.getCollection(MongoSupport.USERS_COLLECTION);
	}

	@Override
	public void disconnect() {
		if (mongoClient != null) {
			mongoClient.close();
		}
		mongoClient = null;
		database = null;
		inventoryCollection = null;
		historyCollection = null;
		usersCollection = null;
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		connect();
		try {
			Document document = usersCollection.find(eq("employeeId", employeeId))
					.filter(eq("password", password))
					.first();
			if (document == null) {
				return null;
			}
			return new Employee(
					readInt(document, "employeeId"),
					document.getString("name"),
					document.getString("password"));
		} catch (Exception ex) {
			System.err.println("Error consultando usuario en MongoDB");
			ex.printStackTrace();
			return null;
		} finally {
			disconnect();
		}
	}

	@Override
	public ArrayList<Product> getInventory() {
		connect();
		try {
			ArrayList<Product> inventory = new ArrayList<>();
			for (Document document : inventoryCollection.find().sort(ascending("id"))) {
				inventory.add(toProduct(document));
			}
			return inventory;
		} catch (Exception ex) {
			System.err.println("Error leyendo inventario desde MongoDB");
			ex.printStackTrace();
			return new ArrayList<>();
		} finally {
			disconnect();
		}
	}

	@Override
	public boolean writeInventory(ArrayList<Product> products) {
		connect();
		try {
			List<Document> documents = new ArrayList<>();
			for (Product product : products) {
				documents.add(toHistoricalDocument(product));
			}
			if (!documents.isEmpty()) {
				historyCollection.insertMany(documents);
			}
			return true;
		} catch (Exception ex) {
			System.err.println("Error exportando inventario a MongoDB");
			ex.printStackTrace();
			return false;
		} finally {
			disconnect();
		}
	}

	@Override
	public void addProduct(Product product) {
		connect();
		try {
			if (product.getId() <= 0) {
				product.setId(nextProductId());
			}
			inventoryCollection.insertOne(toInventoryDocument(product));
		} catch (Exception ex) {
			System.err.println("Error insertando producto en MongoDB");
			ex.printStackTrace();
		} finally {
			disconnect();
		}
	}

	@Override
	public void updateProduct(Product product) {
		connect();
		try {
			inventoryCollection.replaceOne(
					eq("id", product.getId()),
					toInventoryDocument(product),
					new ReplaceOptions().upsert(false));
		} catch (Exception ex) {
			System.err.println("Error actualizando producto en MongoDB");
			ex.printStackTrace();
		} finally {
			disconnect();
		}
	}

	@Override
	public void deleteProduct(int productId) {
		connect();
		try {
			inventoryCollection.deleteOne(eq("id", productId));
		} catch (Exception ex) {
			System.err.println("Error eliminando producto en MongoDB");
			ex.printStackTrace();
		} finally {
			disconnect();
		}
	}

	private int nextProductId() {
		Document lastProduct = inventoryCollection.find().sort(descending("id")).first();
		return lastProduct == null ? 1 : readInt(lastProduct, "id") + 1;
	}

	private void initializeClient() {
		mongoClient = MongoSupport.createClient();
		database = mongoClient.getDatabase(MongoSupport.getDatabaseName());
	}

	private Product toProduct(Document document) {
		Document wholesalePrice = document.get("wholesalePrice", Document.class);
		double price = 0.0;
		if (wholesalePrice != null) {
			Object value = wholesalePrice.get("value");
			if (value instanceof Number number) {
				price = number.doubleValue();
			}
		}
		return new Product(
				readInt(document, "id"),
				document.getString("name"),
				price,
				document.getBoolean("available", Boolean.TRUE),
				readInt(document, "stock"));
	}

	private Document toInventoryDocument(Product product) {
		return new Document("id", product.getId())
				.append("name", product.getName())
				.append("wholesalePrice", MongoSupport.priceDocument(product.getPrice()))
				.append("available", product.isAvailable())
				.append("stock", product.getStock());
	}

	private Document toHistoricalDocument(Product product) {
		return toInventoryDocument(product).append("created_at", java.util.Date.from(Instant.now()));
	}

	private int readInt(Document document, String key) {
		Object value = document.get(key);
		if (value instanceof Number number) {
			return number.intValue();
		}
		return 0;
	}
}
