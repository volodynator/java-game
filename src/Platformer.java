import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serial;

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
			startGame();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		int playerCenterX = player.x + player.getImage().getWidth() / 2;
		System.out.println("Off: "+l.offsetX);
		System.out.println("Player : "+player.x);
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
		}
	}

	public Player getPlayer() {
		return player;
	}
}
