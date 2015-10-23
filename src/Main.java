import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends Canvas implements MouseListener, KeyListener {
	private boolean logicRequiredThisLoop;
	private boolean gameRunning = true;
	private boolean startAnim = false;
	private static JPanel panel;
	private int cellSize = 5;
	private int gameSpeed = 200;
	private byte[][] cells = new byte[800 / cellSize][600 / cellSize];
	private byte[][] nextGen = new byte[800 / cellSize][600 / cellSize];
	private int generations = 0;

	public Main() {
		addMouseListener(this);
		addKeyListener(this);
		JFrame container = new JFrame("Conway's GOL");
		panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(800, 600));
		panel.setLayout(null);
		setBounds(0, 0, 800, 600);
		panel.add(this);
		setIgnoreRepaint(true);
		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		createBufferStrategy(2);
	}

	public void updateLogic() {
		logicRequiredThisLoop = true;
	}

	public void gameLoop() {
		while (gameRunning) {
			nextGen = new byte[800 / cellSize][600 / cellSize];
			
			if(gameSpeed < 0)
				gameSpeed = 0;
			
			if (startAnim) {
				for (int x = 0; x < 800 / cellSize; x++) {
					for (int y = 0; y < 600 / cellSize; y++) {
						nextGen[x][y] = evaluateCell(x, y, 800 / cellSize, 600 / cellSize);
					}
				}

				generations++;
				cells = nextGen;
			}

			Graphics2D g = (Graphics2D) getBufferStrategy().getDrawGraphics();

			g.setColor(Color.white);
			g.fillRect(0, 0, 800, 600);
			g.setColor(Color.gray);
			for (int x = 0; x < 800 / cellSize; x++)
				g.drawLine(x * cellSize, 0, x * cellSize, 600);
			for (int y = 0; y < 600 / cellSize; y++)
				g.drawLine(0, y * cellSize, 800, y * cellSize);

			g.setColor(Color.black);
			for (int x = 0; x < 800 / cellSize; x++) {
				for (int y = 0; y < 600 / cellSize; y++) {
					if (cells[x][y] == 1) {
						g.fillRect(x * cellSize + 1, y * cellSize + 1, cellSize - 1, cellSize - 1);
					}
				}
			}
			
			g.setFont(new Font("Calibri", Font.BOLD, 15)); 
			g.setColor(Color.blue);
			g.drawString("Delay per gen (ms): " + gameSpeed, 10, 20);
			g.drawString("Running: " + startAnim, 10, 40);
			g.drawString("Generation: " + generations, 10, 60);

			getBufferStrategy().show();
			g.dispose();

			try {
				Thread.sleep(startAnim ? gameSpeed : 50);
			} catch (Exception e) {
			}
		}
	}

	public byte evaluateCell(int x, int y, int width, int height) {
		boolean alive = cells[x][y] == 1;

		byte top = y == height - 1 ? 0 : cells[x][y + 1];
		byte bottom = y == 0 ? 0 : cells[x][y - 1];
		byte right = x == width - 1 ? 0 : cells[x + 1][y];
		byte left = x == 0 ? 0 : cells[x - 1][y];
		byte topLeft = (y == height - 1 || x == 0) ? 0 : cells[x - 1][y + 1];
		byte topRight = (y == height - 1 || x == width - 1) ? 0 : cells[x + 1][y + 1];
		byte bottomLeft = (y == 0 || x == 0) ? 0 : cells[x - 1][y - 1];
		byte bottomRight = (y == 0 || x == width - 1) ? 0 : cells[x + 1][y - 1];
		int totalNeighbours = right + left + top + bottom + topLeft + topRight + bottomLeft + bottomRight;

		if (alive) {
			if (totalNeighbours < 2)
				return 0;
			else if (totalNeighbours > 3)
				return 0;
			else if (totalNeighbours == 2 || totalNeighbours == 3)
				return 1;
		} else {
			if (totalNeighbours == 3)
				return 1;
			else
				return 0;
		}

		return 0;
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == 1) {
			int x = e.getX() / cellSize;
			int y = e.getY() / cellSize;
			cells[x][y] = (byte) (cells[x][y] == 1 ? 0 : 1);
		}
	}

	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == ' ')
			startAnim = !startAnim;
		else if (e.getKeyChar() == 'q' && gameSpeed > 0)
			gameSpeed -= 10;
		else if (e.getKeyChar() == 'w')
			gameSpeed += 10;
	}

	public static void main(String argv[]) {
		Main g = new Main();
		g.gameLoop();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void keyPressed(KeyEvent arg0) {
	}

	public void keyReleased(KeyEvent e) {
	}
}