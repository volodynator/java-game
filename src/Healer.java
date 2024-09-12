import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Healer extends Item{
    int healPoints;

    public Healer(Level level, int healPoints) throws IOException {
        this.healPoints = healPoints;
        this.level = level;
        this.icon = ImageIO.read(new File("assets/ourAssets/chickenLeg.png"));
    }

    @Override
    public void use() throws IOException, NotEnoughManaExeption {
        level.player.heal(healPoints);
    }
}
