import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.imageio.ImageIO;

public class GenerateTextScreenshot {

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("Uso: GenerateTextScreenshot <inputTextFile> <outputPng>");
			System.exit(1);
		}

		Path input = Path.of(args[0]);
		Path output = Path.of(args[1]);
		List<String> lines = Files.readAllLines(input, StandardCharsets.UTF_8);

		int width = 1500;
		int padding = 24;
		int lineHeight = 24;
		int height = Math.max(220, padding * 2 + Math.max(1, lines.size()) * lineHeight);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(new Color(20, 20, 20));
		g.fillRect(0, 0, width, height);

		g.setColor(new Color(80, 200, 120));
		g.setFont(new Font("Consolas", Font.PLAIN, 18));

		int y = padding + 18;
		for (String line : lines) {
			String safe = line == null ? "" : line;
			if (safe.length() > 150) {
				safe = safe.substring(0, 150);
			}
			g.drawString(safe, padding, y);
			y += lineHeight;
		}

		g.dispose();
		if (output.getParent() != null) {
			Files.createDirectories(output.getParent());
		}
		ImageIO.write(image, "png", output.toFile());
	}
}
