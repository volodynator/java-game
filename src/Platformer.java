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
import java.util.*;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Platformer extends JFrame{
	@Serial
	private static final long serialVersionUID = 5736902251450559962L;
	Timer timer;
	private Level l = null;
	Player player;
	BufferStrategy bufferStrategy;
	private boolean running = false;
	

	private List<Bullet> bullets = new ArrayList<>();
	private boolean notEnoughMana = false;

	private int selectedItem = 0;
	public List<Tile> levelObjects = new ArrayList<>();

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
			player.playSound(".\\assets\\Sound\\soundtrack.wav");
			createBufferStrategy(2);
			bufferStrategy = this.getBufferStrategy();

			this.getGraphics().setColor(Color.RED);
			this.setBounds(0, 0, 1000, 10 * 70);
			this.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}

		levelObjects.addAll((Collection<? extends Tile>) l.levelObjects);
		startTimer();
	}


	private void startTimer() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// Call update and repaint periodically
				l.update();
				player.update();
				checkCollision();
				updateGameStateAndRepaint();

			}
		}, 0, 10); // Schedule the task to run every 10 ms
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

//		System.out.println("Player : "+player.x);
		int maxOffsetX = l.getResultingImage().getWidth(this) - this.getWidth();

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
		g2d.drawImage(player.getImage(), player.x, player.y, this);

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
			g2d.setColor(Color.BLUE);
			int playerCenterX = player.x + player.getImage().getWidth() / 2;
			int playerCenterY = player.y + player.getImage().getHeight() / 2;
			g2d.drawOval(playerCenterX - 150, playerCenterY-150, 300, 300);
		}
	}


	private void checkCollision(){
		BoundingBox.ColisionType colisionType = BoundingBox.ColisionType.NONE;

		for(Tile tile : levelObjects){
			switch(tile.tileIndex){
				case 0: {
					colisionType = player.getBoundingBox().checkColision(tile.getBoundingBox());
					switch(colisionType){
						case DOWN -> {
							player.inAir = false;
							return;
						}

					}
				}
//				case 1: {
//					colisionType = player.getBoundingBox().checkColision(tile.getBoundingBox());
//					if(colisionType != BoundingBox.ColisionType.NONE){
//						System.out.println("restart");
//						player.restart();
//					}
//				}
			}
		}

		player.inAir = true;
		if (player.y > l.getHeight()) {
			player.restart();
		}

	}
	@Override
	public void dispose() {
		// Stop the timer when the window is closed
		if (timer != null) {
			timer.cancel();
		}
		super.dispose();
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
				player.move("left");
			}

			if (keyCode == KeyEvent.VK_RIGHT) {
				player.move("right");
			}
			if (keyCode == KeyEvent.VK_SPACE) {
				player.jump();
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
