package util;

import javax.swing.UIManager;

public final class LookAndFeelUtil {

	private LookAndFeelUtil() {
	}

	public static void applySystemLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			// No hacer ruido: si falla, se mantiene el L&F por defecto.
		}
	}
}
