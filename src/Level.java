import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

public class Level {
    BufferedImage water = ImageIO.read(new File("C:\\Users\\Volodymyr\\Downloads\\Step0\\Step0\\assets\\Tiles\\liquidWaterTop_mid.png"));
    BufferedImage grass = ImageIO.read(new File("C:\\Users\\Volodymyr\\Downloads\\Step0\\Step0\\assets\\Tiles\\grassMid.png"));

    public BufferedImage getLevel() {
        return level;
    }

    BufferedImage level;

    public Level(File file) throws IOException {
        try {
            BufferedImage levelImage = ImageIO.read(file);
            BufferedImage generatedImage = new BufferedImage(70*levelImage.getWidth(), 70*levelImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g2d = generatedImage.createGraphics();
            for (int x = 0; x < levelImage.getWidth(); x++){
                for (int y = 0; y < levelImage.getHeight(); y++){
                    Color color = new Color(levelImage.getRGB(x, y));
                    if (color.equals(Color.BLUE)){
                        g2d.drawImage(water, x*70, y*70, water.getWidth(), water.getHeight(), null);
                    }
                    else if (color.equals(Color.BLACK)){
                        g2d.drawImage(grass, x*70, y*70, grass.getWidth(), grass.getHeight(), null);
                    }
                }
            }
            level = generatedImage;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
