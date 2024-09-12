import java.io.IOException;

public class EnemyGun {
    int damage;
    Enemy enemy;
    Level level;

    public EnemyGun(Enemy enemy, Level level, int damage){
        this.damage = damage;
        this.enemy = enemy;
        this.level = level;
    }

    public void shoot() throws IOException {
        Bullet blt = new Bullet((int) (enemy.pos.x - level.offsetX), (int) enemy.pos.y-10, damage);
        if (enemy.facingLeft){
            blt.movingLeft = true;
        }
        enemy.bullets.add(blt);
        level.player.playSound("assets\\Sound\\gun-gunshot-01.wav");
    }

}
