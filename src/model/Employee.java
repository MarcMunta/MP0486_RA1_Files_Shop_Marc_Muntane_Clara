package model;

import dao.Dao;
import dao.DaoImplJDBC;
import main.Logable;

/**
 * Clase que representa un empleado de la tienda.
 * Extiende Person e implementa Logable para autenticación.
 * Utiliza JDBC para validar credenciales contra la base de datos.
 * 
 * @author Marc Muntané Clarà
 * @version 2.0
 */
public class Employee extends Person implements Logable {
	
	/** Identificador único del empleado */
	private int employeeId;
	
	/** Contraseña del empleado */
	private String password;
	
	/** DAO JDBC para autenticación contra base de datos */
	private Dao dao = new DaoImplJDBC();
	
	/**
	 * Constructor con nombre.
	 * 
	 * @param name nombre del empleado
	 */
	public Employee(String name) {
		super(name);
	}
	
	/**
	 * Constructor completo con credenciales.
	 * 
	 * @param employeeId identificador del empleado
	 * @param name nombre del empleado
	 * @param password contraseña del empleado
	 */
	public Employee(int employeeId, String name, String password) {
		super(name);
		this.employeeId = employeeId;
		this.password = password;
	}
	
	/**
	 * Constructor por defecto.
	 */
	public Employee() {
		super();
	}
	
	/**
	 * @return the employeeId
	 */
	public int getEmployeeId() {
		return employeeId;
	}

	/**
	 * @param employeeId the employeeId to set
	 */
	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param user from application, password from application
	 * @return true if credentials are correct or false if not
	 */
	@Override
	public boolean login(int user, String password) {
//		if (USER == user && PASSWORD.equals(password)) {
//			return true;
//		} 
		boolean success = false;
		
		// connect to data
		dao.connect();
		
		// get employee data
		if(dao.getEmployee(user, password) != null) {
			success =  true;
		}
		
		// disconnect data
		dao.disconnect();
		return success;
	}

}
