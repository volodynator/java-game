import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Healer extends Item{
    int healPoints;

    public Healer(Player player, int healPoints) throws IOException {
        this.healPoints = healPoints;
        this.player = player;
        this.icon = ImageIO.read(new File("assets/Items/mushroomRed.png"));
    }

    @Override
    public void use() throws IOException, NotEnoughManaExeption {
        player.heal(healPoints);
    }
}
