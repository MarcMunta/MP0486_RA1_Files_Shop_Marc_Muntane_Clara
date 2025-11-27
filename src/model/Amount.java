package model;

import java.text.DecimalFormat;

/**
 * Clase que representa una cantidad monetaria.
 * Gestiona valores numéricos con formato de moneda (euros).
 * 
 * @author Marc Muntané Clarà
 * @version 2.0
 */
public class Amount {
	
	/** Valor numérico de la cantidad */
	private double value;
	
	/** Símbolo de moneda (por defecto euros) */
	private String currency = "€";
	
	/** Formato decimal para mostrar cantidades (2 decimales) */
	private static final DecimalFormat df = new DecimalFormat("0.00");
	
	/**
	 * Constructor con valor inicial.
	 * 
	 * @param value valor monetario inicial
	 */
	public Amount(double value) {
		super();
		this.value = value;
	}

	/**
	 * Obtiene el valor numérico.
	 * @return valor de la cantidad
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Establece el valor numérico.
	 * @param value nuevo valor
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * Representación en cadena con formato monetario.
	 * @return valor formateado con símbolo de moneda
	 */
	@Override
	public String toString() {
		return df.format(value) + currency;
	}
}
