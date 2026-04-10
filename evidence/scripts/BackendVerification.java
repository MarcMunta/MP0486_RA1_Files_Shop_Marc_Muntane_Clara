import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;

import dao.DaoImplMongoDB;
import dao.DaoImplObjectDB;
import model.Employee;
import model.Product;

public class BackendVerification {

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Uso: BackendVerification <outputDir>");
			System.exit(1);
		}

		Path outputDir = Path.of(args[0]);
		Files.createDirectories(outputDir);

		String productName = "evidenceobj" + System.currentTimeMillis() % 10000;
		Path reportPath = outputDir.resolve("backend_verification.txt");
		Path productNamePath = outputDir.resolve("product_name.txt");
		Path inventoryPath = outputDir.resolve("inventory_backend.json");

		StringBuilder report = new StringBuilder();
		report.append("BACKEND VERIFICATION").append(System.lineSeparator());
		report.append("timestamp=").append(LocalDateTime.now()).append(System.lineSeparator());

		DaoImplObjectDB objectDbDao = new DaoImplObjectDB();
		objectDbDao.connect();
		try {
			Employee ok = objectDbDao.getEmployee(123, "test");
			Employee ko = objectDbDao.getEmployee(666, "nose");
			report.append("LOGIN_OK_CAN=").append(ok != null).append(System.lineSeparator());
			report.append("LOGIN_BAD_CANNOT=").append(ko == null).append(System.lineSeparator());
		} finally {
			objectDbDao.disconnect();
		}

		DaoImplMongoDB mongoDao = new DaoImplMongoDB();

		ArrayList<Product> initial = mongoDao.getInventory();
		report.append("INVENTORY_INITIAL_SIZE=").append(initial.size()).append(System.lineSeparator());

		Product added = new Product(productName, 4.5, true, 7);
		mongoDao.addProduct(added);

		ArrayList<Product> afterAdd = mongoDao.getInventory();
		Product persisted = findByName(afterAdd, productName);
		boolean addCan = persisted != null && persisted.getStock() == 7;
		report.append("ADD_PRODUCT_CAN=").append(addCan).append(System.lineSeparator());

		if (persisted != null) {
			persisted.setStock(persisted.getStock() + 5);
			mongoDao.updateProduct(persisted);
		}

		ArrayList<Product> afterStock = mongoDao.getInventory();
		Product withStock = findByName(afterStock, productName);
		boolean stockCan = withStock != null && withStock.getStock() == 12;
		report.append("ADD_STOCK_CAN=").append(stockCan).append(System.lineSeparator());

		int sizeBeforeDeleteMissing = afterStock.size();
		mongoDao.deleteProduct(999999);
		ArrayList<Product> afterDeleteMissing = mongoDao.getInventory();
		boolean deleteMissingCannot = afterDeleteMissing.size() == sizeBeforeDeleteMissing;
		report.append("DELETE_MISSING_CANNOT=").append(deleteMissingCannot).append(System.lineSeparator());

		if (withStock != null) {
			mongoDao.deleteProduct(withStock.getId());
		}

		ArrayList<Product> afterDelete = mongoDao.getInventory();
		boolean deleteCan = findByName(afterDelete, productName) == null;
		report.append("DELETE_PRODUCT_CAN=").append(deleteCan).append(System.lineSeparator());

		report.append("PRODUCT_NAME_USED=").append(productName).append(System.lineSeparator());

		StringBuilder inventoryDump = new StringBuilder();
		for (Product product : afterDelete) {
			inventoryDump.append("{\n");
			inventoryDump.append("  \"id\": ").append(product.getId()).append(",\n");
			inventoryDump.append("  \"name\": \"").append(escape(product.getName())).append("\",\n");
			inventoryDump.append("  \"stock\": ").append(product.getStock()).append("\n");
			inventoryDump.append("}\n\n");
		}

		Files.writeString(reportPath, report.toString(), StandardCharsets.UTF_8);
		Files.writeString(productNamePath, productName + System.lineSeparator(), StandardCharsets.UTF_8);
		Files.writeString(inventoryPath, inventoryDump.toString(), StandardCharsets.UTF_8);
	}

	private static Product findByName(ArrayList<Product> products, String name) {
		for (Product product : products) {
			if (product != null && product.getName().equalsIgnoreCase(name)) {
				return product;
			}
		}
		return null;
	}

	private static String escape(String value) {
		if (value == null) {
			return "";
		}
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
