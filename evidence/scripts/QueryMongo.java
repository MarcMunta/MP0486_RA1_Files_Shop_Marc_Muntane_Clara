import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class QueryMongo {

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("Uso: QueryMongo <connectionString> <database> <outputDir>");
			System.exit(1);
		}

		String connectionString = args[0];
		String databaseName = args[1];
		Path outputDir = Path.of(args[2]);
		Files.createDirectories(outputDir);

		JsonWriterSettings settings = JsonWriterSettings.builder()
				.indent(true)
				.outputMode(JsonMode.RELAXED)
				.build();

		try (MongoClient client = MongoClients.create(connectionString)) {
			MongoDatabase database = client.getDatabase(databaseName);
			dumpCollection(database.getCollection("inventory"), outputDir.resolve("inventory.json"), settings);
			dumpCollection(database.getCollection("historical_inventory"), outputDir.resolve("historical_inventory.json"),
					settings);
			dumpCollection(database.getCollection("users"), outputDir.resolve("users.json"), settings);
		}
	}

	private static void dumpCollection(MongoCollection<Document> collection, Path output, JsonWriterSettings settings)
			throws Exception {
		List<String> lines = new ArrayList<>();
		for (Document document : collection.find()) {
			lines.add(document.toJson(settings));
		}
		if (lines.isEmpty()) {
			lines.add("[]");
		}
		Files.writeString(output, String.join(System.lineSeparator() + System.lineSeparator(), lines),
				StandardCharsets.UTF_8);
	}
}
