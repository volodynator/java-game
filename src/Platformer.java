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
import java.util.List;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Platformer extends JFrame implements Runnable{
	@Serial
	private static final long serialVersionUID = 5736902251450559962L;

	private Level l = null;
	Player player;
	BufferStrategy bufferStrategy;
	private boolean running = false;
	public List<GameObject> gameObjects = new ArrayList<>();

	private List<Bullet> bullets = new ArrayList<>();
	private boolean notEnoughMana = false;

	private int selectedItem = 0;

	public Platformer() {
		//exit program when window is closed
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("./"));
		fc.setDialogTitle("Select input image");
		FileFilter filter = new FileNameExtensionFilter("Level image (.bmp)", "bmp");
		fc.setFileFilter(filter);
		int result = fc.showOpenDialog(this);
		File selectedFile = new File("");
		addKeyListener(new AL(this));

		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fc.getSelectedFile();
			System.out.println("Selected file: " + selectedFile.getAbsolutePath());
		} else {
			dispose();
			System.exit(0);
		}

		try {
			l = new Level(selectedFile.getAbsolutePath());
			player = new Player();
			player.playSound("C:\\Users\\Volodymyr\\Downloads\\Step0\\Step0\\assets\\Sound\\soundtrack.wav");
			createBufferStrategy(2);
			bufferStrategy = this.getBufferStrategy();

			this.getGraphics().setColor(Color.RED);
			this.setBounds(0, 0, 1000, 10 * 70);
			this.setVisible(true);
			startGame();
		} catch (Exception e) {
			e.printStackTrace();
		}

		gameObjects.addAll(l.levelObjects);
	}

	private void startGame(){
		running = true;
		Thread game = new Thread(this);
		game.start();
	}
	@Override
	public void run() {
		long lastTime = System.nanoTime();
		final double nsPerTick = 1000000000.0 / 60.0; // 60 FPS

		double delta = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;

			while (delta >= 1) {
				updateGameStateAndRepaint();
				delta--;
			}
		}
		paint(getGraphics());
	}


	private void updateGameStateAndRepaint() {
		l.update();
		Iterator<Bullet> bulletIterator = bullets.iterator();
		while (bulletIterator.hasNext()) {
			Bullet bullet = bulletIterator.next();
			bullet.update();

			if (bullet.delete()) {
				bulletIterator.remove();
			}
		}
		int playerCenterX = player.x + player.getImage().getWidth() / 2;
		int maxOffsetX = l.getResultingImage().getWidth(null) - this.getWidth();

		l.offsetX = Math.max(0, Math.min(playerCenterX - this.getWidth() / 2, maxOffsetX));
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) bufferStrategy.getDrawGraphics();
		try {
			try {
				draw(g2);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}finally {
			g2.dispose();
		}
		bufferStrategy.show();
	}

	private void draw(Graphics2D g2d) throws IOException {
		BufferedImage img_level = (BufferedImage) l.getResultingImage();
		BufferedImage visibleLevel =
				img_level.getSubimage((int) l.offsetX, 0, 1000, l.getHeight());
		g2d.drawImage(visibleLevel, 0, 0, this);
		g2d.drawImage(player.getImage(), player.x - (int) l.offsetX, player.y, this);

		// bullets
		for (Bullet bullet: bullets) {
			g2d.drawImage(bullet.image, bullet.x, bullet.y, this);
		}
		// hp
		g2d.setColor(Color.RED);
		g2d.drawRoundRect(50, 50, 300, 20, 4, 4);
		g2d.fillRoundRect(50, 50, player.hp*3, 20, 4, 4);

		// mana
		g2d.setColor(Color.BLUE);
		g2d.drawRoundRect(50, 90, 300, 20, 4, 4);
		g2d.fillRoundRect(50, 90, player.mana*3, 20, 4, 4);
		if (notEnoughMana){
			g2d.setColor(Color.RED);
			g2d.fillRoundRect(50, 90, player.mana*3, 20, 4, 4);
			notEnoughMana=false;
		}
		// skills
		g2d.setColor(Color.RED);
		int space = 0;
		for (int i = 0; i<4; i++) {
			if (i==selectedItem){
				g2d.setColor(Color.GREEN);
			}
			g2d.drawRoundRect(650+space, 50, 50, 50, 4, 4);
			g2d.drawImage(player.itemsList.get(i).icon, 650+space, 50, 50, 50, this);
			space+=70;
			g2d.setColor(Color.RED);
		}
		// shield
		if (player.shield!=null){
			}
	}


	private BoundingBox.ColisionType checkCollision(){
		BoundingBox.ColisionType colisionType = BoundingBox.ColisionType.NONE;
		for(GameObject o : gameObjects){
			colisionType = player.getBoundingBox().checkColision(o.getBoundingBox());
			System.out.println(colisionType + " " + o.getX() + " " + o.getY());
		}
		 return colisionType;
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

			if (keyCode == KeyEvent.VK_ESCAPE) {
				dispose();
			}

			if (keyCode == KeyEvent.VK_LEFT) {
				player.move(-3, 0);
			}

			if (keyCode == KeyEvent.VK_RIGHT) {
				player.move(3, 0);
			}
			if (keyCode == KeyEvent.VK_UP) {
				player.move(0, -3);
			}
			if (keyCode == KeyEvent.VK_DOWN) {
				player.move(0, 3);
			}
			if (keyCode == KeyEvent.VK_E){
				try {
					System.out.println("Mana: "+player.mana);
					player.itemsList.get(selectedItem).use();
					bullets.addAll(player.bullets);
				} catch (IOException | NotEnoughManaExeption e) {
					notEnoughMana = true;
					player.playSound("assets/Sound/button-18.wav");
				}
            }
			if (keyCode == KeyEvent.VK_1) {
				selectedItem=0;
			}
			if (keyCode == KeyEvent.VK_2) {
				selectedItem=1;
			}
			if (keyCode == KeyEvent.VK_3) {
				selectedItem=2;
			}
			if (keyCode == KeyEvent.VK_4) {
				selectedItem=3;
			}
		}
	}

	public Player getPlayer() {
		return player;
	}
}
