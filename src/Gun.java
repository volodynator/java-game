import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Gun extends Weapon{

    public Gun(Player player, int cost, int damage) throws IOException {
        this.player = player;
        this.mana = cost;
        this.damage = damage;
        this.icon = ImageIO.read(new File("assets/Items/fireball.png"));
    }

    @Override
    public void use() throws IOException, NotEnoughManaExeption {
        if (player.mana-mana>0){
            player.mana-=mana;
            player.bullets.add(new Bullet(player.x + 2, player.y + 2));
            player.playSound("C:\\Users\\Volodymyr\\Downloads\\Step0\\Step0\\assets\\Sound\\gun-gunshot-01.wav");
        }
        else {
            throw new NotEnoughManaExeption();
        }
    }
}
