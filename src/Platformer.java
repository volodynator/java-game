import java.awt.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Platformer extends JFrame {
	public static final String BasePath = "./assets/";
	@Serial
	private static final long serialVersionUID = 5736902251450559962L;

	private Player p;
	private Level l = null;
	private boolean isFullScreen = false;
	BufferStrategy bufferStrategy;
	java.util.List<Bullet> bullets = new CopyOnWriteArrayList<>();
	java.util.List<Bullet> bulletsToRemove = new CopyOnWriteArrayList<>();
	java.util.List<Explosion> explosions = new CopyOnWriteArrayList<>();

	BufferedImage frameNotSelected = ImageIO.read(new File("assets/ourAssets/frames/frameNotSelected.png"));
	BufferedImage frameSelected = ImageIO.read(new File("assets/ourAssets/frames/frameSelected.png"));


	private int selectedItem = 0;



	Timer gameStateUpdateTrigger;
	boolean notEnoughMana = false;


	//TODO despawn monsters after respawn
	//TODO draw tiles
	//TODO kill monsters

	public Platformer() throws IOException {
		//exit program when window is closed
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
					System.exit(0);
			}
		});

		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("./"));
		fc.setDialogTitle("Select input image");
		FileFilter filter = new FileNameExtensionFilter("Level image (.bmp)","bmp");
		fc.setFileFilter(filter);
		int result = fc.showOpenDialog(this);
		File selectedFile = new File("");
		addKeyListener(new AL(this));
		createBufferStrategy(2);
		bufferStrategy = this.getBufferStrategy();


		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fc.getSelectedFile();
			System.out.println("Selected file: " + selectedFile.getAbsolutePath());
		} else {
			dispose();
			System.exit(0);
		}

		try {
			l = new Level(selectedFile.getAbsolutePath(), "assets/ourAssets/bestBackground (1).png");
			p = l.player;


			this.setBounds(0, 0, 1000, 10 * 70);
			this.setVisible(true);
			gameStateUpdateTrigger = new Timer();
			gameStateUpdateTrigger.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					try {
						updateGameStateAndRepaint();
					} catch (IOException e) {
						throw new RuntimeException(e);
					} catch (NotEnoughManaExeption e) {
						throw new RuntimeException(e);
					}
				}

			}, 0, 10);
			playSound(BasePath + "Sound/soundtrack.wav");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void restart() throws IOException {
		p.pos.x = 0;
		p.pos.y = 0;
		l.offsetX = 0;
		p.hp = 100;
		l.enemies.clear();
		bullets.clear();
		l.initLevel();
		p.points = 0;
	}

	private void updateGameStateAndRepaint() throws IOException, NotEnoughManaExeption {
		l.update();
		p.update();
		for (Enemy enemy : l.enemies) {
			enemy.update();

			Iterator<Bullet> bulletIterator = enemy.bullets.iterator();
			while (bulletIterator.hasNext()) {
				Bullet bullet = bulletIterator.next();
				bullet.update();
				if (bullet.shouldBeRemoved()) {
					bulletIterator.remove();
				}
			}
			bullets.addAll(enemy.bullets);
		}

		checkCollision();
		checkEnemiesCollision();
		bullets.removeAll(bulletsToRemove);
		bulletsToRemove.clear();
		for (Explosion explosion : explosions) {
			explosion.update();
			if (explosion.isFinished()) {
				explosions.remove(explosion);
			}
		}
		if (l.enemies.isEmpty()){
			restart();
		}
		repaint();
	}


	private void checkCollision() {
		float playerPosX = p.pos.x;

		p.collidesDown = false;
		p.collidesLeft = false;
		p.collidesRight = false;
		p.collidesTop = false;
		p.collides = false;

		if (!bullets.isEmpty()) {
			Iterator<Bullet> bulletIterator = bullets.iterator();
			while (bulletIterator.hasNext()) {
				Bullet bullet = bulletIterator.next();
				bullet.update();
				if (!bullet.ownBullet){
					if (p.boundingBox.intersect(bullet.boundingBox) && !bullet.hasCollided) {
						if (!p.hasShield){
							p.damage(bullet.damage);
						}
						else {
							if (p.shield.hp>0){
								p.shield.hp--;
							}
							else {
								p.hasShield=false;
							}
						}
						explosions.add(new Explosion(bullet.x, bullet.y, l));
						bullet.hasCollided=true;
						bulletsToRemove.add(bullet);
					}
				}
			}
		}

		// Collision
		for (int i = 0; i < l.tiles.size(); i++) {

			Tile tile = l.tiles.get(i);

			float epsilon = 12.f; // experiment with this value. If too low,the player might get stuck when walking over the
			// ground. If too high, it can cause glitching inside/through walls

			Vec2 overlapSize = tile.bb.OverlapSize(p.boundingBox);

			if (overlapSize.x >= 0 && overlapSize.y >= 0 && Math.abs(overlapSize.x + overlapSize.y) >= epsilon) {

				if(tile.hasRigidCollision) {
					if (Math.abs(overlapSize.x) > Math.abs(overlapSize.y)) {// Y overlap correction

						if (p.boundingBox.min.y + p.boundingBox.max.y > tile.bb.min.y + tile.bb.max.y) { // player comes from below
							p.pos.y += overlapSize.y;
							p.collidesTop = true;
						} else { // player comes from above
							p.pos.y -= overlapSize.y;
							p.collidesDown = true;
						}
					} else { // X overlap correction
						if (p.boundingBox.min.x + p.boundingBox.max.x > tile.bb.min.x + tile.bb.max.x) { // player comes from right
							p.pos.x += overlapSize.x;
							p.collidesLeft = true;
						} else { // player comes from left
							p.pos.x -= overlapSize.x;
							p.collidesRight = true;
						}
					}
				}
				p.collides = true;
				tile.onCollision(p);
				p.updateBoundingBox();
				if (p.hp <= 0)
					try {
						gameOver();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
	}
	private void checkEnemiesCollision() {
		for (Enemy enemy: l.enemies) {
			float enemyPosX = enemy.pos.x;

			enemy.collidesDown = false;
			enemy.collidesLeft = false;
			enemy.collidesRight = false;
			enemy.collidesTop = false;
			enemy.collides = false;

			if (!bullets.isEmpty()) {
				Iterator<Bullet> bulletIterator = bullets.iterator();
				while (bulletIterator.hasNext()) {
					Bullet bullet = bulletIterator.next();
					if (bullet != null) {
						bullet.update();
					}
					if (bullet.ownBullet){
						if (enemy.boundingBox.intersect(bullet.boundingBox) && !bullet.hasCollided) {
							enemy.damage(bullet.damage);
							if (enemy.hp<=0){
								l.enemies.remove(enemy);
							}
							explosions.add(new Explosion(bullet.x, bullet.y, l));
							bullet.hasCollided=true;
							bulletsToRemove.add(bullet);
						}
					}
				}
			}

			// Collision
			for (int i = 0; i < l.tiles.size(); i++) {

				Tile tile = l.tiles.get(i);

				float epsilon = 8.f; // experiment with this value. If too low,the player might get stuck when walking over the
				// ground. If too high, it can cause glitching inside/through walls

				Vec2 overlapSize = tile.bb.OverlapSize(enemy.boundingBox);

				if (overlapSize.x >= 0 && overlapSize.y >= 0 && Math.abs(overlapSize.x + overlapSize.y) >= epsilon) {

					if(tile.hasRigidCollision) {
						if (Math.abs(overlapSize.x) > Math.abs(overlapSize.y)) {// Y overlap correction

							if (enemy.boundingBox.min.y + enemy.boundingBox.max.y > tile.bb.min.y + tile.bb.max.y) { // player comes from below
								enemy.pos.y += overlapSize.y;
								enemy.collidesTop = true;
							} else { // player comes from above
								enemy.pos.y -= overlapSize.y;
								enemy.collidesDown = true;
							}
						} else { // X overlap correction
							if (enemy.boundingBox.min.x + enemy.boundingBox.max.x > tile.bb.min.x + tile.bb.max.x) { // player comes from right
								enemy.pos.x += overlapSize.x;
								enemy.collidesLeft = true;
							} else { // player comes from left
								enemy.pos.x -= overlapSize.x;
								enemy.collidesRight = true;
							}
						}
					}
					enemy.collides = true;
					tile.onCollisionWithEnemy(enemy);
					enemy.updateBoundingBox();
					if (enemy.hp <= 0)
						System.out.println("Enemy died");
				}
			}
		}
	}

	private void gameOver() throws IOException {
		restart();
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = null;

		try {
			g2 = (Graphics2D) bufferStrategy.getDrawGraphics();
			draw(g2);

		} finally {
			g2.dispose();
		}
		bufferStrategy.show();
	}

	private void draw(Graphics2D g2d) {
		BufferedImage level = (BufferedImage) l.getResultingImage();
		if (l.offsetX > level.getWidth() - 1000)
			l.offsetX = level.getWidth() - 1000;
		BufferedImage bi = level.getSubimage((int) l.offsetX, 0, 1000, level.getHeight());
		g2d.drawImage(l.backgroundImage, 0, 0, this);
		g2d.drawImage(bi, 0, 0, this);

		for (int i = 0; i < l.tiles.size(); i++) {
			l.tiles.get(i).draw(g2d, l.offsetX, 0);
		}
		g2d.drawImage(getPlayer().getPlayerImage(), (int) (getPlayer().pos.x - l.offsetX), (int) getPlayer().pos.y, this);

		for (Enemy enemy : l.enemies) {
			g2d.drawImage(enemy.getEnemyImage(), (int) (enemy.pos.x - l.offsetX), (int) enemy.pos.y, this);
		}

		for (Enemy enemy : l.enemies) {
			bullets.addAll(enemy.bullets);
		}
		for (Explosion explosion : explosions) {
			explosion.draw(g2d);
		}

		drawBullets(g2d);
		drawHPMana(g2d);
		drawSkills(g2d);
		drawShield(g2d);
	}


	public void drawBullets(Graphics2D g2d){
		for (Bullet bullet : bullets) {
			g2d.drawImage(bullet.image, bullet.x - (int) l.offsetX, bullet.y, this);
		}
	}
	public void drawHPMana(Graphics2D g2d){
		// HP
		g2d.setColor(new Color(138, 23, 23));
		g2d.drawRoundRect(50, 50, 300, 20, 4, 4);
		g2d.fillRoundRect(50, 50, getPlayer().hp * 3, 20, 4, 4);

		// Mana
		g2d.setColor(new Color(51, 99, 187));
		g2d.drawRoundRect(50, 90, 300, 20, 4, 4);
		g2d.fillRoundRect(50, 90, getPlayer().mana * 3, 20, 4, 4);
		if (notEnoughMana) {
			g2d.setColor(Color.RED);
			g2d.fillRoundRect(50, 90, getPlayer().mana * 3, 20, 4, 4);
			notEnoughMana = false;
		}
		int rectBottom = 110;

		g2d.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 28));
		g2d.setColor(Color.WHITE);

		FontMetrics metrics = g2d.getFontMetrics();

		int yPosition = rectBottom - metrics.getHeight();

		g2d.setColor(Color.BLACK);
		g2d.drawString("Enemies left " + l.enemies.size(), 402, yPosition + 2);

		g2d.setColor(Color.WHITE);
		g2d.drawString("Enemies left " + l.enemies.size(), 400, yPosition);
	}
	public void drawSkills(Graphics2D g2d){
		int space = 0;
		for (int i = 0; i < 4; i++) {
			if (i == selectedItem) {
				g2d.drawImage(frameSelected, 650+space, 50, this);
			}
			else {
				g2d.drawImage(frameNotSelected, 650+space, 50, this);
			}
			g2d.drawImage(getPlayer().itemsList.get(i).icon, 650 + space, 50, 70, 70, this);
			space += 70;
		}
	}
	public void drawShield(Graphics2D g2d){
		if (getPlayer().hasShield) {
			int shieldWidth = 100;
			int shieldHeight = 100;

			int playerCenterX = (int) (p.pos.x - l.offsetX) + (p.tilesWalk.get(0).getWidth() / 2);
			int playerCenterY = (int) p.pos.y + (p.tilesWalk.get(0).getHeight() / 2);

			g2d.drawOval(playerCenterX - shieldWidth / 2, playerCenterY - shieldHeight / 2, shieldWidth, shieldHeight);

		}
	}

	public Player getPlayer() {
		return this.p;
	}

	public Level getLevel() {
		return this.l;
	}

	public void setFullScreenMode(boolean b) {
		this.isFullScreen = b;
	}

	public boolean getFullScreenMode() {
		return this.isFullScreen;
	}

	public class AL extends KeyAdapter {
		Platformer p;

		public AL(Platformer p) {
			super();
			this.p = p;
		}

		@Override
		public void keyPressed(KeyEvent event) {
			int keyCode = event.getKeyCode();
			Player player = p.getPlayer();

			if (keyCode == KeyEvent.VK_ESCAPE) {
				dispose();
			}
			if (keyCode == KeyEvent.VK_1) {
				selectedItem = 0;
			}

			if (keyCode == KeyEvent.VK_2) {
				selectedItem = 1;
			}

			if (keyCode == KeyEvent.VK_3) {
				selectedItem = 2;
			}

			if (keyCode == KeyEvent.VK_4) {
				selectedItem = 3;
			}

			if (keyCode == KeyEvent.VK_LEFT) {
				player.walkingLeft = true;
			}

			if (keyCode == KeyEvent.VK_RIGHT) {
				player.walkingRight = true;
			}

			if (keyCode == KeyEvent.VK_SPACE) {
				player.jump = true;
			}

			if (keyCode == KeyEvent.VK_E) {
				try {
					System.out.println("Mana: " + player.mana);

					player.itemsList.get(selectedItem).use();
					bullets.addAll(player.bullets);
				} catch (IOException | NotEnoughManaExeption e) {
					notEnoughMana = true;
					player.playSound("assets/Sound/button-18.wav");
				}
			}
			if (keyCode == KeyEvent.VK_R) {
				try {
					restart();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent event) {
			int keyCode = event.getKeyCode();
			Player player = p.getPlayer();

			if (keyCode == KeyEvent.VK_UP) {
			}

			if (keyCode == KeyEvent.VK_DOWN) {
			}

			if (keyCode == KeyEvent.VK_LEFT) {
				player.walkingLeft = false;
			}

			if (keyCode == KeyEvent.VK_RIGHT) {
				player.walkingRight = false;
			}

			if (keyCode == KeyEvent.VK_SPACE) {
				player.jump = false;
			}
		}
	}

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
}