import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ManaRecovery extends Item{
    int points;
    public ManaRecovery(Level level, int points) throws IOException {
        this.points = points;
        this.level = level;
        this.icon = ImageIO.read(new File("assets/Items/plantPurple.png"));
    }

    @Override
    public void use() throws IOException, NotEnoughManaExeption {
        level.player.manaRegeneration(points);
    }
}
