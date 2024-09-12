import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Player extends GameObject {
    private static final int gravity = 1;
    private static final int jumpStrength = 20;
    private static final int maxFallSpeed = 15;
    private static final float airResistance = 0.85f;
    public boolean inAir = false;
    public boolean wantsToJump = false;
    int hp = 20;
    int mana = 100;
    List<Bullet> bullets = new ArrayList<>();
    private int startPosX, startPosY;
    private int velx = 0, vely = 0;

    List<BufferedImage> images = new ArrayList<>();

    List<Item> itemsList = new ArrayList<>();
    Shield shield = null;

    Weapon weapon = new Gun(this, 30, 50);
    int curr = 0;
    public static final String path = ".\\assets\\Player\\";

    public Player() throws IOException {
        this.x = 10;
        this.y = 10;
        this.startPosX = this.x;  // Save initial position for restarts
        this.startPosY = this.y;


        for (int i = 1; i < 10; i++) {
            images.add(ImageIO.read(new File(path + "p1_walk\\PNG\\p1_walk0" + i + ".png")));
        Healer healer = new Healer(this, 20);
        ManaRecovery manaRecovery = new ManaRecovery(this, 20);
        itemsList.add(weapon);
        itemsList.add(healer);
        itemsList.add(manaRecovery);
        itemsList.add(weapon);
        for(int i = 1; i<10; i++){
            images.add(ImageIO.read(new File( path +"p1_walk\\PNG\\p1_walk0"+i+".png")));
        }
        images.add(ImageIO.read(new File(path + "p1_walk\\PNG\\p1_walk10.png")));
        images.add(ImageIO.read(new File(path + "p1_walk\\PNG\\p1_walk11.png")));

        this.width = images.get(0).getWidth();
        this.height = images.get(0).getHeight();
        this.boundingBox = new BoundingBox(new Vec2(x, y), new Vec2(x + width, y - height));
    }

    public BufferedImage getImage() {
        return images.get(curr);
    }

    public void updateAnimation() {
        curr = (curr + 1) % images.size();
    }

    public void move(String direction) {
        if ("left".equals(direction)) {
            velx = -10;
        } else if ("right".equals(direction)) {
            velx = 10;
        }
        updateAnimation();
    }
    public void playSound(String path){
        File lol = new File(path);
        try{
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(lol));
            clip.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void jump() {
        if (!inAir) {
            vely = -jumpStrength;
            inAir = true;
        }
    }

    public void restart() {
        x = startPosX;
        y = startPosY;
        velx = 0;
        vely = 0;
        inAir = false;
    }
    public void heal(int points){
        if (hp+points>100){
            hp = 100;
        }
        else {
            hp+=points;
        }
        this.playSound("assets/Sound/burp-1.wav");
    }

    public void damage(){
        if (hp>0){
            this.hp--;
        }
    }

    public void manaRegeneration(int points){
        if (mana+points>100){
            mana=100;
        }
        else {
            mana+=points;
        }
        this.playSound("assets/Sound/magic-chime-01 2.wav");
    }


    public void update() {

        if (inAir) {
            vely += gravity;
            if (vely > maxFallSpeed) {
                vely = maxFallSpeed;
            }
        } else vely = 0;


        x += velx;
        y += vely;


        if (velx != 0 && !inAir) {
            velx--;
            if (Math.abs(velx) < 1) velx = 0;
        }


        if (inAir) {
            velx = (int) (velx * airResistance);
        }

        // Update bounding box for collision detection
        this.boundingBox.min.x = x;
        this.boundingBox.min.y = y;
        this.boundingBox.max.x = x + width;
        this.boundingBox.max.y = y + height;
    }
}
