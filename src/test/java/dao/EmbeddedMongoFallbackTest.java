package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.ServerSocket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import utils.EmbeddedMongoServer;

class EmbeddedMongoFallbackTest {

	@AfterEach
	void cleanup() {
		System.clearProperty("shop.dao");
		System.clearProperty("shop.mongo.connectionString");
		System.clearProperty("shop.mongo.database");
		System.clearProperty("shop.mongo.autoSeed");
		System.clearProperty("shop.mongo.embedded");
		EmbeddedMongoServer.stop();
	}

	@Test
	void getInventoryStartsEmbeddedMongoWhenNoLocalServerExists() throws IOException {
		int port = randomPort();

		System.setProperty("shop.dao", "mongo");
		System.setProperty("shop.mongo.connectionString", "mongodb://localhost:" + port);
		System.setProperty("shop.mongo.database", "shop_fallback_" + System.nanoTime());
		System.setProperty("shop.mongo.autoSeed", "true");
		System.setProperty("shop.mongo.embedded", "true");

		var inventory = new DaoImplMongoDB().getInventory();

		assertEquals(5, inventory.size());
	}

	private int randomPort() throws IOException {
		try (ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		}
	}
}
