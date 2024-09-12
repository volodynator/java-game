import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject {



    int prevX;
    int prevY;
    int speedX;
    int speedY;

    List<BufferedImage> images = new ArrayList<>();
    int curr = 0;
    public static final String path = ".\\assets\\Player\\";

    public Player() throws IOException {
        this.x = 10;
        this.y = 10;
        for(int i = 1; i<10; i++){
            images.add(ImageIO.read(new File( path +"p1_walk\\PNG\\p1_walk0"+i+".png")));
        }
        images.add(ImageIO.read(new File(path +"p1_walk\\PNG\\p1_walk10.png")));
        images.add(ImageIO.read(new File(path +"p1_walk\\PNG\\p1_walk11.png")));
        this.width = images.get(0).getWidth();
        this.height = images.get(0).getHeight();
        this.boundingBox = new BoundingBox(new Vec2(x,y), new Vec2(x + width,y - height));
    }
    public BufferedImage getImage(){
        return images.get(curr);
    }
    public void updateAnimation() {
        curr = (curr + 1) % images.size();
    }
    public void move(int dx, int dy) {
        x += dx;
        y+=dy;
        updateAnimation();
    }


}
