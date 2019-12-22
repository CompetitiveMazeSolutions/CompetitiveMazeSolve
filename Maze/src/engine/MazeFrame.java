package engine;
//package FORKIDS;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MazeFrame extends JFrame implements ActionListener, Runnable {

	public static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;
	public static final int CPU = 1, P2 = 2, TT = 3;
	public static final int BORING = 0, BOXES = 1;

	public static int rows, cols; //20 and 35 best
	private int mode;
	private int aispeed; // Speed of bot
	private double startTime; // Time game is started
	private double mazeFidelity; // Called at the end of CarveStep
	private int stagePreset = BORING; // Not implemented
	private String matchName;// Will be added onto the fileOutput if saved

	private JPanel controls, maze;
	private JButton[] buttons = { new JButton("THIS ONE'S TRASH"), new JButton("I'M READY"), new JButton("SETTINGS"),
			new JButton("SAVE") };
	private MazeCell[][] cells;
	private MazeCell begi, end;
	private Stack<MazeCell> tex;
	private Stack<MazeCell> mex;
	private ReadyListener embededListener; // Put inside I'm Ready button, called on move
	private OverarchingListener frameListener;

	private boolean on;

	/*
	 * dumb private Color beg = new Color(1,1,1); private Color plead = new
	 * Color(255,255,255);
	 */
	/*
	 * snekl private Color beg = new Color(50,100,255); private Color plead = new
	 * Color(255,100,50);
	 */
	/*
	 * festivo private Color beg = new Color(200,30,10); private Color plead = new
	 * Color(10,200,30);
	 */
	/*
	 * ug private Color beg = new Color(50,20,0); private Color plead = new
	 * Color(0,20,50);
	 */
	/*
	 * BURN IN HECK private Color beg = new Color(100,0,0); private Color plead =
	 * new Color(255,255,50);
	 */

	/// *random
	private Color beg = new Color((int) (Math.random() * 256), (int) (Math.random() * 256),
			(int) (Math.random() * 256));
	private Color plead = new Color((int) (Math.random() * 256), (int) (Math.random() * 256),
			(int) (Math.random() * 256));
	// */

	private final Color p1 = beg;
	private final Color p2 = plead;

	/** Constructors **/

	public MazeFrame(int mode, double mazeFidelity, String matchName, int r, int c) {
		super("MAZE");

		this.matchName = matchName;
		this.mode = mode;
		this.mazeFidelity = mazeFidelity;
		rows = r;
		cols = c;
		
		aispeed = (int) (200 - (200 * (1 - mazeFidelity)));
		stagePreset = BORING;
		embededListener = new ReadyListener(this);
		frameListener = new OverarchingListener(this);

		if (matchName == null || matchName.equals("")) {
			this.matchName = "unnamed";
		}

		setUpControlPanel();// make the buttons & put them in the north
		instantiateCells();// give birth to all the mazeCells & get them onto the screen
		carveARandomMaze();// this will knock down walls to create a maze

		// finishing touches
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(rows * 40, cols * 40);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setBackground(Color.BLACK);
		setVisible(true);
		setFocusable(true);
		addKeyListener(frameListener);
		requestFocus();
	}

	public MazeFrame(int mode, double mazeFidelity, int aispeed, String matchName) {
		super("MAZE");

		this.matchName = matchName;
		this.mode = mode;
		this.mazeFidelity = mazeFidelity;
		this.aispeed = aispeed;
		stagePreset = BORING;
		embededListener = new ReadyListener(this);
		frameListener = new OverarchingListener(this);

		if (matchName == null || matchName.equals("")) {
			this.matchName = "unnamed";
		}

		setUpControlPanel();// make the buttons & put them in the north
		instantiateCells();// give birth to all the mazeCells & get them onto the screen
		carveARandomMaze();// this will knock down walls to create a maze

		// finishing touches
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(rows * 40, cols * 40);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setBackground(Color.BLACK);
		setVisible(true);
		setFocusable(true);
		addKeyListener(frameListener);
		requestFocus();
	}

	public MazeFrame(int mode, double mazeFidelity, int aispeed, int stagePreset, String matchName) {
		super("MAZE");

		this.matchName = matchName;
		this.mode = mode;
		this.mazeFidelity = mazeFidelity;
		this.aispeed = aispeed;
		this.stagePreset = stagePreset;
		embededListener = new ReadyListener(this);
		frameListener = new OverarchingListener(this);

		if (matchName == null || matchName.equals("")) {
			this.matchName = "unnamed";
		}

		setUpControlPanel();// make the buttons & put them in the north
		instantiateCells();// give birth to all the mazeCells & get them onto the screen
		carveARandomMaze();// this will knock down walls to create a maze

		// finishing touches
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(rows * 40, cols * 40);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setBackground(Color.BLACK);
		setVisible(true);
		setFocusable(true);
		addKeyListener(frameListener);
		requestFocus();
	}

	/** End Constructors **/

	/************ Setup Methods ************/

	// Makes the maze itself
	private void instantiateCells() {

		// Initialize new stacks
		tex = new Stack<MazeCell>();
		mex = new Stack<MazeCell>();

		// Initialize new maze panel
		maze = new JPanel();
		maze.setBackground(this.getBackground());
		maze.setLayout(new GridLayout(rows, cols));

		// Initialize cell array
		cells = new MazeCell[rows][cols];

		// Fill in the cell array
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				cells[i][j] = new MazeCell(i, j, mode);
				maze.add(cells[i][j]); // Add each cell to maze panel
			}
		}

		// Put the maze on the screen
		this.add(maze, BorderLayout.CENTER);
	}

	// Sets up bottom of screen
	private void setUpControlPanel() {

		// Create control panel
		controls = new JPanel();
		controls.setBackground(this.getBackground());

		// Initialize all buttons and add them to the panel
		for (JButton b : buttons) {
			b.addActionListener(this);
			controls.add(b);
		}

		// Add ready listener and set panel to bottom
		buttons[1].addKeyListener(embededListener);
		add(controls, BorderLayout.SOUTH);
	}

	// Called when a new maze is not carved
	private void carveARandomMaze() {

		// Pick beginning and end
		begi = cells[(int) (Math.random() * (rows * .5) + rows * .25)][0];
		end = cells[(int) (Math.random() * (rows * .5) + rows * .25)][cols - 1];
		// Sets starting state for carveAI
		begi.setStatus(MazeCell.VISITED);
		end.setStatus(MazeCell.BLANK);

		// Make beginning and end
		begi.clearWallDir(LEFT);
		end.clearWallDir(RIGHT);

		// Push beginning to bot and end to p2
		tex.push(begi);
		mex.push(end);

		// Set to be playered
		mex.peek().setPlayered(true);
		// Sets color and player of stack starts
		if (mode == TT || mode == P2) {
			mex.peek().setPly(2, p2);
			if(mode == P2)tex.peek().setPly(1, p1);

			// Clear out all side walls that need to be cleared
			for (int i = (int) (rows * .25); i < rows * .75; i++) {
				cells[i][0].clearWallDir(LEFT);
				cells[i][cols - 1].clearWallDir(RIGHT);
			}
		}

		// Carve the maze
		while (!tex.isEmpty())
			stepCarve();

		// Finish it off
		stepCarve();
		if(mode==TT)begi.setStatus(MazeCell.BLANK);
	}

	// Called by carveARandomMaze for each step
	private void stepCarve() {

		// If this is the first or last step
		if (tex.isEmpty()) {
			// Set all cells to be blanks
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < cols; j++)
					cells[i][j].setStatus(MazeCell.BLANK);
			// Push the first cell
			tex.push(begi);
			tex.peek().setStatus(MazeCell.VISITED);
			return;
		}
		// Create a new array of neighbors
		ArrayList<MazeCell> bop = blankNeighbors(tex.peek());
		// If no options
		if (bop.isEmpty()) {
			// Back up boi
			tex.pop().setStatus(MazeCell.DEAD);
		} else {
			// Pick a random choice
			MazeCell chosen1 = bop.get((int) (Math.random() * bop.size()));
			// Clear walls to new cell and from next space to this one
			tex.peek().clearWallDir(getDirectionFrom(tex.peek(), chosen1));
			chosen1.clearWallDir(getDirectionFrom(chosen1, tex.peek()));
			// Make the new cell visited
			chosen1.setStatus(MazeCell.VISITED);
			// Push it to carve stack
			tex.push(chosen1);
			// Randomly reset new space and make it reachable again
			if (Math.random() > mazeFidelity)
				tex.pop().setStatus(MazeCell.BLANK);
		}
	}

	/*
	 * private void carveALameMaze() {// "hard code" a maze
	 * cells[4][0].clearWallRight(); cells[4][0].clearWallLeft();
	 * cells[4][1].clearWallLeft(); cells[7][9].clearWallRight();
	 * cells[7][9].clearWallLeft(); cells[7][8].clearWallRight();
	 * cells[7][8].clearWallLeft(); cells[7][7].clearWallRight();
	 * cells[7][7].clearWallLeft(); cells[7][6].clearWallRight();
	 * cells[7][6].clearWallUp(); cells[6][6].clearWallDown();
	 * cells[6][6].clearWallUp(); cells[5][6].clearWallDown();
	 * cells[7][5].clearWallRight(); cells[7][6].clearWallLeft();
	 * cells[4][1].clearWallDown(); cells[5][1].clearWallUp();
	 * cells[5][1].clearWallRight(); cells[5][2].clearWallLeft();
	 * cells[5][2].clearWallRight(); cells[5][3].clearWallLeft();
	 * cells[4][3].clearWallDown(); cells[5][3].clearWallUp();
	 * cells[3][3].clearWallDown(); cells[4][3].clearWallUp();
	 * cells[2][3].clearWallDown(); cells[3][3].clearWallUp();
	 * cells[3][3].clearWallRight(); cells[3][4].clearWallLeft();
	 * cells[3][4].clearWallRight(); cells[3][5].clearWallLeft();
	 * cells[3][5].clearWallDown(); cells[4][5].clearWallUp();
	 * cells[4][5].clearWallRight(); cells[4][6].clearWallLeft();
	 * cells[4][7].clearWallDown(); cells[5][7].clearWallUp();
	 * cells[4][6].clearWallRight(); cells[4][7].clearWallLeft();
	 * cells[5][6].clearWallRight(); cells[5][7].clearWallLeft();
	 * cells[8][5].clearWallUp(); cells[7][5].clearWallDown();
	 * cells[6][2].clearWallUp(); cells[5][2].clearWallDown();
	 * cells[8][4].clearWallRight(); cells[8][5].clearWallLeft();
	 * tex.push(cells[4][0]); tex.peek().setStatus(MazeCell.VISITED); }
	 */

	// Takes the next step in solving the maze in CPU mode
	private boolean solveStep() {

		if (on) {
			// If player has reached the beginning
			if (mex.peek() == begi) {
				// Player wins
				win(2);
				return false;
			}

			// If bot is at the end
			if (isLast(tex.peek())) {
				// Bot wins
				win(0);
				return false;
			}

			// If neither win conditions are met, move on.
			for (int dir = 0; dir <= 3; dir++) { // for all directions

				// If cell is unblocked, existing, and unvisited
				if (getNeighbor(tex.peek(), getBestDir(tex.peek(), end)[dir]) != null
						&& !(tex.peek().isBlockedDir(getBestDir(tex.peek(), end)[dir]))
						&& getNeighbor(tex.peek(), getBestDir(tex.peek(), end)[dir]).getStatus() == MazeCell.BLANK) {
					// Add it to list of actions, and make it visited
					tex.push(getNeighbor(tex.peek(), getBestDir(tex.peek(), end)[dir]));
					tex.peek().setStatus(MazeCell.VISITED);
					return true;
				}
			}
			// If not able to move in any direction, move backwards
			tex.pop().setStatus(MazeCell.DEAD);
		}
		return true;
	}

	// Moves players in P2 mode
	public void playerMove(int player, int dir) {

		// Default stacks
		Stack<MazeCell> mex = this.tex;
		Stack<MazeCell> tex = this.mex;
		Color pCo = p1;

		// Skippable cells based on mode
		int skipO = 0;
		if(mode == P2) {
			skipO = player % 2 + 1;
		}else if(mode == TT){
			skipO = player;
		}

		// Set stacks to player stacks
		if (player == 2) {
			pCo = p2;
			mex = this.mex;
			tex = this.tex;
		}
		
		// Convenience Variables
		MazeCell nextOver = getNeighbor(mex.peek(),dir);

		if (nextOver != null && nextOver.getPly() == 0
				&& !mex.peek().isBlockedDir(dir)) { // into blank

			mex.peek().setStatus(mex.peek().getStatus());
			mex.push(nextOver);
			mex.peek().setPly(player, pCo);
			return;

		} else if (nextOver != null && nextOver.getPly() == player
				&& !mex.peek().isBlockedDir(dir)) { // into own

			MazeCell yes = nextOver;

			while (mex.peek() != yes) {
				mex.pop().setPly(0, null);
			}
			return;

		} else if (nextOver != null && !tex.isEmpty()
				&& nextOver == tex.peek()) { // into enemy head

			for (int i = 0; i < rows / 5; i++)
				if (!tex.isEmpty())
					tex.pop().setPly(0, null);
			return;

		} else if (nextOver != null && nextOver.getPly() == skipO
				&& getNeighbor(nextOver, dir) != null
				&& getNeighbor(nextOver, dir).getPly() == 0) { // able to skip over

			mex.peek().setStatus(mex.peek().getStatus());
			mex.push(getNeighbor(nextOver, dir));
			mex.peek().setPly(player, pCo);
			return;

		} else if (nextOver != null && nextOver.getPly() == skipO
				&& getNeighbor(nextOver, dir) != null
				&& getNeighbor(nextOver, dir).getPly() == player) { // skipping back

			MazeCell yes = getNeighbor(nextOver, dir);

			while (mex.peek() != yes) {
				mex.pop().setPly(0, null);
			}
			return;

		}
	}

	// playerMove case for bot mode
	public void playerMove(int dir) {
		if (getNeighbor(mex.peek(), dir) != null && !mex.peek().isBlockedDir(dir)) {
			mex.peek().setPHead(false);
			mex.push(getNeighbor(mex.peek(), dir));
			mex.peek().setPlayered(true);
		}
	}

	/**************** UTILITY METHODS ****************/

	// If the given cell qualifies for last move
	private boolean isLast(MazeCell luckyBoy) {
		return luckyBoy.col() == cols - 1 && !luckyBoy.isBlockedDir(RIGHT);
	}

	// If the possible cell is valid
	private boolean isInBounds(int r, int c) {
		return r >= 0 && r < rows && c >= 0 && c < cols;
	}

	// Returns an array of directional neighbor cells to the given cells
	private ArrayList<MazeCell> blankNeighbors(MazeCell mc) {
		ArrayList<MazeCell> results = new ArrayList<MazeCell>();
		// For each direction
		for (int i = 0; i < 4; i++) {
			MazeCell inQuestion = getNeighbor(mc, i);
			// If it is real, and blank
			if (inQuestion != null && inQuestion.getStatus() == MazeCell.BLANK)
				results.add(inQuestion);
		}
		return results;
	}

	// Returns the closest MazeCell in direction
	public MazeCell getNeighbor(MazeCell mc, int dir) {
		// Where moving to
		switch (dir) {
		// If exists, return cell in direction
		case LEFT:
			if (isInBounds(mc.row(), mc.col() - 1))
				return cells[mc.row()][mc.col() - 1];
			else
				return null;
		case RIGHT:
			if (isInBounds(mc.row(), mc.col() + 1))
				return cells[mc.row()][mc.col() + 1];
			else
				return null;
		case DOWN:
			if (isInBounds(mc.row() + 1, mc.col()))
				return cells[mc.row() + 1][mc.col()];
			else
				return null;
		case UP:
			if (isInBounds(mc.row() - 1, mc.col()))
				return cells[mc.row() - 1][mc.col()];
			else
				return null;
		default:
			return null;
		}
	}

	// Returns a set of directions in order of importance
	private int[] getBestDir(MazeCell orig, MazeCell dest) {
		// Initialize new moveset
		int[] moves = new int[4];
		int yDis = dest.row() - orig.row();
		int xDis = dest.col() - orig.col();
		// If closer in y than in x
		if (Math.abs(xDis) <= Math.abs(yDis)) {
			// Decide which direction is best in order
			if (yDis <= 0) {
				moves[0] = UP;
				moves[2] = DOWN;
			} else {
				moves[0] = DOWN;
				moves[2] = UP;
			}
			if (xDis <= 0) {
				moves[1] = RIGHT;
				moves[3] = LEFT;
			} else {
				moves[1] = RIGHT;
				moves[3] = LEFT;
			}
		} else {
			if (xDis <= 0) {
				moves[0] = RIGHT;
				moves[3] = LEFT;
			} else {
				moves[0] = RIGHT;
				moves[3] = LEFT;
			}
			if (yDis <= 0) {
				moves[1] = UP;
				moves[2] = DOWN;
			} else {
				moves[1] = DOWN;
				moves[2] = UP;
			}
		}
		return moves;
	}

	// Returns a direction from one point to the other
	private int getDirectionFrom(MazeCell orig, MazeCell dest) {
		// Default case is nonexistent direction
		int ret = -1;
		// Else, find the best direction to dest
		if (dest.row() < orig.row())
			ret = MazeCell.UP;
		if (dest.row() > orig.row())
			ret = MazeCell.DOWN;
		if (dest.col() > orig.col())
			ret = MazeCell.RIGHT;
		if (dest.col() < orig.col())
			ret = MazeCell.LEFT;
		return ret;
	}

	// Called when bot thread is started
	public void run() {
		while (solveStep())
			pause(aispeed);
	}

	// Pauses this thread
	public void pause(int t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
		}
	}

	// Creates a new settings pane
	public void openSettings() {
		Settings settingWindow = new Settings(this);
		this.add(settingWindow);
		maze.setVisible(false);
		controls.setVisible(false);
		pack();
	}

	// Fully resets this maze's frame
	public void resetMaze() {
		new MazeFrame(mode, mazeFidelity, null, rows, cols);

		try {
			Thread.sleep(50 + (rows * cols) / 1000);
		} catch (Exception e) {
		}

		setVisible(false);
	}

	// Saves the current maze image
	// Took me a while to figure out robot :(
	public void saveMaze() {
		BufferedImage imagebuf = null; // New blank image
		try {
			// Robot just scans the screen for each pixel
			imagebuf = new Robot().createScreenCapture(maze.getBounds());
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		// New graphics to catch image trace
		Graphics2D graphics2D = imagebuf.createGraphics();
		maze.paint(graphics2D);
		try {
			// Writes the image onto a new file
			File outputFile = new File("src/output/");
			// If the directory does not exist, create it
			if (!outputFile.exists()) {
				System.out.println("creating directory: " + outputFile.getName());
				boolean result = false;

				try {
					outputFile.mkdir();
					result = true;
				} catch (SecurityException se) {
					// handle it
				}
				if (result) {
					System.out.println("DIR created");
				}
				outputFile = new File("src/output/" + matchName + ".JPEG");
			}
			// If it does exist, then find make the new file name have a different number
			if (outputFile.exists()) { // If the file exists
				int currentInstance = 0;
				String currentName = outputFile.getName();
				if (currentName.contains("(")) { // If it has an identifier
					String newInstance = currentName.substring(currentName.indexOf("(") + 1);
					currentInstance = Integer.parseInt(newInstance);
				}
				// Create a new output file
				if (((int) currentInstance + 1) == 0) {
					outputFile = new File("src/output/" + matchName + ".JPEG");
				} else {
					outputFile = new File("src/output/" + matchName + "(" + (((int) currentInstance) + 1) + ").JPEG");
				}
			}

			// Write it to disk
			ImageIO.write(imagebuf, "jpeg", outputFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Called at the end of the game, ends game
	public void win(int player) {

		// Turn off maze actions
		on = false;

		if (player == 0) {
			// Some gradient setup
			double i = 0;
			double t = 0;
			int sizzle = tex.size();
			// While we still have stuff to gradient
			while (!tex.isEmpty()) {
				// Set gradient factor
				t = i / sizzle;
				i++;
				// Set the gradient based on gradient factor
				int gradVal = (int) (255 * (1 - t));
				tex.pop().setGrad(new Color(gradVal, gradVal, gradVal));
			}
			// Find match time
			int matchTime = (int) (((int) (System.currentTimeMillis()) - startTime));
			// Display match time
			JOptionPane.showMessageDialog(this, (double)matchTime/1000 + " seconds");
		} else if (player == 1) {
			// Some gradient setup
			double i = 0;
			double t = 0;
			int sizzle = tex.size();
			// Random color to gradient to
			Color badiddle = new Color((int) (Math.random() * 256), (int) (Math.random() * 256),
					(int) (Math.random() * 256));
			// While we still have stuff to gradient
			while (!tex.isEmpty()) {
				// Set gradient factor
				t = i / sizzle;
				i++;
				// Set player and color based on gradient factor
				tex.pop().setPly(1,
						new Color((int) (beg.getRed() * t + badiddle.getRed() * (1 - t)),
								(int) (beg.getGreen() * t + badiddle.getGreen() * (1 - t)),
								(int) (beg.getBlue() * t + badiddle.getBlue() * (1 - t))));
			}
			// Display match time
			int matchTime = (int) (((int) (System.currentTimeMillis()) - startTime));
			JOptionPane.showMessageDialog(this, (double)matchTime/1000 + " seconds");
			return;
		} else if (player == 2) {
			// Some gradient setup
			double i = 0;
			double t = 0;
			int sizzle = mex.size();
			// Random color to gradient to
			Color badiddle = new Color((int) (Math.random() * 256), (int) (Math.random() * 256),
					(int) (Math.random() * 256));
			// While we still have stuff to gradient
			while (!mex.isEmpty()) {
				// Set gradient factor
				t = i / sizzle;
				i++;
				// Set player and color based on gradient factor
				mex.pop().setPly(2,
						new Color((int) (plead.getRed() * t + badiddle.getRed() * (1 - t)),
								(int) (plead.getGreen() * t + badiddle.getGreen() * (1 - t)),
								(int) (plead.getBlue() * t + badiddle.getBlue() * (1 - t))));
			}
			// Display match time
			int matchTime = (int) (((int) (System.currentTimeMillis()) - startTime));
			JOptionPane.showMessageDialog(this, (double)matchTime/1000 + " seconds");
			return;
		}
	}

	/***********************************************/

	// Called any time that you press a button
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttons[0]) {
			resetMaze();
		}

		if (e.getSource() == buttons[1]) {
			for (MazeCell[] out : cells)
				for (MazeCell in : out)
					in.go();
			on = true;
			startTime = (int) (System.currentTimeMillis());
			if (mode == CPU)
				(new Thread(this)).start();
			return;
		}

		if (e.getSource() == buttons[2]) {
			openSettings();
			return;
		}

		if (e.getSource() == buttons[3]) {
			saveMaze();
			return;
		}
	}// end action performed

	/**************************/
	/* ACCESSORS AND MUTATORS */
	/**************************/

	public JPanel getGameWindow() {
		return maze;
	}

	public JButton getSolve() {
		return buttons[1];
	}

	public Stack<MazeCell> getMex() {
		return mex;
	}

	public Stack<MazeCell> getTex() {
		return tex;
	}

	public MazeCell[][] getCells() {
		return cells;

	}

	public Runnable getRun() {
		return this;
	}

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

}// end class
