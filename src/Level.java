import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

public class Level {
	BufferedImage levelImg, resultingLevelImg, backgroundImage, cloudImage1, mushroomImage;
	public Player player = new Player(this);
	Vec2 lvlSize;
	float offsetX;
	ArrayList<Tile> tiles;
	ArrayList<Enemy> enemies = new ArrayList<>();

	public Level(String levelMapPath, String levelBackgroundMapPath) throws IOException {
		try {
			backgroundImage = ImageIO.read(new File(levelBackgroundMapPath));
			tiles = new ArrayList<Tile>();
			lvlSize = new Vec2(0, 0);
			offsetX = 0.0f;

			try {
				// Level image
				levelImg = ImageIO.read(new File(levelMapPath));

				// Cloud image
				cloudImage1 = ImageIO.read(new File("./assets/Items/cloud1.png"));
				mushroomImage = ImageIO.read(new File("assets/ourAssets/hellMushroom.png"));

				// Tile images
				Tile.images.add(ImageIO.read(new File("assets/ourAssets/img_2.png"))); //lava
				Tile.images.add(ImageIO.read(new File("assets/ourAssets/img.png"))); //grass

				//TODO draw gold and dirt
				Tile.images.add(ImageIO.read(new File("assets/ourAssets/gold.png"))); //gold
				Tile.images.add(ImageIO.read(new File("assets/ourAssets/dirt.png"))); //dirt



				Healer healer = new Healer(this, 20);
				Weapon weapon = new Gun(this, 30, 20);
				ManaRecovery manaRecovery = new ManaRecovery(this, 20);
				Shield shield1 = new Shield(this);
				player.itemsList.add(weapon);
				player.itemsList.add(healer);
				player.itemsList.add(manaRecovery);
				player.itemsList.add(shield1);
			} catch (IOException e) {

			}
			initLevel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update() {

		//update camera offset
		float diff = (player.boundingBox.max.x + player.boundingBox.min.x)*0.5f-500  - offsetX;

		int noMoveZone = 100;

		if(Math.abs(diff)>noMoveZone){
			if(diff<0)
				diff+=noMoveZone;
			else
				diff-=noMoveZone;
			offsetX += diff;
		}


		if (offsetX < 0)
			offsetX = 0;

		if (offsetX > resultingLevelImg.getWidth() - 1000)
			offsetX = resultingLevelImg.getWidth() - 1000;
	}

	public void initLevel() throws IOException {
		lvlSize.x = Tile.tileSize * levelImg.getWidth(null);
		lvlSize.y = Tile.tileSize * levelImg.getHeight(null);

		resultingLevelImg = new BufferedImage((int) lvlSize.x, (int) lvlSize.y, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = null;
		g2d = (Graphics2D) resultingLevelImg.getGraphics();

//		for (int x = 0; x < resultingLevelImg.getWidth(null); x += backgroundImage.getWidth()) {
//
//			g2d.drawImage((BufferedImage) backgroundImage, null, x, 0);
//
//			AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
//			tx.translate(-backgroundImage.getWidth(null), 0);
//			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
//			backgroundImage = op.filter(backgroundImage, null);
//		}

//		for (int x = 0; x < resultingLevelImg.getWidth(null); x += cloudImage1.getWidth() * 2) {
//			Random r = new Random();
//			g2d.drawImage((BufferedImage) cloudImage1, null, x + r.nextInt(250), r.nextInt(250) + 50);
//		}

		tiles.clear();

		for (int y = 0; y < levelImg.getHeight(null); y++) {
			for (int x = 0; x < levelImg.getWidth(null); x++) {
				Color color = new Color(levelImg.getRGB(x, y));

				 Tile t = null;

				// Compare color of pixels in order to select the corresponding tiles

				if (color.equals(Color.BLUE))
					t = new TileWater(0,x*Tile.tileSize,y*Tile.tileSize);
				if (color.equals(Color.BLACK)){

					float xPosition = x*Tile.tileSize;
					float yPosition = y*Tile.tileSize;
					Random r = new Random();
					double d = r.nextDouble();
					if (d>0.2 && d<0.23){
						Enemy enemy = new Enemy(this, (int) xPosition, (int) yPosition-70);
						enemies.add(enemy);
					}
					if (r.nextDouble(1)<0.2){
						g2d.drawImage(mushroomImage,null, (int) xPosition, (int) yPosition-70);
					}
					t = new Tile(1, xPosition, yPosition);
				}
				if (color.equals(Color.YELLOW))
					t = new Tile(2,x*Tile.tileSize,y*Tile.tileSize,false);
				if (color.equals(Color.ORANGE))
					t = new Tile(3,x*Tile.tileSize,y*Tile.tileSize,false	);


				if(t!=null) {
					tiles.add(t);
					// Get graphics context
					g2d = (Graphics2D) resultingLevelImg.getGraphics();

					// Draw tile into data of image
					t.drawStatic(g2d,0,0);
				}
			}
		}
		g2d.dispose();
	}

	public Image getResultingImage() {
		return resultingLevelImg;
	}

	public int getSizeX() {
		return resultingLevelImg.getWidth();
	}

	public int getSizeY() {
		return resultingLevelImg.getHeight();
	}
}
