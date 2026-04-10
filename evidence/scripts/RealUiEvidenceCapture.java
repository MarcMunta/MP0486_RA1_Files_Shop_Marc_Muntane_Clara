import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import utils.Constants;
import view.LoginView;
import view.ProductView;
import view.ShopView;

public class RealUiEvidenceCapture {

	private final Robot robot;
	private final Path screenshotsDir;

	public RealUiEvidenceCapture(Path screenshotsDir) throws AWTException {
		this.robot = new Robot();
		this.robot.setAutoDelay(120);
		this.screenshotsDir = screenshotsDir;
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Uso: RealUiEvidenceCapture <screenshotsDir>");
			System.exit(1);
		}

		Path screenshots = Path.of(args[0]);
		Files.createDirectories(screenshots);

		RealUiEvidenceCapture capture = new RealUiEvidenceCapture(screenshots);
		capture.run();
	}

	private void run() throws Exception {
		String productName = "evidenceobj" + (System.currentTimeMillis() % 10000);
		Files.writeString(Path.of("evidence/dumps/product_name.txt"), productName + System.lineSeparator());

		LoginView login = onEdt(() -> {
			LoginView view = new LoginView();
			maximizeFrame(view);
			view.setVisible(true);
			view.toFront();
			return view;
		});

		waitMs(900);
		setTextField(login, "textFieldEmployeeId", "666");
		setTextField(login, "textFieldPassword", "nose");
		waitMs(500);
		captureScreen("01-login-incorrecto-datos.png");

		clickButton(login, "btnLogin");
		waitForDialog("Error", "ERROR");
		waitMs(400);
		captureScreen("02-login-incorrecto-error.png");
		closeMessageDialogs();

		setTextField(login, "textFieldEmployeeId", "123");
		setTextField(login, "textFieldPassword", "test");
		waitMs(500);
		captureScreen("03-login-correcto-datos.png");

		clickButton(login, "btnLogin");
		ShopView shopView = waitForFrame("Menu principal", 15000);
		waitMs(500);
		captureScreen("04-menu-principal.png");

		SwingUtilities.invokeLater(() -> {
			shopView.toFront();
			shopView.requestFocus();
			shopView.exportInventory();
		});
		waitForDialog("INFORMACION", "Information");
		waitMs(300);
		captureScreen("05-exportar-inventario-ok.png");
		closeMessageDialogs();

		ProductView addView = openProductView(shopView, Constants.OPTION_ADD_PRODUCT);
		setTextField(addView, "textFieldName", productName);
		setTextField(addView, "textFieldStock", "7");
		setTextField(addView, "textFieldPrice", "4.5");
		waitMs(500);
		captureScreen("06-anadir-producto-datos-stock7.png");
		clickButton(addView, "okButton");
		waitForDialog("Information", "INFORMACION");
		waitMs(300);
		captureScreen("07-anadir-producto-ok.png");
		closeMessageDialogs();

		ProductView dupView = openProductView(shopView, Constants.OPTION_ADD_PRODUCT);
		setTextField(dupView, "textFieldName", productName);
		setTextField(dupView, "textFieldStock", "7");
		setTextField(dupView, "textFieldPrice", "4.5");
		clickButton(dupView, "okButton");
		waitForDialog("Error", "ERROR");
		waitMs(300);
		captureScreen("08-anadir-producto-duplicado-error.png");
		closeMessageDialogs();
		disposeWindow(dupView);

		ProductView stockView = openProductView(shopView, Constants.OPTION_ADD_STOCK);
		setTextField(stockView, "textFieldName", productName);
		setTextField(stockView, "textFieldStock", "5");
		waitMs(500);
		captureScreen("09-anadir-stock-datos-cantidad5.png");
		clickButton(stockView, "okButton");
		waitForDialog("Information", "INFORMACION");
		waitMs(300);
		captureScreen("10-anadir-stock-ok.png");
		closeMessageDialogs();

		ProductView stockMissing = openProductView(shopView, Constants.OPTION_ADD_STOCK);
		setTextField(stockMissing, "textFieldName", "noproducto999");
		setTextField(stockMissing, "textFieldStock", "4");
		clickButton(stockMissing, "okButton");
		waitForDialog("Error", "ERROR");
		waitMs(300);
		captureScreen("11-anadir-stock-inexistente-error.png");
		closeMessageDialogs();
		disposeWindow(stockMissing);

		ProductView deleteView = openProductView(shopView, Constants.OPTION_REMOVE_PRODUCT);
		setTextField(deleteView, "textFieldName", productName);
		waitMs(500);
		captureScreen("12-eliminar-producto-datos.png");
		clickButton(deleteView, "okButton");
		waitForDialog("Information", "INFORMACION");
		waitMs(300);
		captureScreen("13-eliminar-producto-ok.png");
		closeMessageDialogs();

		ProductView deleteMissing = openProductView(shopView, Constants.OPTION_REMOVE_PRODUCT);
		setTextField(deleteMissing, "textFieldName", "noproducto999");
		clickButton(deleteMissing, "okButton");
		waitForDialog("Error", "ERROR");
		waitMs(300);
		captureScreen("14-eliminar-producto-inexistente-error.png");
		closeMessageDialogs();
		disposeWindow(deleteMissing);

		disposeWindow(shopView);
		disposeWindow(login);
	}

	private ProductView openProductView(ShopView shopView, int option) throws Exception {
		ProductView productView = onEdt(() -> {
			ProductView view = new ProductView(shopView.getShop(), option);
			view.setModal(false);
			maximizeDialog(view);
			view.setVisible(true);
			view.toFront();
			return view;
		});
		waitMs(600);
		return productView;
	}

	private void setTextField(Object owner, String fieldName, String value) throws Exception {
		onEdt(() -> {
			JTextField field = (JTextField) getField(owner, fieldName);
			field.setText(value);
			field.requestFocusInWindow();
			return null;
		});
	}

	private void clickButton(Object owner, String fieldName) throws Exception {
		onEdt(() -> {
			JButton button = (JButton) getField(owner, fieldName);
			button.requestFocusInWindow();
			SwingUtilities.invokeLater(button::doClick);
			return null;
		});
		waitMs(450);
	}

	private void captureScreen(String fileName) throws Exception {
		BufferedImage image = robot.createScreenCapture(
				new java.awt.Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		ImageIO.write(image, "png", screenshotsDir.resolve(fileName).toFile());
	}

	private void closeMessageDialogs() throws Exception {
		onEdt(() -> {
			for (Window window : Window.getWindows()) {
				if (window instanceof JDialog dialog && dialog.isShowing()) {
					String title = dialog.getTitle() == null ? "" : dialog.getTitle();
					if (title.equalsIgnoreCase("Error")
							|| title.equalsIgnoreCase("ERROR")
							|| title.equalsIgnoreCase("Information")
							|| title.equalsIgnoreCase("INFORMACION")) {
						dialog.dispose();
					}
				}
			}
			return null;
		});
		waitMs(250);
	}

	private ShopView waitForFrame(String titleContains, long timeoutMs) throws Exception {
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < timeoutMs) {
			ShopView found = onEdt(() -> Arrays.stream(JFrame.getFrames())
					.filter(frame -> frame.isShowing())
					.filter(frame -> frame instanceof ShopView)
					.map(frame -> (ShopView) frame)
					.filter(frame -> frame.getTitle() != null && frame.getTitle().contains(titleContains))
					.findFirst()
					.orElse(null));
			if (found != null) {
				onEdt(() -> {
					found.toFront();
					return null;
				});
				return found;
			}
			waitMs(150);
		}
		throw new IllegalStateException("No se encontro ShopView con titulo: " + titleContains);
	}

	private void waitForDialog(String... titleCandidates) throws Exception {
		long timeoutMs = 15000;
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < timeoutMs) {
			JDialog dialog = onEdt(() -> Arrays.stream(Window.getWindows())
					.filter(window -> window instanceof JDialog)
					.map(window -> (JDialog) window)
					.filter(JDialog::isShowing)
					.filter(d -> {
						String title = d.getTitle() == null ? "" : d.getTitle();
						for (String candidate : titleCandidates) {
							if (title.equalsIgnoreCase(candidate)) {
								return true;
							}
						}
						return false;
					})
					.findFirst()
					.orElse(null));
			if (dialog != null) {
				onEdt(() -> {
					dialog.toFront();
					return null;
				});
				return;
			}
			waitMs(120);
		}
		throw new IllegalStateException("No se encontro dialogo: " + String.join(",", titleCandidates));
	}

	private void disposeWindow(Window window) throws Exception {
		if (window == null) {
			return;
		}
		onEdt(() -> {
			window.dispose();
			return null;
		});
		waitMs(200);
	}

	@SuppressWarnings("unchecked")
	private <T> T onEdt(ThrowingSupplier<T> supplier) throws Exception {
		if (SwingUtilities.isEventDispatchThread()) {
			return supplier.get();
		}
		final Object[] holder = new Object[1];
		final Exception[] error = new Exception[1];
		SwingUtilities.invokeAndWait(() -> {
			try {
				holder[0] = supplier.get();
			} catch (Exception ex) {
				error[0] = ex;
			}
		});
		if (error[0] != null) {
			throw error[0];
		}
		return (T) holder[0];
	}

	private Object getField(Object owner, String fieldName) {
		Class<?> type = owner.getClass();
		while (type != null) {
			try {
				Field field = type.getDeclaredField(fieldName);
				field.setAccessible(true);
				return field.get(owner);
			} catch (NoSuchFieldException ex) {
				type = type.getSuperclass();
			} catch (IllegalAccessException ex) {
				throw new IllegalStateException(ex);
			}
		}
		throw new IllegalArgumentException("No existe el campo: " + fieldName);
	}

	private void waitMs(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	private void maximizeFrame(JFrame frame) {
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	private void maximizeDialog(JDialog dialog) {
		Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		dialog.setBounds(bounds);
	}

	@FunctionalInterface
	private interface ThrowingSupplier<T> {
		T get() throws Exception;
	}
}
