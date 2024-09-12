import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ManaRecovery extends Item{
    int points;
    public ManaRecovery(Player player, int points) throws IOException {
        this.points = points;
        this.player = player;
        this.icon = ImageIO.read(new File("assets/Items/plantPurple.png"));
    }

    @Override
    public void use() throws IOException, NotEnoughManaExeption {
        player.manaRegeneration(points);
    }
}
