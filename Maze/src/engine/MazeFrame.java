package engine;
//package FORKIDS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
	private int aispeed;
	private int startTime;
	private double mazeFidelity;
	private int stagePreset = BORING;

	private JPanel controls, maze;
	private JButton solve, hic, carb;
	private MazeCell[][] cells;
	private MazeCell begi, end;
	private CellStack tex;
	private CellStack mex;

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

		stagePreset = BORING;
		this.mode = mode;
		this.mazeFidelity = mazeFidelity;
		aispeed = (int) (200 - (200 * (1 - mazeFidelity)));

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
		addKeyListener(new OverarchingListener(this));
		requestFocus();
	}

	public MazeFrame(int mode, double mazeFidelity, int aispeed) {
		super("MAZE");

		stagePreset = BORING;
		this.mode = mode;
		this.mazeFidelity = mazeFidelity;
		this.aispeed = aispeed;

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
		addKeyListener(new OverarchingListener(this));
		requestFocus();
	}

	/** End Constructors **/

	/************ Setup Methods ************/
	private void instantiateCells() {

		tex = new CellStack();
		mex = new CellStack();
		maze = new JPanel();
		maze.setBackground(this.getBackground());
		maze.setLayout(new GridLayout(ROWS, COLS));

		cells = new MazeCell[ROWS][COLS];

		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				cells[i][j] = new MazeCell(i, j, mode);
				maze.add(cells[i][j]);
			}
		}

		// put the maze on the screen
		this.add(maze, BorderLayout.CENTER);
	}

	private void setUpControlPanel() {
		controls = new JPanel();
		controls.setBackground(this.getBackground());
		carb = new JButton("THIS ONE'S TRASH");
		hic = new JButton("I'M HERE TOO");
		solve = new JButton("I'M READY");
		carb.addActionListener(this);
		hic.addActionListener(this);
		solve.addActionListener(this);
		solve.addKeyListener(new ReadyListener(this));
		controls.add(carb);
		controls.add(hic);
		controls.add(solve);

		this.add(controls, BorderLayout.NORTH);
	}

	private void carveARandomMaze() {
		/*
		 * if(!tex.isEmpty()){ tex.peek().setStatus(MazeCell.BLANK);
		 * tex.peek().setPly(0, null); tex.peek().setPly(0, null); tex.pop(); }
		 * if(!mex.isEmpty()){ mex.peek().setPStat(false); mex.peek().setPly(0, null);
		 * mex.peek().setPly(0, null); mex.pop(); }
		 */
		/*
		 * for(int i=0; i<ROWS; i++){ cells[i][0].setStatus(MazeCell.DEAD);
		 * cells[i][COLS-1].setStatus(MazeCell.DEAD); }
		 */
		begi = cells[(int) (Math.random() * (ROWS * .5) + ROWS * .25)][0];
		end = cells[(int) (Math.random() * (ROWS * .5) + ROWS * .25)][COLS - 1];
		begi.setStatus(MazeCell.VISITED);
		end.setStatus(MazeCell.BLANK);
		begi.clearWallLeft();
		end.clearWallRight();
		tex.push(begi);
		mex.push(end);
		mex.peek().setPStat(true);
		if (mode == P2) {
			tex.peek().setPly(1, p1);
			mex.peek().setPly(2, p2);

			for (int i = (int) (ROWS * .25); i < ROWS * .75; i++) {
				cells[i][0].clearWallLeft();
				cells[i][COLS - 1].clearWallRight();
			}
		}
		while (!tex.isEmpty())
			stepCarve();
		stepCarve();
	}

	private void stepCarve() {

		if (tex.isEmpty()) {
			for (int i = 0; i < ROWS; i++)
				for (int j = 0; j < COLS; j++)
					cells[i][j].setStatus(MazeCell.BLANK);
			tex.push(begi);
			tex.peek().setStatus(MazeCell.VISITED);
			return;
		}
		ArrayList<MazeCell> bop = blankNeighbors(tex.peek());
		if (bop.isEmpty()) {
			tex.pop().setStatus(MazeCell.DEAD);
		} else {
			MazeCell chosen1 = bop.get((int) (Math.random() * bop.size()));
			tex.peek().clearWallDir(getDirectionFrom(tex.peek(), chosen1));
			chosen1.clearWallDir(getDirectionFrom(chosen1, tex.peek()));
			chosen1.setStatus(MazeCell.VISITED);
			tex.push(chosen1);
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

	private boolean solveStep() {// takes the next step in solving the maze

		if (on && isLast(tex.peek())) {
			double i = 0;
			double t = 0;
			int sizzle = tex.size();
			while (!tex.isEmpty() && on) {
				t = i / sizzle;
				i++;
				tex.pop()
						.setGrad(new Color((int) (Color.BLACK.getRed() * t + Color.WHITE.getRed() * (1 - t)),
								(int) (Color.BLACK.getGreen() * t + Color.WHITE.getGreen() * (1 - t)),
								(int) (Color.BLACK.getBlue() * t + Color.WHITE.getBlue() * (1 - t))));
			}
			int matchTime = (int) (((int) (System.currentTimeMillis()) - startTime) / 1000);
			JOptionPane.showMessageDialog(this, matchTime);
			on = false;
			return true;
		}

		if ((tex.isEmpty() || (!mex.isEmpty() && mex.peek() == begi)) && on) {
			double i = 0;
			double t = 0;
			int sizzle = mex.size();
			while (!mex.isEmpty()) {
				t = i / sizzle;
				i++;
				mex.pop()
						.setGrad(new Color((int) (beg.getRed() * t + plead.getRed() * (1 - t)),
								(int) (beg.getGreen() * t + plead.getGreen() * (1 - t)),
								(int) (beg.getBlue() * t + plead.getBlue() * (1 - t))));

			}
			int matchTime = (int) (((int) (System.currentTimeMillis()) - startTime) / 1000);
			JOptionPane.showMessageDialog(this, matchTime);
			on = false;
			return false;
		}

		for (int dir = 0; dir <= 3; dir++) { // for all directions

			// if cell is unblocked, existing, and unvisited
			if (on && getNeighbor(tex.peek(), getBetDir(tex.peek(), end)[dir]) != null
					&& !(tex.peek().isBlockedDir(getBetDir(tex.peek(), end)[dir]))
					&& getNeighbor(tex.peek(), getBetDir(tex.peek(), end)[dir]).getStatus() == MazeCell.BLANK) {
				// add it to list of actions, make
				// it visited
				tex.push(getNeighbor(tex.peek(), getBetDir(tex.peek(), end)[dir]));
				tex.peek().setStatus(MazeCell.VISITED);
				return true;
			}
		}
		if (on)
			tex.pop().setStatus(MazeCell.DEAD); // if not able to move, kill
		return on;
	}

	public void playerMove(int player, int dir) {

		CellStack mex = this.mex;
		CellStack tex = this.tex;
		Color pCo = Color.WHITE;
		if (player == 1) {
			pCo = p1;
		} else if (player == 2) {
			pCo = p2;
		}

		if (player == 1) {
			mex = this.tex;
			tex = this.mex;
		}

		if (getNeighbor(mex.peek(), dir) != null && getNeighbor(mex.peek(), dir).getPly() == 0
				&& !mex.peek().isBlockedDir(dir)) { // into blank
			mex.peek().setStatus(mex.peek().getStatus());
			mex.push(getNeighbor(mex.peek(), dir));
			mex.peek().setPly(player, pCo);
		} else if (getNeighbor(mex.peek(), dir) != null && getNeighbor(mex.peek(), dir).getPly() == player
				&& !mex.peek().isBlockedDir(dir)) { // into own
			MazeCell yes = getNeighbor(mex.peek(), dir);
			while (mex.peek() != yes) {
				mex.pop().setPly(0, null);
			}
		} else if (getNeighbor(mex.peek(), dir) != null && !tex.isEmpty()
				&& getNeighbor(mex.peek(), dir) == tex.peek()) { // into enemy head
			for (int i = 0; i < ROWS / 5; i++)
				if (!tex.isEmpty())
					tex.pop().setPly(0, null);
		} else if (getNeighbor(mex.peek(), dir) != null && getNeighbor(mex.peek(), dir).getPly() == player % 2 + 1
				&& getNeighbor(getNeighbor(mex.peek(), dir), dir) != null
				&& getNeighbor(getNeighbor(mex.peek(), dir), dir).getPly() == 0) { // able to skip over
			mex.peek().setStatus(mex.peek().getStatus());
			mex.push(getNeighbor(getNeighbor(mex.peek(), dir), dir));
			mex.peek().setPly(player, pCo);
		} else if (getNeighbor(mex.peek(), dir) != null && getNeighbor(mex.peek(), dir).getPly() == player % 2 + 1
				&& getNeighbor(getNeighbor(mex.peek(), dir), dir) != null
				&& getNeighbor(getNeighbor(mex.peek(), dir), dir).getPly() == player) { // skipping back
			MazeCell yes = getNeighbor(getNeighbor(mex.peek(), dir), dir);
			while (mex.peek() != yes) {
				mex.pop().setPly(0, null);
			}
		}
	}

	public void playerMove(int dir) {
		if (getNeighbor(mex.peek(), dir) != null && !mex.peek().isBlockedDir(dir)) {
			mex.peek().setStatus(mex.peek().getStatus());
			mex.push(getNeighbor(mex.peek(), dir));
			mex.peek().setPStat(true);
		}
	}

	/**************** UTILITY METHODS ****************/

	private boolean isLast(MazeCell luckyBoy) {
		return luckyBoy.col() == COLS - 1 && !luckyBoy.isBlockedRight();
	}

	public boolean isInBounds(int r, int c) {
		return r >= 0 && r < ROWS && c >= 0 && c < COLS;
	}

	private ArrayList<MazeCell> blankNeighbors(MazeCell mc) {
		ArrayList<MazeCell> results = new ArrayList<MazeCell>();
		for (int i = 0; i < 4; i++)
			if (getNeighbor(mc, i) != null && getNeighbor(mc, i).getStatus() == MazeCell.BLANK)
				results.add(getNeighbor(mc, i));
		return results;
	}

	public MazeCell getNeighbor(MazeCell mc, int dir) {
		switch (dir) { // where moving to
		case LEFT:
			if (isInBounds(mc.row(), mc.col() - 1))
				return cells[mc.row()][mc.col() - 1];
			else
				return null; // if exists, return cell in direction
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

	private int[] getBetDir(MazeCell orig, MazeCell dest) {
		int[] moves = new int[4];
		int yDis = dest.row() - orig.row();
		int xDis = dest.col() - orig.col();
		if (Math.abs(xDis) <= Math.abs(yDis)) {
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

	private int getDirectionFrom(MazeCell orig, MazeCell dest) {
		int ret = -1;
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

	public void run() {
		while (solveStep())
			pause(aispeed);
	}

	public void pause(int t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
		}
	}

	public void openSettings() {
		Settings settingWindow = new Settings(this);
		this.add(settingWindow);
		maze.setVisible(false);
		controls.setVisible(false);
		pack();
	}

	public void resetMaze() {
		new MazeFrame(mode, mazeFidelity);

		try {
			Thread.sleep(50 + (ROWS * COLS) / 1000);
		} catch (Exception e) {
		}

		setVisible(false);
	}

	public void win(int player) {

		on = false;

		if (player == 1) {
			double i = 0;
			double t = 0;
			int sizzle = tex.size();
			Color badiddle = new Color((int) (Math.random() * 256), (int) (Math.random() * 256),
					(int) (Math.random() * 256));
			while (!tex.isEmpty()) {
				t = i / sizzle;
				i++;
				tex.pop().setPly(1,
						new Color((int) (beg.getRed() * t + badiddle.getRed() * (1 - t)),
								(int) (beg.getGreen() * t + badiddle.getGreen() * (1 - t)),
								(int) (beg.getBlue() * t + badiddle.getBlue() * (1 - t))));
			}
			int matchTime = (int) (((int) (System.currentTimeMillis()) - startTime) / 1000);
			JOptionPane.showMessageDialog(this, matchTime);
		} else {
			double i = 0;
			double t = 0;
			int sizzle = mex.size();
			Color badiddle = new Color((int) (Math.random() * 256), (int) (Math.random() * 256),
					(int) (Math.random() * 256));
			while (!mex.isEmpty()) {
				t = i / sizzle;
				i++;
				mex.pop().setPly(2,
						new Color((int) (plead.getRed() * t + badiddle.getRed() * (1 - t)),
								(int) (plead.getGreen() * t + badiddle.getGreen() * (1 - t)),
								(int) (plead.getBlue() * t + badiddle.getBlue() * (1 - t))));
			}
			int matchTime = (int) (((int) (System.currentTimeMillis()) - startTime) / 1000);
			JOptionPane.showMessageDialog(this, matchTime);
		}
	}

	/***********************************************/

	// This gets called any time that you press a button
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == solve) {
			for (MazeCell[] out : cells)
				for (MazeCell in : out)
					in.go();
			startTime = (int) (System.currentTimeMillis());
			if (mode == CPU)
				(new Thread(this)).start();
		}

		if (e.getSource() == carb) {
			this.setVisible(false);
			new MazeFrame(mode, mazeFidelity);

		}
		if (e.getSource() == hic) {
			openSettings();
		}
	}// end action performed

	/**************************/
	/* ACCESSORS AND MUTATORS */
	/**************************/

	public JPanel getGameWindow() {
		return maze;
	}

	public JButton getSolve() {
		return solve;
	}

	public CellStack getMex() {
		return mex;
	}

	public CellStack getTex() {
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
