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
    boolean hasCollided = false;
    boolean ownBullet;

    BufferedImage image = ImageIO.read(new File("assets/ourAssets/blueBall.png"));

    int width = 70;
    int height = 70;

    public Bullet(int startX, int startY, int damage) throws IOException {
        this.startX = startX;
        this.startY = startY;
        this.x = startX;
        this.y = startY;
        this.damage = damage;
        boundingBox = new BoundingBox(x, y, x + width, y + height);
    }

    public void update() {
        if (movingLeft) {
            x -= 4;
        } else {
            x += 4;
        }

        updateBoundingBox();
    }

    public boolean delete() {
        return Math.abs(x - startX) > range;
    }

    public void updateBoundingBox() {
        if (movingLeft) {

            boundingBox.min.x = x;
            boundingBox.max.x = x + width;
        } else {

            boundingBox.min.x = x;
            boundingBox.max.x = x + width;
        }

        boundingBox.min.y = y;
        boundingBox.max.y = y + height;
    }
    public boolean shouldBeRemoved() {
        return Math.abs(x - startX) > range || x < 0;
    }

}

