import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.SwingConstants;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.awt.event.ActionListener;

/**
 * 
 * @author nickxzs23
 * @version 1.0.0
 * @date June 19, 2016
 *
 */

// Implementation of Conway's Game of Life in Java using the Swing library


// TODO
//	1)	I have no idea why but the dimensions are all screwed up
//		when I created the start array I had to add 3 to the # of columns idk why
//
//	2)	Display what generation it is on the actual window ------------
//
//	3)	Add pause button ------------
//
//	4) 	Add speed slider
//
//	5) 	Add mechanism to draw and remove pixels -----------------
//
//	6) 	Add keylistener to exit window ------------------------------------
//
//	7) 	Maybe later try to add scrolling
//
//	8) 	Add support for other 2d automata
//
//	9)	Add grid using JLayeredPanel

@SuppressWarnings("serial")
public class GameOfLife extends JFrame implements MouseListener{

	private JLabel lblNewLabel;
	private JPanel panel;

	private static int CellHeight = 10;
	private static int CellWidth = 10;

	private boolean paused;
	private boolean isOver;
	private static Thread gameLoop;

	private static int[][] newGen;


	public GameOfLife() {

		this.setTitle("Conway's Game of Life");

		addMouseListener(this);
		setResizable(true);

		paused = false;
		isOver = false;

		// Poll IO events
		addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					System.exit(JFrame.EXIT_ON_CLOSE);
				}
				else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					paused = !paused;
				}
			}

		});

		setMinimumSize(new Dimension(640, 640));
		setSize(new Dimension(640, 640));

		// JLabel displaying Generation number
		lblNewLabel = new JLabel("HELLO HELLO HELLO HELLO HELLO");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblNewLabel, BorderLayout.SOUTH);

		// JPanel displaying graphics
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(640, 640));
		panel.setMinimumSize(new Dimension(640, 640));
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLocation(new Point(100, 100));

	}

	/**
	 * Returns next generation based on Conway's Game of Life
	 * @param gen Current Generation
	 * @return int[][] with next generation of Conway's Game of Life
	 */
	public static int[][] life2d(int[][] gen) {

		newGen = new int[gen.length][gen[0].length];

		int height = gen.length - 1;
		int width = gen[0].length - 1;

		for (int r = 1; r < height; r++) {
			for (int c = 1; c < width; c++) {
				int count = getNeighbors(r, c, gen);

				// cell is alive
				if (gen[r][c] == 1) {
					// Death
					if (count < 2 || count > 3) {
						newGen[r][c] = 0;
					}
					// Survival
					else {
						newGen[r][c] = 1;
					}
				}
				// cell is dead
				else {
					// Rebirth
					if (count == 3) {
						newGen[r][c] = 1;
					}
				}

			}
		}

		return newGen;
	}

	/**
	 * Returns number of the 8 neighbor cells excluding the tile itself
	 * which are alive
	 * @param r Row in generation of current cell
	 * @param c Column in generation of current cell
	 * @param gen Current generation
	 * @return number of live neighbor cells
	 */
	public static int getNeighbors(int r, int c, int[][] gen) {
		int count = 0;

		if (gen[r-1][c-1] == 1) count++;
		if (gen[r-1][c] == 1) count++;
		if (gen[r-1][c+1] == 1) count++;

		if (gen[r][c-1] == 1) count++;
		if (gen[r][c+1] == 1) count++;

		if (gen[r+1][c-1] == 1) count++;
		if (gen[r+1][c] == 1) count++;
		if (gen[r+1][c+1] == 1) count++;

		return count;
	}

	/**
	 * Draws the current generation. A yellow cell is alive and a dark gray cell is dead.
	 * @param arr int[][] which holds generation to be drawn
	 * @param g Graphics component
	 */
	public static void DrawGeneration(int[][] arr, Graphics g) {
		for (int r = 0; r < arr.length; r++) {
			for (int c = 0; c < arr[0].length; c++) {

				g.setColor( arr[r][c] == 1 ? Color.YELLOW : Color.DARK_GRAY );

//				g.drawRect((c-1)*CellHeight, (r-1)*CellWidth, CellWidth, CellHeight);
				g.fillRect((c)*CellHeight, (r)*CellWidth, CellWidth, CellHeight);

			}
		}
	}
	
	/**
	 * Draws grid around cells so they're more easily visible.
	 */
	public static void DrawGrid(Graphics g) {
		for (int r = 0; r < 640; r++) {
			if (r % GameOfLife.CellHeight == 0) {
				g.setColor(Color.WHITE);
				g.drawLine(0, r, 640, r);
			}
		}
		
		for (int c = 0; c < 640; c++) {
			if (c % GameOfLife.CellWidth == 0) {
				g.setColor(Color.WHITE);
				g.drawLine(c, 0, c, 640);
			}
		}
	}

	/**
	 * Sets generation label
	 * @param text
	 */
	public void setGenLabel(String text) {
		this.lblNewLabel.setText(text);

	}

	public void mouseClicked(MouseEvent e) {

	}


	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * Creates a live cell where the mouse was clicked. Mouse coordinates set the origin at
	 * the top left of the screen. For some reason, I had to decrease the y coordinate by 3
	 * to get it to align with the actual cursor.
	 */
	public void mouseReleased(MouseEvent e) {
		if (paused == true) {
			int x = e.getX() / GameOfLife.CellWidth;
			int y = e.getY() / GameOfLife.CellHeight - 3;
			
			// Draw over cell
			Graphics g = this.panel.getGraphics();
			g.setColor(newGen[y][x] == 0 ? Color.YELLOW : Color.DARK_GRAY);
			
			g.fillRect((e.getX())/10*10, (e.getY() - 25)/10*10, CellWidth, CellHeight);
			
			System.out.println("x: " + x);
			System.out.println("y: " + y);

			// Change original newGen array
			newGen[y][x] = newGen[y][x] == 0 ? 1 : 0;

			System.out.println("Mouse clicked");
		}

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) throws InvocationTargetException, InterruptedException {
		/*
		while (frame.isOver == false) {

			if (frame.paused == false) {

				frame.setGenLabel("GENERATION: " + gen);

				Thread.sleep(50);

				DrawGeneration(start, g);
				start = life2d(start);
				
				gen++;

			}

		}*/

		/**
		 * Create the Game Loop
		 */
		gameLoop = new Thread(() -> {
			GameOfLife frame = new GameOfLife();
			frame.setVisible(true);

			int[][] arr = new int[frame.getHeight() / GameOfLife.CellHeight][frame.getWidth() / GameOfLife.CellWidth];
			arr[6][2] = 1;
			arr[6][3] = 1;
			arr[7][2] = 1;
			arr[7][3] = 1;

			arr[4][14] = 1;
			arr[4][15] = 1;
			arr[5][13] = 1;
			arr[5][17] = 1;
			arr[6][12] = 1;
			arr[6][18] = 1;
			arr[7][12] = 1;
			arr[7][16] = 1;
			arr[7][18] = 1;
			arr[7][19] = 1;
			arr[8][12] = 1;
			arr[8][18] = 1;
			arr[9][13] = 1;
			arr[9][17] = 1;
			arr[10][14] = 1;
			arr[10][15] = 1;

			arr[4][22] = 1;
			arr[4][23] = 1;
			arr[5][22] = 1;
			arr[5][23] = 1;
			arr[6][22] = 1;
			arr[6][23] = 1;
			arr[3][24] = 1;
			arr[7][24] = 1;
			arr[2][26] = 1;
			arr[3][26] = 1;
			arr[7][26] = 1;
			arr[8][26] = 1;

			arr[4][36] = 1;
			arr[4][37] = 1;
			arr[5][36] = 1;
			arr[5][37] = 1;

			Graphics g = frame.panel.getGraphics();	

			int gen = 0;

			while (frame.isOver == false) {
				frame.setGenLabel("GENERATION: " + gen);

				DrawGeneration(arr, g);
				arr = life2d(arr);
				try {
				    Thread.sleep(50);
				}
				catch (InterruptedException ex) {
					
				}

				while(frame.paused == true) {
					DrawGeneration(arr, g);
					try {
				    	Thread.sleep(50);
					}
					catch (InterruptedException ex) {

					}
				}
				gen++;
			}
		});

		gameLoop.start();



	}

}
