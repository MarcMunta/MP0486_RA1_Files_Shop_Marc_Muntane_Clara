package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import main.Shop;
import model.Product;
import support.MongoIntegrationSupport;
import utils.MongoSupport;

class DaoImplMongoDBIntegrationTest extends MongoIntegrationSupport {

	@Test
	void getInventoryLoadsSeededProductsFromInventoryCollection() {
		Dao dao = new DaoImplMongoDB();

		var inventory = dao.getInventory();

		assertEquals(5, inventory.size());
		assertEquals("Manzana", inventory.getFirst().getName());
		assertEquals("Leche", inventory.getLast().getName());
		assertEquals(100, inventory.getLast().getStock());
	}

	@Test
	void exportInventoryWritesSnapshotIntoHistoricalInventoryCollection() {
		Shop shop = new Shop();
		shop.initializeInventory();

		assertTrue(shop.exportInventoryToDatabase());

		try (var client = com.mongodb.client.MongoClients.create(connectionString)) {
			var history = client.getDatabase(databaseName).getCollection(MongoSupport.HISTORY_COLLECTION);
			assertEquals(shop.getInventory().size(), history.countDocuments());

			Document exportedProduct = history.find(new Document("id", 1)).first();
			assertNotNull(exportedProduct);
			assertNotNull(exportedProduct.getDate("created_at"));
			assertNotNull(exportedProduct.get("wholesalePrice", Document.class));
		}
	}

	@Test
	void addUpdateAndDeleteProductKeepInventoryCollectionSynchronized() {
		Shop shop = new Shop();
		shop.initializeInventory();

		Product product = new Product("Yogurth", 16.5, true, 10);
		shop.addProduct(product);

		Document inserted = findFirst(MongoSupport.INVENTORY_COLLECTION, "name", "Yogurth");
		assertNotNull(inserted);
		assertEquals(6, ((Number) inserted.get("id")).intValue());
		assertEquals(10, ((Number) inserted.get("stock")).intValue());
		assertEquals(16.5, ((Number) inserted.get("wholesalePrice", Document.class).get("value")).doubleValue());

		product.setStock(18);
		shop.updateProduct(product);

		Document updated = findFirst(MongoSupport.INVENTORY_COLLECTION, "name", "Yogurth");
		assertNotNull(updated);
		assertEquals(18, ((Number) updated.get("stock")).intValue());

		shop.deleteProduct(product.getId());

		assertNull(findFirst(MongoSupport.INVENTORY_COLLECTION, "name", "Yogurth"));
	}
}
