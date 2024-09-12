import java.io.IOException;

public class Gun implements Weapon{
    Player player;

    public Gun(Player player){
        this.player = player;
    }

    @Override
    public Bullet use() throws IOException {
        System.out.println("Shoot");
        return new Bullet(player.x + 2, player.y + 2);
    }
}
