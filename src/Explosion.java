import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

public class Explosion {
    private BufferedImage[] frames;
    Level level;
    private int currentFrame = 0;
    private int x, y;
    private boolean finished = false;
    private int frameDelay = 10;
    private int frameCount = 0;

    public Explosion(int x, int y, Level level) {
        this.level = level;
        this.x = x - (int) level.offsetX;
        this.y = y;
        frames = new BufferedImage[4];

        try {
            frames[0] = ImageIO.read(new File("assets/ourAssets/explosion/3.png"));
            frames[1] = ImageIO.read(new File("assets/ourAssets/explosion/4.png"));
            frames[2] = ImageIO.read(new File("assets/ourAssets/explosion/5.png"));
            frames[3] = ImageIO.read(new File("assets/ourAssets/explosion/6.png"));
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void update() {
        frameCount++;
        if (frameCount >= frameDelay) {
            currentFrame++;
            frameCount = 0;

            if (currentFrame >= frames.length) {
                finished = true;
            }
        }
    }

    public void draw(Graphics g) {
        if (!finished) {
            g.drawImage(frames[currentFrame], x, y, null);
        }
    }

    public boolean isFinished() {
        return finished;
    }
}

