import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Shield extends Item{
    public Shield(Level level) throws IOException {
        this.level = level;
        this.icon = ImageIO.read(new File("assets/ourAssets/shield.png"));
    }

    @Override
    public void use() throws IOException, NotEnoughManaExeption {
        level.player.shield = new Shield(level);
    }
}
