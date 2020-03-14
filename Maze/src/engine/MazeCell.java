//package Phase1;
package engine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JPanel;
import consts.*;

public class MazeCell extends JPanel {
	 // For statuses in bot mode
	public static final Color lineColor = Color.WHITE; // Color of walls
	public static final Color textColor = Color.WHITE.darker(); // Color of inside text
	public static final Color defaultBG = Color.GRAY; // Background of cell

	private int wallThickness = 2; // Width of wall brush
	private int padding = 2; // Amount of space to the next cell
	private int row, col; // Location of cell
	private Player ply; // Player
	private Stroke str; // Brush for walls
	private Color plyCo; // Color of player
	private boolean[] borders = { true, true, true, true }; // Border for each side
	protected boolean hide; // If the cell is hidden

	private char yees = (char) ((int) (Math.random() * 94) + 27);//symbol
	
	public MazeCell(int r, int c) {
		super();
		str = new BasicStroke(wallThickness); // Had a stroke reading this
		row = r;
		col = c;
		hide = true;

		setBackground(defaultBG);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g); // Makes the maze draw this cell onto it

		g.setColor(computeHue());

		/**** DRAW ALL OF IT BASED ON THE COLORS ABOVE ****/
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		g.setColor(lineColor);
		((Graphics2D) g).setStroke(str);
		if (borders[Direction.UP.ordinal()])
			g.drawLine(0, 0, this.getWidth(), 0);
		if (borders[Direction.RIGHT.ordinal()])
			g.drawLine(this.getWidth() - padding, 0, this.getWidth() - padding, this.getHeight());
		if (borders[Direction.DOWN.ordinal()])
			g.drawLine(0, this.getHeight() - padding, this.getWidth(), this.getHeight() - padding);
		if (borders[Direction.LEFT.ordinal()])
			g.drawLine(0, 0, 0, this.getHeight());

		g.setColor(textColor);
		g.drawString(this.toString(), 5, 15);
	}
	
	protected Color computeHue() {
		if (ply != null) // If  player exists
			return new Color(plyCo.getRed(), plyCo.getGreen(), plyCo.getBlue(), 200);
		
		int roll = (int) (Math.random() * 35) + 130; // Roll for how dark the cell is
		return (hide) ? Color.WHITE : new Color(roll, roll, roll);
	}

	// Returns row of cell
	public int row() {
		return row;
	}

	// Returns column of cell
	public int col() {
		return col;
	}

	// Sets player and player color
	public void setPly(Player x, Color colour) {
		ply = x;
		plyCo = colour;
		repaint();
	}

	// Gets the current player
	public Player getPly() {
		return ply;
	}

	// Called when the game opens
	public void go() {
		hide = false;
		paint(this.getGraphics());
	}

	public boolean isBlockedDir(Direction dir) {
		return borders[dir.ordinal()];
	}

	public void clearWallDir(Direction dir) {
		borders[dir.ordinal()] = false;
		repaint();
	}

	public void blockWallDir(Direction dir) {
		borders[dir.ordinal()] = true;
		repaint();
	}

	public String toString() {
		return "" + yees;
	}

}
