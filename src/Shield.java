import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Shield extends Item{
    public Shield(Player player) throws IOException {
        this.player = player;
        this.icon = ImageIO.read(new File("assets/Items/star.png"));
    }

    @Override
    public void use() throws IOException, NotEnoughManaExeption {
        player.shield = new Shield(player);
    }
}
