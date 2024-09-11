import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Platformer extends JFrame {
	@Serial
	private static final long serialVersionUID = 5736902251450559962L;

	public Platformer() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("HelloWorld");
		this.setBounds(0, 0, 320, 240);
		this.setVisible(true);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g; // cast to Graphics2D
		Line2D.Double line = new Line2D.Double(20.0, 50.0, 50.0, 200.0);
		g2d.draw(line);
		Rectangle2D.Double rect = new Rectangle2D.Double(100.0, 50.0, 60.0, 80.0);
		g2d.draw(rect); // also try g2d.fill(rect);
		g2d.fill(rect);
		Ellipse2D.Double circle = new Ellipse2D.Double(200.0, 100.0, 80.0, 80.0);
		g2d.draw(circle); // also try g2d.fill(circle);
		g2d.fill(circle);
		try {
			BufferedImage image = ImageIO.read(new File("C:\\Users\\Volodymyr\\Downloads\\Step0\\Step0\\testImg.bmp"));
			ImageObserver observer = new ImageObserver() {
				@Override
				public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
					return true;
				}
			};
			g2d.drawImage(image, 0, 0, 320, 240, observer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
