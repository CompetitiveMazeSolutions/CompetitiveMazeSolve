package engine;
//package FORKIDS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MazeFrame extends JFrame implements ActionListener, Runnable {

	public static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;
	public static int ROWS = 20, COLS = 35;
	public static final int CPU = 1, P2 = 2;
	public static final int BORING = 0, BOXES = 1;

	private int mode;
	private int aispeed; // Speed of bot
	private int startTime; // Time game is started
	private double mazeFidelity; // Called at the end of CarveStep
	private int stagePreset = BORING; // Not implemented

	private JPanel controls, maze;
	private JButton[] buttons = { new JButton("THIS ONE'S TRASH"), new JButton("I'M READY"), new JButton("SETTINGS") };
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

	public MazeFrame(int mode, double mazeFidelity) {
		super("MAZE");

		this.mode = mode;
		this.mazeFidelity = mazeFidelity;
		aispeed = (int) (200 - (200 * (1 - mazeFidelity)));
		stagePreset = BORING;
		embededListener = new ReadyListener(this);
		frameListener = new OverarchingListener(this);

		setUpControlPanel();// make the buttons & put them in the north
		instantiateCells();// give birth to all the mazeCells & get them onto the screen
		carveARandomMaze();// this will knock down walls to create a maze

		// finishing touches
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(ROWS * 40, COLS * 40);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setBackground(Color.BLACK);
		setVisible(true);
		setFocusable(true);
		addKeyListener(frameListener);
		requestFocus();
	}

	public MazeFrame(int mode, double mazeFidelity, int aispeed) {
		super("MAZE");

		this.mode = mode;
		this.mazeFidelity = mazeFidelity;
		this.aispeed = aispeed;
		stagePreset = BORING;
		embededListener = new ReadyListener(this);
		frameListener = new OverarchingListener(this);

		setUpControlPanel();// make the buttons & put them in the north
		instantiateCells();// give birth to all the mazeCells & get them onto the screen
		carveARandomMaze();// this will knock down walls to create a maze

		// finishing touches
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(ROWS * 40, COLS * 40);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setBackground(Color.BLACK);
		setVisible(true);
		setFocusable(true);
		addKeyListener(frameListener);
		requestFocus();
	}

	public MazeFrame(int mode, double mazeFidelity, int aispeed, int stagePreset) {
		super("MAZE");

		this.mode = mode;
		this.mazeFidelity = mazeFidelity;
		this.aispeed = aispeed;
		this.stagePreset = stagePreset;
		embededListener = new ReadyListener(this);
		frameListener = new OverarchingListener(this);

		setUpControlPanel();// make the buttons & put them in the north
		instantiateCells();// give birth to all the mazeCells & get them onto the screen
		carveARandomMaze();// this will knock down walls to create a maze

		// finishing touches
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(ROWS * 40, COLS * 40);
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
		maze.setLayout(new GridLayout(ROWS, COLS));

		// Initialize cell array
		cells = new MazeCell[ROWS][COLS];

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
		begi = cells[(int) (Math.random() * (ROWS * .5) + ROWS * .25)][0];
		end = cells[(int) (Math.random() * (ROWS * .5) + ROWS * .25)][COLS - 1];

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
		if (mode == P2) {
			tex.peek().setPly(1, p1);
			mex.peek().setPly(2, p2);

			// Clear out all side walls that need to be cleared
			for (int i = (int) (ROWS * .25); i < ROWS * .75; i++) {
				cells[i][0].clearWallDir(LEFT);
				cells[i][COLS - 1].clearWallDir(RIGHT);
			}
		}

		// Carve the maze
		while (!tex.isEmpty())
			stepCarve();

		// Finish it off
		stepCarve();
	}

	// Called by carveARandomMaze for each step
	private void stepCarve() {

		// If this is the first or last step
		if (tex.isEmpty()) {
			// Set all cells to be blanks
			for (int i = 0; i < ROWS; i++)
				for (int j = 0; j < COLS; j++)
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

			// If bot is at the end
			if (isLast(tex.peek())) {
				// Bot wins
				win(0);
				return false;
			}

			// If player has reached the beginning
			if (mex.peek() == begi) {
				// Player wins
				win(2);
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

		// Set stacks to player stacks
		if (player == 2) {
			pCo = p2;
			mex = this.mex;
			tex = this.tex;
		}

		if (getNeighbor(mex.peek(), dir) != null && getNeighbor(mex.peek(), dir).getPly() == 0
				&& !mex.peek().isBlockedDir(dir)) { // into blank

			mex.peek().setStatus(mex.peek().getStatus());
			mex.push(getNeighbor(mex.peek(), dir));
			mex.peek().setPly(player, pCo);
			return;

		} else if (getNeighbor(mex.peek(), dir) != null && getNeighbor(mex.peek(), dir).getPly() == player
				&& !mex.peek().isBlockedDir(dir)) { // into own

			MazeCell yes = getNeighbor(mex.peek(), dir);

			while (mex.peek() != yes) {
				mex.pop().setPly(0, null);
			}
			return;

		} else if (getNeighbor(mex.peek(), dir) != null && !tex.isEmpty()
				&& getNeighbor(mex.peek(), dir) == tex.peek()) { // into enemy head

			for (int i = 0; i < ROWS / 5; i++)
				if (!tex.isEmpty())
					tex.pop().setPly(0, null);
			return;

		} else if (getNeighbor(mex.peek(), dir) != null && getNeighbor(mex.peek(), dir).getPly() == player % 2 + 1
				&& getNeighbor(getNeighbor(mex.peek(), dir), dir) != null
				&& getNeighbor(getNeighbor(mex.peek(), dir), dir).getPly() == 0) { // able to skip over

			mex.peek().setStatus(mex.peek().getStatus());
			mex.push(getNeighbor(getNeighbor(mex.peek(), dir), dir));
			mex.peek().setPly(player, pCo);
			return;

		} else if (getNeighbor(mex.peek(), dir) != null && getNeighbor(mex.peek(), dir).getPly() == player % 2 + 1
				&& getNeighbor(getNeighbor(mex.peek(), dir), dir) != null
				&& getNeighbor(getNeighbor(mex.peek(), dir), dir).getPly() == player) { // skipping back

			MazeCell yes = getNeighbor(getNeighbor(mex.peek(), dir), dir);

			while (mex.peek() != yes) {
				mex.pop().setPly(0, null);
			}
			return;

		}
	}

	// playerMove case for bot mode
	public void playerMove(int dir) {
		if (getNeighbor(mex.peek(), dir) != null && !mex.peek().isBlockedDir(dir)) {
			mex.push(getNeighbor(mex.peek(), dir));
			mex.peek().setPlayered(true);
		}
	}

	/**************** UTILITY METHODS ****************/

	// If the given cell qualifies for last move
	private boolean isLast(MazeCell luckyBoy) {
		return luckyBoy.col() == COLS - 1 && !luckyBoy.isBlockedDir(RIGHT);
	}

	// If the possible cell is valid
	private boolean isInBounds(int r, int c) {
		return r >= 0 && r < ROWS && c >= 0 && c < COLS;
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
		new MazeFrame(mode, mazeFidelity);

		try {
			Thread.sleep(50 + (ROWS * COLS) / 1000);
		} catch (Exception e) {
		}

		setVisible(false);
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
			int matchTime = (int) (((int) (System.currentTimeMillis()) - startTime) / 1000);
			// Display match time
			JOptionPane.showMessageDialog(this, matchTime + " second match");
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
			int matchTime = (int) (((int) (System.currentTimeMillis()) - startTime) / 1000);
			JOptionPane.showMessageDialog(this, matchTime);
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
			int matchTime = (int) (((int) (System.currentTimeMillis()) - startTime) / 1000);
			JOptionPane.showMessageDialog(this, matchTime);
			return;
		}
	}

	/***********************************************/

	// Called any time that you press a button
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttons[0]) {
			setVisible(false);
			new MazeFrame(mode, mazeFidelity);
			return;
		}

		if (e.getSource() == buttons[1]) {
			for (MazeCell[] out : cells)
				for (MazeCell in : out)
					in.go();
			startTime = (int) (System.currentTimeMillis());
			if (mode == CPU)
				(new Thread(this)).start();
			return;
		}

		if (e.getSource() == buttons[2]) {
			openSettings();
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

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

}// end class
