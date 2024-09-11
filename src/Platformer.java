import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serial;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Platformer extends JFrame {
	@Serial
	private static final long serialVersionUID = 5736902251450559962L;

	BufferedImage levelImg;
	int viewX = 0;
	int viewY = 0;
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
		FileFilter filter = new FileNameExtensionFilter("Level image (.bmp)", "bmp");
		fc.setFileFilter(filter);
		int result = fc.showOpenDialog(this);
		File selectedFile = new File("");

		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fc.getSelectedFile();
			System.out.println("Selected file: " + selectedFile.getAbsolutePath());
		} else {
			dispose();
			System.exit(0);
		}

		try {
			Level level = new Level(selectedFile);
			levelImg = level.getLevel();
			addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode()==KeyEvent.VK_LEFT){
						if (viewX>=0){
							viewX-=50;
							repaint();
						}
					}
					if (e.getKeyCode()==KeyEvent.VK_RIGHT){
						if (viewX<levelImg.getWidth()){
							viewX+=50;
							repaint();
						}
					}
				}
			});

			this.setBounds(0, 0, 1000, 350);
			this.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g.drawImage(levelImg, 0, 0, 1000, 350, viewX, viewY, viewX + 1000, viewY + 350, null);

	}
}
