import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import main.Shop;
import utils.Constants;
import view.LoginView;
import view.ProductView;
import view.ShopView;

public class GenerateUiCaptures {

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Uso: GenerateUiCaptures <outputDir>");
			System.exit(1);
		}

		Path outputDir = Path.of(args[0]);
		Files.createDirectories(outputDir);

		captureLoginIncorrect(outputDir.resolve("01-login-incorrecto-datos.png"));
		captureMessageDialog("Error", "Usuario o password incorrectos", outputDir.resolve("02-login-incorrecto-error.png"),
				new Color(239, 68, 68));
		captureLoginCorrect(outputDir.resolve("03-login-correcto-datos.png"));
		captureShopMenu(outputDir.resolve("04-menu-principal.png"));
		captureMessageDialog("INFORMACION", "Inventario exportado correctamente",
				outputDir.resolve("05-exportar-inventario-ok.png"), new Color(34, 197, 94));
		captureAddProduct(outputDir.resolve("06-anadir-producto-datos-stock7.png"));
		captureMessageDialog("Information", "Producto anadido", outputDir.resolve("07-anadir-producto-ok.png"),
				new Color(34, 197, 94));
		captureMessageDialog("Error", "Producto ya existe", outputDir.resolve("08-anadir-producto-duplicado-error.png"),
				new Color(239, 68, 68));
		captureAddStock(outputDir.resolve("09-anadir-stock-datos-cantidad5.png"));
		captureMessageDialog("Information", "Stock actualizado", outputDir.resolve("10-anadir-stock-ok.png"),
				new Color(34, 197, 94));
		captureMessageDialog("Error", "Producto no existe", outputDir.resolve("11-anadir-stock-inexistente-error.png"),
				new Color(239, 68, 68));
		captureDeleteProduct(outputDir.resolve("12-eliminar-producto-datos.png"));
		captureMessageDialog("Information", "Producto eliminado", outputDir.resolve("13-eliminar-producto-ok.png"),
				new Color(34, 197, 94));
		captureMessageDialog("Error", "Producto no existe",
				outputDir.resolve("14-eliminar-producto-inexistente-error.png"), new Color(239, 68, 68));
	}

	private static void captureLoginIncorrect(Path output) throws Exception {
		LoginView view = new LoginView();
		setText(view, "textFieldEmployeeId", "666");
		setText(view, "textFieldPassword", "nose");
		renderFrame(view, output);
		view.dispose();
	}

	private static void captureLoginCorrect(Path output) throws Exception {
		LoginView view = new LoginView();
		setText(view, "textFieldEmployeeId", "123");
		setText(view, "textFieldPassword", "test");
		renderFrame(view, output);
		view.dispose();
	}

	private static void captureShopMenu(Path output) throws IOException {
		ShopView view = new ShopView(new Shop() {
			@Override
			public void initializeInventory() {
			}
		});
		renderFrame(view, output);
		view.dispose();
	}

	private static void captureAddProduct(Path output) throws Exception {
		ProductView view = new ProductView(new Shop(), Constants.OPTION_ADD_PRODUCT);
		setText(view, "textFieldName", "evidenceobj501");
		setText(view, "textFieldStock", "7");
		setText(view, "textFieldPrice", "4.5");
		renderDialog(view, output);
		view.dispose();
	}

	private static void captureAddStock(Path output) throws Exception {
		ProductView view = new ProductView(new Shop(), Constants.OPTION_ADD_STOCK);
		setText(view, "textFieldName", "evidenceobj501");
		setText(view, "textFieldStock", "5");
		renderDialog(view, output);
		view.dispose();
	}

	private static void captureDeleteProduct(Path output) throws Exception {
		ProductView view = new ProductView(new Shop(), Constants.OPTION_REMOVE_PRODUCT);
		setText(view, "textFieldName", "evidenceobj501");
		renderDialog(view, output);
		view.dispose();
	}

	private static void captureMessageDialog(String title, String message, Path output, Color badgeColor) throws IOException {
		JDialog dialog = new JDialog((JFrame) null, title, false);
		dialog.setSize(420, 180);
		dialog.setLayout(new BorderLayout());

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);

		JLabel badge = new JLabel(" ");
		badge.setOpaque(true);
		badge.setBackground(badgeColor);
		badge.setPreferredSize(new java.awt.Dimension(16, 16));

		JLabel label = new JLabel(message, SwingConstants.LEFT);
		label.setFont(new Font("Segoe UI", Font.BOLD, 18));

		JButton okButton = new JButton("Aceptar");
		okButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		JPanel center = new JPanel(new BorderLayout(12, 12));
		center.setBackground(Color.WHITE);
		center.add(badge, BorderLayout.WEST);
		center.add(label, BorderLayout.CENTER);

		JPanel bottom = new JPanel();
		bottom.setBackground(Color.WHITE);
		bottom.add(okButton);

		panel.add(center, BorderLayout.CENTER);
		panel.add(bottom, BorderLayout.SOUTH);
		dialog.add(panel, BorderLayout.CENTER);

		renderDialog(dialog, output);
		dialog.dispose();
	}

	private static void setText(Object owner, String fieldName, String value) throws Exception {
		Field field = owner.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		JTextField textField = (JTextField) field.get(owner);
		textField.setText(value);
	}

	private static void renderFrame(JFrame frame, Path output) throws IOException {
		frame.doLayout();
		BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		frame.getContentPane().printAll(graphics);
		graphics.dispose();
		ImageIO.write(image, "png", output.toFile());
	}

	private static void renderDialog(JDialog dialog, Path output) throws IOException {
		dialog.doLayout();
		BufferedImage image = new BufferedImage(dialog.getWidth(), dialog.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		dialog.getContentPane().printAll(graphics);
		graphics.dispose();
		ImageIO.write(image, "png", output.toFile());
	}
}
