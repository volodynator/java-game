import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Player extends GameObject {



    int prevX;
    int prevY;
    int speedX;
    int speedY;
    int hp = 20;
    int mana = 100;
    List<Bullet> bullets = new ArrayList<>();

    List<BufferedImage> images = new ArrayList<>();

    List<Item> itemsList = new ArrayList<>();
    Shield shield = null;

    Weapon weapon = new Gun(this, 30, 50);
    int curr = 0;
    public static final String path = ".\\assets\\Player\\";

    public Player() throws IOException {
        this.x = 10;
        this.y = 10;
        Healer healer = new Healer(this, 20);
        ManaRecovery manaRecovery = new ManaRecovery(this, 20);
        itemsList.add(weapon);
        itemsList.add(healer);
        itemsList.add(manaRecovery);
        itemsList.add(weapon);
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


}
