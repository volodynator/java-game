import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Bullet {
    int startX;
    int startY;
    int x;
    int y;
    int range = 500;

    BufferedImage image = ImageIO.read(new File(".\\assets\\Items\\fireball.png"));

    public Bullet(int startX, int startY) throws IOException {
        this.startX = startX;
        this.startY = startY;
        this.x = startX;
        this.y = startY;
    }
    public void update(){
        x+=10;
    }
    public boolean delete(){
        return x - startX>range;
    }
}
