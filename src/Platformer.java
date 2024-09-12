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
	java.util.List<Bullet> bullets = new ArrayList<>();
	private int selectedItem = 0;



	Timer gameStateUpdateTrigger;
	boolean notEnoughMana = false;

	public Platformer() {
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
			l = new Level(selectedFile.getAbsolutePath(), BasePath + "background0.png");
			p = l.player;

			this.setBounds(0, 0, 1000, 12 * 70);
			this.setVisible(true);
			gameStateUpdateTrigger = new Timer();
			gameStateUpdateTrigger.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					updateGameStateAndRepaint();
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
		l.initLevel();
		p.points = 0;
	}

	private void updateGameStateAndRepaint() {
		l.update();
		p.update();
		checkCollision();
		repaint();
		Iterator<Bullet> bulletIterator = bullets.iterator();
		while (bulletIterator.hasNext()) {
			Bullet bullet = bulletIterator.next();
			bullet.update();

			if (bullet.delete()) {
				bulletIterator.remove();
			}
		}
	}

	private void checkCollision() {
		float playerPosX = p.pos.x;

		p.collidesDown = false;
		p.collidesLeft = false;
		p.collidesRight = false;
		p.collidesTop = false;
		p.collides = false;

		// Collision
		for (int i = 0; i < l.tiles.size(); i++) {

			Tile tile = l.tiles.get(i);

			Vec2 overlapSize = tile.bb.OverlapSize(p.boundingBox);

			float epsilon = 8.f; // experiment with this value. If too low,the player might get stuck when walking over the
			                     // ground. If too high, it can cause glitching inside/through walls


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
				if (p.hp == 0)
					try {
						gameOver();
					} catch (IOException e) {
						e.printStackTrace();
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

		for (int i = 0; i< l.tiles.size(); i++) {
			l.tiles.get(i).draw(g2d,l.offsetX,0);
		}
		g2d.drawImage(getPlayer().getPlayerImage(), (int) (getPlayer().pos.x-l.offsetX), (int) getPlayer().pos.y, this);


		drawBullets(g2d);
		drawHPMana(g2d);
		drawSkills(g2d);
		drawShield(g2d);

	}

	public void drawBullets(Graphics2D g2d){
		for (Bullet bullet : bullets) {
			g2d.drawImage(bullet.image, bullet.x, bullet.y, this);
		}
	}
	public void drawHPMana(Graphics2D g2d){
		// HP
		g2d.setColor(Color.RED);
		g2d.drawRoundRect(50, 50, 300, 20, 4, 4);
		g2d.fillRoundRect(50, 50, getPlayer().hp * 3, 20, 4, 4);

		// Mana
		g2d.setColor(Color.BLUE);
		g2d.drawRoundRect(50, 90, 300, 20, 4, 4);
		g2d.fillRoundRect(50, 90, getPlayer().mana * 3, 20, 4, 4);
		if (notEnoughMana) {
			g2d.setColor(Color.RED);
			g2d.fillRoundRect(50, 90, getPlayer().mana * 3, 20, 4, 4);
			notEnoughMana = false;
		}
	}
	public void drawSkills(Graphics2D g2d){
		g2d.setColor(Color.RED);
		int space = 0;
		for (int i = 0; i < 4; i++) {
			if (i == selectedItem) {
				g2d.setColor(Color.GREEN);
			}
			g2d.drawRoundRect(650 + space, 50, 50, 50, 4, 4);
			g2d.drawImage(getPlayer().itemsList.get(i).icon, 650 + space, 50, 50, 50, this);
			space += 70;
			g2d.setColor(Color.RED);
		}
	}
	public void drawShield(Graphics2D g2d){
		if (getPlayer().shield != null) {

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

			if (keyCode == KeyEvent.VK_UP) {
			}

			if (keyCode == KeyEvent.VK_DOWN) {
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