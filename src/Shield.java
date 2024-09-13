import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Shield extends Item{
    int hp = 2;
    public Shield(Level level) throws IOException {
        this.level = level;
        this.icon = ImageIO.read(new File("assets/ourAssets/shield.png"));
    }

    @Override
    public void use() throws IOException, NotEnoughManaExeption {
        if (level.player.mana>=50){
            level.player.shield = new Shield(level);
            level.player.hasShield=true;
            level.player.mana-=50;
            level.player.playSound("assets/ourAssets/effects/sound_shield_create.wav");
        }
        else {
            throw new NotEnoughManaExeption();
        }

    }
}
