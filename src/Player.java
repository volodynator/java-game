import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Player {
	boolean jump = false, walkingLeft = false, walkingRight = false;
	boolean collidesTop = false, collidesDown = false, collidesLeft = false, collidesRight = false, collides = false;

	boolean facingLeft = false;

	Vec2 pos;
	Vec2 posLastFrame;
	Vec2 gravity;
	Vec2 maxSpeed;

	List<Item> itemsList = new ArrayList<>();
	Shield shield = null;
	boolean hasShield = false;
	float tmp;
	int curr = 0;

	int hp = 20;
	int mana = 100;
	List<Bullet> bullets = new ArrayList<>();

	public Vec2 lastValidPosition;

	float movementSpeed;

	BoundingBox boundingBox;
	int numberAnimationStates = 0;
	int displayedAnimationState = 0;
	int moveCounter = 0;
	int points = 0;

	float jumpPower = 35.f;

	// Tiles for movement animation
	protected ArrayList<BufferedImage> tilesWalk;
	
	// Tiles for player's life on HUD
	protected ArrayList<BufferedImage> tilesLife;
	Level l;

	Player(Level l) throws IOException {
		this.pos = new Vec2(0, 0);
		this.posLastFrame = new Vec2(0, 0);
		this.gravity = new Vec2(0, 0.35f);
		this.maxSpeed = new Vec2(5, 10);
		this.movementSpeed = 3.5f;

		this.l = l;
		tilesWalk = new ArrayList<BufferedImage>();
		tilesLife = new ArrayList<BufferedImage>();
		try {
			
			// Tiles for movement animation
			BufferedImage imageWalk;
			BufferedImage imageEmpty;
			BufferedImage imageHalf;
			imageWalk = ImageIO.read(new File("assets/ourAssets/walk/1.0.png"));
			tilesWalk.add(imageWalk);
			imageHalf = ImageIO.read(new File("assets/ourAssets/walk/2.0.png"));
			tilesWalk.add(imageHalf);
			imageEmpty = ImageIO.read(new File("assets/ourAssets/walk/3.0.png"));
			tilesWalk.add(imageEmpty);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		boundingBox = new BoundingBox(0, 0, tilesWalk.get(0).getWidth(), tilesWalk.get(0).getHeight());
		numberAnimationStates = tilesWalk.size();

	}

	void move(int deltaX) {
		if (deltaX < 0) {
				pos.x = pos.x - movementSpeed / 4;
		} else if (deltaX > 0) {
				pos.x = pos.x + movementSpeed / 4;
		}
	}

	public void kill() {
		if (hp > 0) {
			hp-=50;
			pos.x = lastValidPosition.x;
			pos.y = lastValidPosition.y;
			posLastFrame.x = pos.x;
			posLastFrame.y = pos.y;
		}

	}

	public void update() {

		Iterator<Bullet> bulletIterator = bullets.iterator();
		while (bulletIterator.hasNext()) {
			Bullet bullet = bulletIterator.next();
			bullet.update();

			if (bullet.delete()) {
				bulletIterator.remove();
			}
		}

		// Check if walking and call move()
		if (walkingLeft){
			move(-1);
			facingLeft = true;
		}
		if (walkingRight){
			move(1);
			facingLeft = false;
		}

		if(jump && collidesDown){
			pos.y -= jumpPower;
		}

		// Save old position
		Vec2 pos_lastFrame_temp = pos;

		// Add gravity and move according to the actual speed
		pos = pos.add(pos.sub(posLastFrame));

		// Get saved old Position back
		posLastFrame = pos_lastFrame_temp;

		//apply gravity
		pos = pos.add(gravity);

		// Calculate difference in X
		float diffX = pos.x - posLastFrame.x;
		
		// Factor to damp the energy, otherwise the player would glitch threw the world
		float damping = 0.02f;
		if(collides){
			damping = 0.2f;
		}

		// Generate a damped version of the difference
		pos.x = posLastFrame.x + diffX * (1.0f-damping);

		// Check weather speed is under maxSpeed 
		if (pos.x - posLastFrame.x > maxSpeed.x)
			pos.x = posLastFrame.x + maxSpeed.x;

		if (pos.x - posLastFrame.x < -maxSpeed.x)
			pos.x = posLastFrame.x - maxSpeed.x;

		if (pos.y - posLastFrame.y > maxSpeed.y)
			pos.y = posLastFrame.y + maxSpeed.y;

		if (pos.y - posLastFrame.y < -maxSpeed.y)
			pos.y = posLastFrame.y - maxSpeed.y;

		// Check window boundaries
		if (pos.x < 0)
			pos.x = 0;

		if (pos.x > l.lvlSize.x-Tile.tileSize)
			pos.x = l.lvlSize.x-Tile.tileSize;

		updateBoundingBox();
	}

	public void updateBoundingBox(){
		// update BoundingBox
		boundingBox.min.x = pos.x;
		boundingBox.min.y = pos.y;

		boundingBox.max.x = pos.x + tilesWalk.get(0).getWidth();
		boundingBox.max.y = pos.y + tilesWalk.get(0).getHeight();
	}

	public BufferedImage getPlayerImage() {
		BufferedImage b = getNextTile();
		if (facingLeft) {
			AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
			tx.translate(-b.getWidth(null), 0);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			b = op.filter(b, null);
		}
		return b;
	}

	private BufferedImage getNextTile() {
		if ((walkingLeft || walkingRight)) {
			moveCounter++;
			if(moveCounter>=2) {
				displayedAnimationState++;
				moveCounter = 0;
			}
			if (displayedAnimationState > numberAnimationStates - 1) {
				displayedAnimationState = 0;
			}
			return tilesWalk.get(displayedAnimationState);
		}
		return tilesWalk.get(1);
	}
	
	//public void playSound(String path) {
	//    try {
    //        new javafx.embed.swing.JFXPanel();
    //        String uriString = new File(path).toURI().toString();
    //        javafx.scene.media.Media m = new javafx.scene.media.Media(uriString);
    //        MediaPlayer mp = new MediaPlayer(m);
    //        mp.play();
	//    } catch(Exception ex) {
	//        ex.printStackTrace();
	//    }
	//}

	public void playSound(String path){
		File lol = new File(path);

		try{
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(lol));
			clip.start();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	public void heal(int points){
		if (hp+points>100){
			hp = 100;
		}
		else {
			hp+=points;
		}
		this.playSound("assets/ourAssets/effects/sound_heal.wav");
	}
	public void manaRegeneration(int points){
		if (mana+points>100){
			mana=100;
		}
		else {
			mana+=points;
		}
		this.playSound("assets/ourAssets/effects/sound_mana.wav");
	}
	public void damage(int points){
		hp -= points;
		if (hp<0){
			hp=0;
		}
	}


}
