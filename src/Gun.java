import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Gun extends Weapon{

    public Gun(Level level, int cost, int damage) throws IOException {
        this.level = level;
        this.mana = cost;
        this.damage = damage;
        this.icon = ImageIO.read(new File("assets/Items/fireball.png"));
    }

    @Override
    public void use() throws IOException, NotEnoughManaExeption {
        if (level.player.mana-mana>0){
            level.player.mana-=mana;
            Bullet blt = new Bullet((int) (level.player.pos.x - level.offsetX), (int) level.player.pos.y);
            level.player.bullets.add(blt);
            level.player.playSound("assets\\Sound\\gun-gunshot-01.wav");
        }
        else {
            throw new NotEnoughManaExeption();
        }
    }
}
