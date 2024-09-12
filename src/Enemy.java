import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Enemy {
    boolean jump = false, walkingLeft = false, walkingRight = false;
    boolean collidesTop = false, collidesDown = false, collidesLeft = false, collidesRight = false, collides = false;

    boolean facingLeft = false;

    Vec2 pos;
    Vec2 posLastFrame;
    Vec2 gravity;
    Vec2 maxSpeed;

    List<Item> itemsList = new ArrayList<>();
    Shield shield = null;
    float tmp;
    int curr = 0;

    int hp = 20;
    int mana = 100;
    List<Bullet> bullets = new ArrayList<>();

    public Vec2 lastValidPosition;

    float movementSpeed;

    BoundingBox boundingBox;
    int numberAnimationStates = 0;
    int displayedAnimationState = 0;
    int moveCounter = 0;
    int points = 0;

    float jumpPower = 35.f;

    // Tiles for movement animation
    protected ArrayList<BufferedImage> tilesWalk;

    Level l;

    Enemy(Level l, int x, int y) throws IOException {
        this.pos = new Vec2(x, y);
        this.posLastFrame = new Vec2(x, y);
        this.gravity = new Vec2(0, 0.35f);
        this.maxSpeed = new Vec2(5, 10);
        this.movementSpeed = 3.5f;

        this.l = l;
        tilesWalk = new ArrayList<BufferedImage>();
        try {

            // Tiles for movement animation
            BufferedImage imageWalk;
            BufferedImage imageHalf;
            BufferedImage imageEmpty;

            imageWalk = ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk01.png"));
            tilesWalk.add(imageWalk);
            imageHalf = ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk02.png"));
            tilesWalk.add(imageHalf);
            imageEmpty = ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk03.png"));
            tilesWalk.add(imageEmpty);
            imageWalk = ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk04.png"));
            tilesWalk.add(imageWalk);
            imageHalf = ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk05.png"));
            tilesWalk.add(imageHalf);
            imageEmpty = ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk06.png"));
            tilesWalk.add(imageEmpty);
            imageWalk = ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk07.png"));
            tilesWalk.add(imageWalk);
            imageHalf = ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk08.png"));
            tilesWalk.add(imageHalf);
            imageEmpty = ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk09.png"));
            tilesWalk.add(imageEmpty);
            imageWalk = ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk10.png"));
            tilesWalk.add(imageWalk);
            imageHalf = ImageIO.read(new File(Platformer.BasePath + "Player/p1_walk/PNG/p1_walk11.png"));
            tilesWalk.add(imageWalk);

        } catch (IOException e) {
            e.printStackTrace();
        }

        boundingBox = new BoundingBox(0, 0, tilesWalk.get(0).getWidth(), tilesWalk.get(0).getHeight());
        numberAnimationStates = tilesWalk.size();

    }

    void move(int deltaX) {
        if (deltaX < 0) {
            pos.x = pos.x - movementSpeed / 4;
        } else if (deltaX > 0) {
            pos.x = pos.x + movementSpeed / 4;
        }
    }

    public void kill() {
        if (hp > 0) {
            hp-=50;
            pos.x = lastValidPosition.x;
            pos.y = lastValidPosition.y;
            posLastFrame.x = pos.x;
            posLastFrame.y = pos.y;
        }

    }

    public void update() throws IOException, NotEnoughManaExeption {

        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update();

            if (bullet.delete()) {
                bulletIterator.remove();
            }
        }

        Random random = new Random();
        double r = random.nextDouble(40);
        int distance = (int) (pos.x - l.player.pos.x);
        if (distance<500 && r<0.05){
            EnemyGun gun =new EnemyGun(this, l, 10);
            gun.shoot();
        }
        if (r<0.1){
            walkingLeft = true;
        }
        if (r>0.1 && r<0.2){
            walkingRight = true;
        }
        if (r>0.2 && r<0.23){
            jump=true;
        }

        // Check if walking and call move()
        if (walkingLeft){
            move(-1);
            facingLeft = true;
        }
        if (walkingRight){
            move(1);
            facingLeft = false;
        }

        if(jump && collidesDown){
            pos.y -= jumpPower;
        }

        // Save old position
        Vec2 pos_lastFrame_temp = pos;

        // Add gravity and move according to the actual speed
        pos = pos.add(pos.sub(posLastFrame));

        // Get saved old Position back
        posLastFrame = pos_lastFrame_temp;

        //apply gravity
        pos = pos.add(gravity);

        // Calculate difference in X
        float diffX = pos.x - posLastFrame.x;

        // Factor to damp the energy, otherwise the player would glitch threw the world
        float damping = 0.02f;
        if(collides){
            damping = 0.2f;
        }

        // Generate a damped version of the difference
        pos.x = posLastFrame.x + diffX * (1.0f-damping);

        // Check weather speed is under maxSpeed
        if (pos.x - posLastFrame.x > maxSpeed.x)
            pos.x = posLastFrame.x + maxSpeed.x;

        if (pos.x - posLastFrame.x < -maxSpeed.x)
            pos.x = posLastFrame.x - maxSpeed.x;

        if (pos.y - posLastFrame.y > maxSpeed.y)
            pos.y = posLastFrame.y + maxSpeed.y;

        if (pos.y - posLastFrame.y < -maxSpeed.y)
            pos.y = posLastFrame.y - maxSpeed.y;

        // Check window boundaries
        if (pos.x < 0)
            pos.x = 0;

        if (pos.x > l.lvlSize.x-Tile.tileSize)
            pos.x = l.lvlSize.x-Tile.tileSize;

        updateBoundingBox();
        walkingLeft = false;
        walkingRight = false;
        jump = false;
    }

    public void updateBoundingBox(){
        // update BoundingBox
        boundingBox.min.x = pos.x;
        boundingBox.min.y = pos.y;

        boundingBox.max.x = pos.x + tilesWalk.get(0).getWidth();
        boundingBox.max.y = pos.y + tilesWalk.get(0).getHeight();
    }

    public BufferedImage getEnemyImage() {
        BufferedImage b = getNextTile();
        if (facingLeft) {
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-b.getWidth(null), 0);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            b = op.filter(b, null);
        }
        return b;
    }

    private BufferedImage getNextTile() {
        if ((walkingLeft || walkingRight)) {
            moveCounter++;
            if(moveCounter>=3) {
                displayedAnimationState++;
                moveCounter = 0;
            }
            if (displayedAnimationState > numberAnimationStates - 1) {
                displayedAnimationState = 0;
            }
            return tilesWalk.get(displayedAnimationState);
        }
        return tilesWalk.get(7);
    }
}
