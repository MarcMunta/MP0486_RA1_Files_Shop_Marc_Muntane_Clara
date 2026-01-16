package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;

import model.Employee;
import model.Product;

public class DaoImplFile implements Dao {
	@Override
	public void connect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Product> getInventory() {

		// locate file, path and name
		File f = new File(System.getProperty("user.dir") + File.separator + "files/inputInventory.txt");
		ArrayList<Product> inventory = new ArrayList<>();
		try (FileReader fr = new FileReader(f); BufferedReader br = new BufferedReader(fr)) {
			String line = br.readLine();

			// process and read next line until end of file
			while (line != null) {
				// split in sections
				String[] sections = line.split(";");

				String name = "";
				double price = 0.0;
				int stock = 0;

				// read each sections
				for (int i = 0; i < sections.length; i++) {
					// split data in key(0) and value(1)
					String[] data = sections[i].split(":");

					switch (i) {
					case 0:
						// format product name
						name = data[1];
						break;

					case 1:
						// format price
						price = Double.parseDouble(data[1]);
						break;

					case 2:
						// format stock
						stock = Integer.parseInt(data[1]);
						break;

					default:
						break;
					}
				}
				// add product to inventory
				inventory.add(new Product(name, price, true, stock));

				// read next line
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return inventory;
	}

	@Override
	public boolean writeInventory(ArrayList<Product> products) {
		// define file name based on date
		LocalDate myObj = LocalDate.now();
		String fileName = "inventory_" + myObj.toString() + ".txt";

		// locate file, path and name
		File f = new File(System.getProperty("user.dir") + File.separator + "files" + File.separator + fileName);

		try (FileWriter fw = new FileWriter(f, true); PrintWriter pw = new PrintWriter(fw)) {

			int counterInventory = 0;

			for (Product product : products) {

				counterInventory++;

				StringBuilder line = new StringBuilder(
						counterInventory + ";Product:" + product.getName() + ";Stock:" + product.getStock() + ";");
				pw.write(line.toString());
				fw.write("\n");

			}
			StringBuilder lastLine = new StringBuilder("Total number of products:" + counterInventory);
			pw.write(lastLine.toString());
			fw.write("\n");

			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void addProduct(Product product) {
		throw new UnsupportedOperationException("addProduct no implementado en DaoImplFile");
	}

	@Override
	public void updateProduct(Product product) {
		throw new UnsupportedOperationException("updateProduct no implementado en DaoImplFile");
	}

	@Override
	public void deleteProduct(int productId) {
		throw new UnsupportedOperationException("deleteProduct no implementado en DaoImplFile");
	}
}