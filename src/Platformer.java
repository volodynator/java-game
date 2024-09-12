import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.util.*;

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
			createBufferStrategy(2);
			bufferStrategy = this.getBufferStrategy();

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
	}


	private void updateGameStateAndRepaint() {
		l.update();
		checkCollision();
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
		int maxOffsetX = l.getResultingImage().getWidth(null) - this.getWidth();

		l.offsetX = Math.max(0, Math.min(playerCenterX - this.getWidth() / 2, maxOffsetX));
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) bufferStrategy.getDrawGraphics();
		try {
			draw(g2);
		}finally {
			g2.dispose();
		}
		bufferStrategy.show();
	}

	private void draw(Graphics2D g2d) {
		BufferedImage img_level = (BufferedImage) l.getResultingImage();
		BufferedImage visibleLevel =
				img_level.getSubimage((int) l.offsetX, 0, 1000, l.getHeight());
		g2d.drawImage(visibleLevel, 0, 0, this);
		g2d.drawImage(player.getImage(), player.x - (int) l.offsetX, player.y, this);

		for (Bullet bullet: bullets) {
			g2d.drawImage(bullet.image, bullet.x, bullet.y, this);
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
					bullets.add(player.weapon.use());
					player.playSound(".\\assets\\Sound\\gun-gunshot-01.wav");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public Player getPlayer() {
		return player;
	}
}
