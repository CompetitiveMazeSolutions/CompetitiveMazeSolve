//package Phase1;
package engine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JPanel;

public class MazeCell extends JPanel {
	public static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;
	public static final int BLANK = 0, VISITED = 1, DEAD = 2, PATH = 3; // For statuses
	public static final int p1 = 1, p2 = 2;

	private int wallThickness = 2; // Width of wall brush
	private int padding = 2; // Amount of space to the next cell
	private int row, col; // Location of cell
	private int ply; // Player
	private int status; // Determines the color and accessibility
	private int mode; // Mode of MazeFrame (previously needPly)
	private Stroke str; // Brush for walls
	private Color lineColor = Color.WHITE; // Color of walls
	private Color textColor = Color.WHITE.darker(); // Color of inside text
	private Color defaultBG = Color.GRAY; // Background of cell
	private Color plyCo; // Color of player
	private Color[] colors = { defaultBG, Color.BLUE, Color.BLACK, Color.CYAN }; // Color of each status
	private Color[] colorsP = { Color.YELLOW, Color.GREEN, Color.YELLOW.darker(), Color.ORANGE }; // Colors w/ player
	private boolean[] borders = { true, true, true, true }; // Border for each side
	private boolean pHead; // If the cell is the head of the player
	private boolean hide; // If the cell is hidden
	private boolean playered; // Status of a player being in it

	public MazeCell(int r, int c, int mode) {
		super();
		str = new BasicStroke(wallThickness); // Had a stroke reading this
		row = r;
		col = c;
		status = BLANK;
		playered = false;
		this.mode = mode;
		hide = true;

		setBackground(defaultBG);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g); // Makes the maze draw this cell onto it

		Color c;
		if (mode == MazeFrame.CPU) {
			if (!playered) { // look I know it's not a real word, ok?
				c = new Color(colors[status].getRed(), colors[status].getGreen(), colors[status].getBlue(), 150);
			} else {
				c = new Color(colorsP[status].getRed(), colorsP[status].getGreen(), colorsP[status].getBlue(), 150);
			}
			g.setColor(c);
			if (pHead) { // If this is the head of the player in bot mode
				c = colorsP[PATH];
				pHead = false;
				g.setColor(c);
			}
		} else if (ply != 0) { // If not bot mode, and a player exists
			c = new Color(plyCo.getRed(), plyCo.getGreen(), plyCo.getBlue(), 200);
			g.setColor(c);
		}

		int roll = (int) (Math.random() * 35) + 130; // Roll for how dark the cell is
		if (status == BLANK && !(playered) && ply == 0) {
			if (hide) {
				g.setColor(Color.WHITE);
			} else {
				g.setColor(new Color(roll, roll, roll));
			}
		}

		/**** DRAW ALL OF IT BASED ON THE COLORS ABOVE ****/
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		g.setColor(lineColor);
		((Graphics2D) g).setStroke(str);
		if (borders[UP])
			g.drawLine(0, 0, this.getWidth(), 0);
		if (borders[RIGHT])
			g.drawLine(this.getWidth() - padding, 0, this.getWidth() - padding, this.getHeight());
		if (borders[DOWN])
			g.drawLine(0, this.getHeight() - padding, this.getWidth(), this.getHeight() - padding);
		if (borders[LEFT])
			g.drawLine(0, 0, 0, this.getHeight());

		g.setColor(textColor);
		g.drawString(this.toString(), 5, 15);
	}

	// Returns row of cell
	public int row() {
		return row;
	}

	// Returns column of cell
	public int col() {
		return col;
	}

	// Sets status(color) of the cell
	public void setStatus(int x) {
		status = x;
		repaint();
	}

	// Sets status of being playered
	public void setPlayered(boolean trü) {
		if (!pHead && trü)
			pHead = true;
		playered = trü;
		repaint();
	}

	public boolean isPlayered() {
		return playered;
	}

	public void setPHead(boolean pHead) {
		this.pHead = pHead;
		repaint();
	}

	// Sets player and player color
	public void setPly(int x, Color colour) {
		ply = x;
		plyCo = colour;
		repaint();
	}

	// Gets the current player
	public int getPly() {
		return ply;
	}

	// Sets the gradient color
	public void setGrad(Color beep) {
		status = PATH;
		colors[status] = beep;
		colorsP[status] = beep;
		repaint();
	}

	// Called when the game opens
	public void go() {
		hide = false;
		paint(this.getGraphics());
	}

	public boolean isBlank() {
		return status == BLANK;
	}

	public boolean isVisited() {
		return status == VISITED;
	}

	public boolean isDead() {
		return status == DEAD;
	}

	public int getStatus() {
		return status;
	}

	public boolean isBlockedDir(int dir) {
		return borders[dir];
	}

	public void clearWallDir(int dir) {
		borders[dir] = false;
		repaint();
	}

	public String toString() {
		char yees = (char) ((int) (Math.random() * 94) + 27);
		return "" + yees;
	}

}
