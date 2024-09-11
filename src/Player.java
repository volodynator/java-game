import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Player {
    int x = 10;
    int y = 10;
    int prevX;
    int prevY;
    int speedX;
    int speedY;
    List<BufferedImage> images = new ArrayList<>();
    int curr = 0;

    public Player() throws IOException {
        for(int i = 1; i<10; i++){
            images.add(ImageIO.read(new File("C:\\Users\\Volodymyr\\Downloads\\Step0\\Step0\\assets\\Player\\p1_walk\\PNG\\p1_walk0"+i+".png")));
        }
        images.add(ImageIO.read(new File("C:\\Users\\Volodymyr\\Downloads\\Step0\\Step0\\assets\\Player\\p1_walk\\PNG\\p1_walk10.png")));
        images.add(ImageIO.read(new File("C:\\Users\\Volodymyr\\Downloads\\Step0\\Step0\\assets\\Player\\p1_walk\\PNG\\p1_walk11.png")));
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
