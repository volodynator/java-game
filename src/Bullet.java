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
    boolean movingLeft;
    BoundingBox boundingBox;
    int damage;

    BufferedImage image = ImageIO.read(new File("assets/ourAssets/blueBall.png"));

    public Bullet(int startX, int startY, int damage) throws IOException {
        this.startX = startX;
        this.startY = startY;
        this.x = startX;
        this.y = startY;
        this.damage = damage;
        boundingBox = new BoundingBox(x, y, x, y);
    }
    public void update(){
        if (movingLeft){
            x-=4;
        }
        else {
            x+=4;
        }
        updateBoundingBox();
    }
    public boolean delete(){
        return x - startX>range;
    }
    public void updateBoundingBox(){
        boundingBox.min.x = x;
        boundingBox.min.y = y;

        boundingBox.max.x = x;
        boundingBox.max.y = y;
    }
}
