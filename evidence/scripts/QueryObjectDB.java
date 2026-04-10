import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import model.Employee;

public class QueryObjectDB {

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("Uso: QueryObjectDB <usersDbPath> <outputFile>");
			System.exit(1);
		}

		Path dbPath = Path.of(args[0]).toAbsolutePath().normalize();
		Path outputFile = Path.of(args[1]);
		if (outputFile.getParent() != null) {
			Files.createDirectories(outputFile.getParent());
		}

		String objectDbUrl = "objectdb:" + dbPath.toString().replace('\\', '/');

		EntityManagerFactory emf = Persistence.createEntityManagerFactory(objectDbUrl);
		EntityManager em = emf.createEntityManager();
		try {
			List<Employee> users = em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();
			users.sort(Comparator.comparingInt(Employee::getEmployeeId));

			List<String> entries = new ArrayList<>();
			for (Employee user : users) {
				entries.add("{\n"
						+ "  \"employeeId\": " + user.getEmployeeId() + ",\n"
						+ "  \"name\": \"" + escapeJson(user.getName()) + "\",\n"
						+ "  \"password\": \"" + escapeJson(user.getPassword()) + "\"\n"
						+ "}");
			}

			if (entries.isEmpty()) {
				Files.writeString(outputFile, "[]\n", StandardCharsets.UTF_8);
			} else {
				Files.writeString(outputFile, String.join(System.lineSeparator() + System.lineSeparator(), entries) + "\n",
						StandardCharsets.UTF_8);
			}
		} finally {
			em.close();
			emf.close();
		}
	}

	private static String escapeJson(String value) {
		if (value == null) {
			return "";
		}
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
