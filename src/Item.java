import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class Item {
    BufferedImage icon;
    Level level;

    public void use() throws IOException, NotEnoughManaExeption {

    };
}
