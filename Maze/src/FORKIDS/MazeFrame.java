package FORKIDS;
//package FORKIDS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MazeFrame extends JFrame implements ActionListener, Runnable, KeyListener {
	private static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;
	private static int ROWS = 20, COLS = 35;

	private static final int CPU = 1, P2 = 2;
	private int mode;
	private int aispeed;
	private int startTime;
	private double mazeFidelity;
	private int stagePreset;
	private static final int BORING = 0, BOXES = 1;

	private JPanel controls, maze;
	private JButton solve, hic, carb;
	private MazeCell[][] cells;
	private CellStack tex;
	private CellStack mex;
	KeyListener l = new KeyListener() {

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_SHIFT:
				if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
					p1R = true;
				} else {
					p2R = true;
				}
				break;
			case KeyEvent.VK_CONTROL:
				if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
					p1C = true;
				} else {
					p2C = true;
				}
				break;
			}
			if (p1C && p2C) {
				resetMaze();
			}
			if (p1R && p2R) {
				solve.requestFocus();
				for (MazeCell[] out : cells)
					for (MazeCell in : out)
						in.go();
				startTime = (int) (System.currentTimeMillis());
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_SHIFT:
				if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
					p1R = false;
				} else {
					p2R = false;
				}
				break;
			case KeyEvent.VK_CONTROL:
				if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
					p1C = false;
				} else {
					p2C = false;
				}
				break;
			}
		}
	};

	private boolean on;
	private boolean p1C, p2C;
	public boolean p1R, p2R;

	private MazeCell begi, end;
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

	/** Constructor **/
	public MazeFrame() {
		super("MAZE");

		setUpControlPanel();// make the buttons & put them in the north
		instantiateCells();// give birth to all the mazeCells & get them onto the screen
		carveARandomMaze();// this will knock down walls to create a maze

		// finishing touches
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(ROWS * 40, COLS * 40);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setBackground(Color.BLACK);
		setVisible(true);
		addKeyListener(l);
		requestFocus();
	}// end constructor

	/* 1111111111111111 PHASE 1 STUFF 1111111111111111111111 */
	private void instantiateCells() {

		stagePreset = BORING;
		mode = P2;
		on = true;
		mazeFidelity = .7;
		aispeed = (int) (200 - (200 * (1 - mazeFidelity)));

		/*
		 * p1t = false; p2t = false; p1b = false; p2b = false;
		 */

		p1C = false;
		p2C = false;

		p1R = false;
		p2R = false;

		tex = new CellStack();
		mex = new CellStack();
		maze = new JPanel();
		maze.setBackground(this.getBackground());
		maze.setLayout(new GridLayout(ROWS, COLS));

		cells = new MazeCell[ROWS][COLS];
		// construct your 2D Array & instantiate EACH MazeCell
		// be sure to add each MazeCell to the panel
		// * call maze.add( ?the cell ? );
		/** ~~~~ WRITE CODE HERE ~~~~ **/
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				cells[i][j] = new MazeCell(i, j, mode);
				maze.add(cells[i][j]);
			}
		}

		/** ~~~~ *************** ~~~~ **/
		// put the maze on the screen
		this.add(maze, BorderLayout.CENTER);
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

	/** 2222222222222222222 PHASE 2 STUFF 22222222222222222222222222 **/
	public boolean isInBounds(int r, int c) {
		return r >= 0 && r < ROWS && c >= 0 && c < COLS;
	}

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
			if (on && getNeighbor(tex.peek(), getBetDir(tex.peek(), end)[dir]) != null
					&& !(tex.peek().isBlockedDir(getBetDir(tex.peek(), end)[dir]))
					&& getNeighbor(tex.peek(), getBetDir(tex.peek(), end)[dir]).getStatus() == MazeCell.BLANK) { // if
																													// cell
																													// is
																													// unvisited
																													// and
																													// real
																													// and
																													// not
																													// blocked
				tex.push(getNeighbor(tex.peek(), getBetDir(tex.peek(), end)[dir])); // add it to list of actions, make
																					// it visited
				tex.peek().setStatus(MazeCell.VISITED);
				return true;
			}
		}
		if (on)
			tex.pop().setStatus(MazeCell.DEAD); // if not able to move, kill
		return on;
	}

	public void pause(int t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
		}
	}

	private boolean isLast(MazeCell luckyBoy) {
		return luckyBoy.col() == COLS - 1 && !luckyBoy.isBlockedRight();
	}

	/* 33333333333333333333 Phase 3 stuff 3333333333333333333333333 */

	private MazeCell getNeighbor(MazeCell mc, int dir) {
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

	private ArrayList<MazeCell> blankNeighbors(MazeCell mc) {
		ArrayList<MazeCell> results = new ArrayList<MazeCell>();
		for (int i = 0; i < 4; i++)
			if (getNeighbor(mc, i) != null && getNeighbor(mc, i).getStatus() == MazeCell.BLANK)
				results.add(getNeighbor(mc, i));
		return results;
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

	// 4444444444444444444 PHASE 4 STUFF 4444444444444444444444444444
	private void win(int player) {
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

	private void playerMove(int player, int dir) {

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

	private void playerMove(int dir) {
		if (getNeighbor(mex.peek(), dir) != null && !mex.peek().isBlockedDir(dir)) {
			mex.peek().setStatus(mex.peek().getStatus());
			mex.push(getNeighbor(mex.peek(), dir));
			mex.peek().setPStat(true);
		}
	}

	public void resetMaze() {
		this.setVisible(false);
		new MazeFrame();
	}

	public void run() {
		while (solveStep())
			pause(aispeed);
	}

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
			new MazeFrame();

		}
		if (e.getSource() == hic) {
			openSettings();
		}
	}// end action performed

	public void setUpControlPanel() {
		controls = new JPanel();
		controls.setBackground(this.getBackground());
		carb = new JButton("THIS ONE'S TRASH");
		hic = new JButton("I'M HERE TOO");
		solve = new JButton("I'M READY");
		carb.addActionListener(this);
		hic.addActionListener(this);
		solve.addActionListener(this);
		solve.addKeyListener(this);
		controls.add(carb);
		controls.add(hic);
		controls.add(solve);

		this.add(controls, BorderLayout.NORTH);
	}

	public static void main(String[] args) {
		new MazeFrame();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!on && e.getKeyCode() != (KeyEvent.VK_CONTROL))
			return;
		if (mode == CPU) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				playerMove(RIGHT);
				break;
			case KeyEvent.VK_LEFT:
				playerMove(LEFT);
				break;
			case KeyEvent.VK_DOWN:
				playerMove(DOWN);
				break;
			case KeyEvent.VK_UP:
				playerMove(UP);
				break;
			}
		} else {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				playerMove(2, RIGHT);
				break;
			case KeyEvent.VK_LEFT:
				if (mex.peek().col() == 0 && !mex.peek().isBlockedDir(LEFT)) {
					win(2);
					break;
				}
				if (mex.peek().col() == 1 && getNeighbor(mex.peek(), LEFT).getPly() == 1) {
					win(2);
					break;
				}
				playerMove(2, LEFT);
				break;
			case KeyEvent.VK_DOWN:
				playerMove(2, DOWN);
				/*
				 * if (mex.peek().row() == ROWS - 1) p2b = true; if (p2t && p2b) { for (int i =
				 * 0; i < ROWS / 5; i++) mex.pop().setPly(0, null); p2b = false; }
				 */
				break;
			case KeyEvent.VK_UP:
				playerMove(2, UP);
				/*
				 * if (mex.peek().row() == 0) p2t = true; if (p2t && p2b) { for (int i = 0; i <
				 * ROWS / 5; i++) mex.pop().setPly(0, null); p2t = false; }
				 */
				break;
			case KeyEvent.VK_D:
				if (tex.peek().col() == COLS - 1 && !tex.peek().isBlockedDir(RIGHT)) {
					win(1);
					break;
				}
				if (tex.peek().col() == COLS - 2 && getNeighbor(tex.peek(), RIGHT).getPly() == 2) {
					win(1);
					break;
				}
				playerMove(1, RIGHT);
				break;
			case KeyEvent.VK_A:
				playerMove(1, LEFT);
				break;
			case KeyEvent.VK_S:
				playerMove(1, DOWN);
				/*
				 * if (tex.peek().row() == ROWS - 1) p1b = true; if (p1t && p1b) { for (int i =
				 * 0; i < ROWS / 5; i++) tex.pop().setPly(0, null); p1b = false; }
				 */
				break;
			case KeyEvent.VK_W:
				playerMove(1, UP);
				/*
				 * if (tex.peek().row() == 0) p1t = true; if (p1t && p1b) { for (int i = 0; i <
				 * ROWS / 5; i++) tex.pop().setPly(0, null); p1t = false;
				 *
				 * }
				 */
				break;
			case KeyEvent.VK_CONTROL:
				if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
					p1C = true;
				} else {
					p2C = true;
				}
				break;
			}
			if (p1C && p2C) {
				resetMaze();
			}
		}

	}

	public void openSettings() {
		Settings settingWindow = new Settings(this);
		this.add(settingWindow);
		maze.setVisible(false);
		controls.setVisible(false);
		pack();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_CONTROL:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
				p1C = false;
			} else {
				p2C = false;
			}
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public JPanel getGameWindow() {
		return maze;
	}

}// end class
