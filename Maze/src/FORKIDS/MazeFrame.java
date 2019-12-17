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
	private static int ROWS = 50, COLS = 75;

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

	private boolean φρ;
	private boolean p1t, p2t, p1b, p2b;

	private MazeCell begi, end;
	// *** you will need a 2DArray of MazeCells****
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
		// carveALameMaze();//this will knock down walls to create a maze
		carveARandomMaze();

		// finishing touches
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(ROWS * 40, COLS * 40);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);
	}// end constructor

	/* 1111111111111111 PHASE 1 STUFF 1111111111111111111111 */
	private void instantiateCells() {

		mode = CPU;
		stagePreset = BOXES;
		mode = P2;
		φρ = true;
		mazeFidelity = .85;
		aispeed = (int) (250 - (250 * (1 - mazeFidelity)));
		mazeFidelity = .8;
		aispeed = (int) (250 - (250 * (1 - mazeFidelity)));

		p1t = false;
		p2t = false;
		p1b = false;
		p2b = false;

		tex = new CellStack();
		mex = new CellStack();
		maze = new JPanel();
		maze.setBackground(Color.WHITE);
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

	private void carveALameMaze() {// "hard code" a maze
		cells[4][0].clearWallRight();
		cells[4][0].clearWallLeft();
		cells[4][1].clearWallLeft();
		cells[7][9].clearWallRight();
		cells[7][9].clearWallLeft();
		cells[7][8].clearWallRight();
		cells[7][8].clearWallLeft();
		cells[7][7].clearWallRight();
		cells[7][7].clearWallLeft();
		cells[7][6].clearWallRight();
		cells[7][6].clearWallUp();
		cells[6][6].clearWallDown();
		cells[6][6].clearWallUp();
		cells[5][6].clearWallDown();
		cells[7][5].clearWallRight();
		cells[7][6].clearWallLeft();
		cells[4][1].clearWallDown();
		cells[5][1].clearWallUp();
		cells[5][1].clearWallRight();
		cells[5][2].clearWallLeft();
		cells[5][2].clearWallRight();
		cells[5][3].clearWallLeft();
		cells[4][3].clearWallDown();
		cells[5][3].clearWallUp();
		cells[3][3].clearWallDown();
		cells[4][3].clearWallUp();
		cells[2][3].clearWallDown();
		cells[3][3].clearWallUp();
		cells[3][3].clearWallRight();
		cells[3][4].clearWallLeft();
		cells[3][4].clearWallRight();
		cells[3][5].clearWallLeft();
		cells[3][5].clearWallDown();
		cells[4][5].clearWallUp();
		cells[4][5].clearWallRight();
		cells[4][6].clearWallLeft();
		cells[4][7].clearWallDown();
		cells[5][7].clearWallUp();
		cells[4][6].clearWallRight();
		cells[4][7].clearWallLeft();
		cells[5][6].clearWallRight();
		cells[5][7].clearWallLeft();
		cells[8][5].clearWallUp();
		cells[7][5].clearWallDown();
		cells[6][2].clearWallUp();
		cells[5][2].clearWallDown();
		cells[8][4].clearWallRight();
		cells[8][5].clearWallLeft();
		tex.push(cells[4][0]);
		tex.peek().setStatus(MazeCell.VISITED);
	}

	/** 2222222222222222222 PHASE 2 STUFF 22222222222222222222222222 **/
	public boolean isInBounds(int r, int c) {
		return r >= 0 && r < ROWS && c >= 0 && c < COLS;
	}

	public void run() {
		while (solveStep())
			pause(aispeed);
	}

	public boolean solveStep() {// takes the next step in solving the maze
		if (isLast(tex.peek())) {
			double i = 0;
			double t = 0;
			int sizzle = tex.size();
			while (!tex.isEmpty() && φρ) {
				t = i / sizzle;
				i++;
				tex.pop()
						.setGrad(new Color((int) (Color.BLACK.getRed() * t + Color.WHITE.getRed() * (1 - t)),
								(int) (Color.BLACK.getGreen() * t + Color.WHITE.getGreen() * (1 - t)),
								(int) (Color.BLACK.getBlue() * t + Color.WHITE.getBlue() * (1 - t))));
			}
			int matchTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
			JOptionPane.showMessageDialog(this, matchTime);
			φρ = false;
			return true;
		}
		if ((tex.isEmpty() || (!mex.isEmpty() && mex.peek() == begi)) && φρ) {
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
			int matchTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
			JOptionPane.showMessageDialog(this, matchTime);
			φρ = false;
			return false;
		}

		for (int dir = 0; dir <= 3; dir++) { // for all directions
			if (getNeighbor(tex.peek(), getBetDir(tex.peek(), end)[dir]) != null
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
		tex.pop().setStatus(MazeCell.DEAD); // if not able to move, kill
		return true;
	}

	public void pause(int t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
		}
	}

	public boolean isLast(MazeCell luckyBoy) {
		return luckyBoy.col() == COLS - 1 && !luckyBoy.isBlockedRight();
	}

	/* 33333333333333333333 Phase 3 stuff 3333333333333333333333333 */

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

	public ArrayList<MazeCell> blankNeighbors(MazeCell mc) {
		ArrayList<MazeCell> results = new ArrayList<MazeCell>();
		for (int i = 0; i < 4; i++)
			if (getNeighbor(mc, i) != null && getNeighbor(mc, i).getStatus() == MazeCell.BLANK)
				results.add(getNeighbor(mc, i));
		return results;
	}

	public int getDirectionFrom(MazeCell orig, MazeCell dest) {
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

	public int[] getBetDir(MazeCell orig, MazeCell dest) {
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

	public void stepCarve() {
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

	public void carveARandomMaze() {
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
		begi = cells[(int) (Math.random() * ROWS)][0];
		end = cells[(int) (Math.random() * ROWS)][COLS - 1];
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

			for (int i = 0; i < ROWS; i++) {
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
		φρ = false;
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
			int matchTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
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
			int matchTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
			JOptionPane.showMessageDialog(this, matchTime);
		}
	}

	// This gets called any time that you press a button
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == solve) {
			startTime = (int) (System.currentTimeMillis() / 1000);
			if (mode == CPU)
				(new Thread(this)).start();
		}

		if (e.getSource() == carb) {
			this.setVisible(false);
			new MazeFrame();

		}
		if (e.getSource() == hic) {
			for (MazeCell[] c : cells)
				for (MazeCell c2 : c)
					c2.setStatus(c2.getStatus());
		}
	}// end action performed

	private void setUpControlPanel() {
		controls = new JPanel();
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
		if (!φρ)
			return;
		if (mode == CPU) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				if (getNeighbor(mex.peek(), RIGHT) != null && !mex.peek().isBlockedRight()) {
					mex.peek().setStatus(mex.peek().getStatus());
					mex.push(getNeighbor(mex.peek(), RIGHT));
					mex.peek().setPStat(true);
				}
				break;
			case KeyEvent.VK_LEFT:
				if (getNeighbor(mex.peek(), LEFT) != null && !mex.peek().isBlockedLeft()) {
					mex.peek().setStatus(mex.peek().getStatus());
					mex.push(getNeighbor(mex.peek(), LEFT));
					mex.peek().setPStat(true);
				}
				break;
			case KeyEvent.VK_DOWN:
				if (getNeighbor(mex.peek(), DOWN) != null && !mex.peek().isBlockedDown()) {
					mex.peek().setStatus(mex.peek().getStatus());
					mex.push(getNeighbor(mex.peek(), DOWN));
					mex.peek().setPStat(true);
				}
				break;
			case KeyEvent.VK_UP:
				if (getNeighbor(mex.peek(), UP) != null && !mex.peek().isBlockedUp()) {
					mex.peek().setStatus(mex.peek().getStatus());
					mex.push(getNeighbor(mex.peek(), UP));
					mex.peek().setPStat(true);
				}
				break;
			}

		} else {

			switch (e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				if (getNeighbor(mex.peek(), RIGHT) != null && getNeighbor(mex.peek(), RIGHT).getPly() == 0
						&& !mex.peek().isBlockedRight()) {
					mex.peek().setStatus(mex.peek().getStatus());
					mex.push(getNeighbor(mex.peek(), RIGHT));
					mex.peek().setPly(2, p2);
				} else if (getNeighbor(mex.peek(), RIGHT) != null && getNeighbor(mex.peek(), RIGHT).getPly() == 2
						&& !mex.peek().isBlockedRight()) {
					MazeCell yes = getNeighbor(mex.peek(), RIGHT);
					while (mex.peek() != yes) {
						mex.pop().setPly(0, null);
					}
				} else if (getNeighbor(mex.peek(), RIGHT) != null && !tex.isEmpty()
						&& getNeighbor(mex.peek(), RIGHT) == tex.peek()) {
					for (int i = 0; i < ROWS / 5; i++)
						if (!tex.isEmpty())
							tex.pop().setPly(0, null);
				}
				break;
			case KeyEvent.VK_LEFT:
				if (getNeighbor(mex.peek(), LEFT) != null && getNeighbor(mex.peek(), LEFT).getPly() == 0
						&& !mex.peek().isBlockedLeft()) {
					mex.peek().setStatus(mex.peek().getStatus());
					mex.push(getNeighbor(mex.peek(), LEFT));
					mex.peek().setPly(2, p2);
				} else if (getNeighbor(mex.peek(), LEFT) != null && getNeighbor(mex.peek(), LEFT).getPly() == 2
						&& !mex.peek().isBlockedLeft()) {
					MazeCell yes = getNeighbor(mex.peek(), LEFT);
					while (mex.peek() != yes) {
						mex.pop().setPly(0, null);
					}
				} else if (getNeighbor(mex.peek(), LEFT) != null && !tex.isEmpty()
						&& getNeighbor(mex.peek(), LEFT) == tex.peek()) {
					for (int i = 0; i < ROWS / 5; i++)
						if (!tex.isEmpty())
							tex.pop().setPly(0, null);
				}
				if (mex.peek().col() == 0)
					win(2);
				break;
			case KeyEvent.VK_DOWN:
				if (getNeighbor(mex.peek(), DOWN) != null && getNeighbor(mex.peek(), DOWN).getPly() == 0
						&& !mex.peek().isBlockedDown()) {
					mex.peek().setStatus(mex.peek().getStatus());
					mex.push(getNeighbor(mex.peek(), DOWN));
					mex.peek().setPly(2, p2);
				} else if (getNeighbor(mex.peek(), DOWN) != null && getNeighbor(mex.peek(), DOWN).getPly() == 2
						&& !mex.peek().isBlockedDown()) {
					MazeCell yes = getNeighbor(mex.peek(), DOWN);
					while (mex.peek() != yes) {
						mex.pop().setPly(0, null);
					}
				} else if (getNeighbor(mex.peek(), DOWN) != null && !tex.isEmpty()
						&& getNeighbor(mex.peek(), DOWN) == tex.peek()) {
					for (int i = 0; i < ROWS / 5; i++)
						if (!tex.isEmpty())
							tex.pop().setPly(0, null);
				}
				if (mex.peek().row() == ROWS - 1)
					p2b = true;
				if (p2t && p2b) {
					for (int i = 0; i < ROWS / 5; i++)
						mex.pop().setPly(0, null);
					p2b = false;
				}
				break;
			case KeyEvent.VK_UP:
				if (getNeighbor(mex.peek(), UP) != null && getNeighbor(mex.peek(), UP).getPly() == 0
						&& !mex.peek().isBlockedUp()) {
					mex.peek().setStatus(mex.peek().getStatus());
					mex.push(getNeighbor(mex.peek(), UP));
					mex.peek().setPly(2, p2);
				} else if (getNeighbor(mex.peek(), UP) != null && getNeighbor(mex.peek(), UP).getPly() == 2
						&& !mex.peek().isBlockedUp()) {
					MazeCell yes = getNeighbor(mex.peek(), UP);
					while (mex.peek() != yes) {
						mex.pop().setPly(0, null);
					}
				} else if (getNeighbor(mex.peek(), UP) != null && !tex.isEmpty()
						&& getNeighbor(mex.peek(), UP) == tex.peek()) {
					for (int i = 0; i < ROWS / 5; i++)
						if (!tex.isEmpty())
							tex.pop().setPly(0, null);
				}
				if (mex.peek().row() == 0)
					p2t = true;
				if (p2t && p2b) {
					for (int i = 0; i < ROWS / 5; i++)
						mex.pop().setPly(0, null);
					p2t = false;
				}
				break;
			case KeyEvent.VK_D:
				if (getNeighbor(tex.peek(), RIGHT) != null && getNeighbor(tex.peek(), RIGHT).getPly() == 0
						&& !tex.peek().isBlockedRight()) {
					tex.peek().setStatus(tex.peek().getStatus());
					tex.push(getNeighbor(tex.peek(), RIGHT));
					tex.peek().setPly(1, p1);
				} else if (getNeighbor(tex.peek(), RIGHT) != null && getNeighbor(tex.peek(), RIGHT).getPly() == 1
						&& !tex.peek().isBlockedRight()) {
					MazeCell yes = getNeighbor(tex.peek(), RIGHT);
					while (tex.peek() != yes) {
						tex.pop().setPly(0, null);
					}
				} else if (getNeighbor(tex.peek(), RIGHT) != null && !mex.isEmpty()
						&& getNeighbor(tex.peek(), RIGHT) == mex.peek()) {
					for (int i = 0; i < ROWS / 5; i++)
						if (!mex.isEmpty())
							mex.pop().setPly(0, null);
				}
				if (tex.peek().col() == COLS - 1)
					win(1);
				break;
			case KeyEvent.VK_A:
				if (getNeighbor(tex.peek(), LEFT) != null && getNeighbor(tex.peek(), LEFT).getPly() == 0
						&& !tex.peek().isBlockedLeft()) {
					tex.peek().setStatus(tex.peek().getStatus());
					tex.push(getNeighbor(tex.peek(), LEFT));
					tex.peek().setPly(1, p1);
				} else if (getNeighbor(tex.peek(), LEFT) != null && getNeighbor(tex.peek(), LEFT).getPly() == 1
						&& !tex.peek().isBlockedLeft()) {
					MazeCell yes = getNeighbor(tex.peek(), LEFT);
					while (tex.peek() != yes) {
						tex.pop().setPly(0, null);
					}
				} else if (getNeighbor(tex.peek(), LEFT) != null && !mex.isEmpty()
						&& getNeighbor(tex.peek(), LEFT) == mex.peek()) {
					for (int i = 0; i < ROWS / 5; i++)
						if (!mex.isEmpty())
							mex.pop().setPly(0, null);
				}
				break;
			case KeyEvent.VK_S:
				if (getNeighbor(tex.peek(), DOWN) != null && getNeighbor(tex.peek(), DOWN).getPly() == 0
						&& !tex.peek().isBlockedDown()) {
					tex.peek().setStatus(tex.peek().getStatus());
					tex.push(getNeighbor(tex.peek(), DOWN));
					tex.peek().setPly(1, p1);
				} else if (getNeighbor(tex.peek(), DOWN) != null && getNeighbor(tex.peek(), DOWN).getPly() == 1
						&& !tex.peek().isBlockedDown()) {
					MazeCell yes = getNeighbor(tex.peek(), DOWN);
					while (tex.peek() != yes) {
						tex.pop().setPly(0, null);
					}
				} else if (getNeighbor(tex.peek(), DOWN) != null && !mex.isEmpty()
						&& getNeighbor(tex.peek(), DOWN) == mex.peek()) {
					for (int i = 0; i < ROWS / 5; i++)
						if (!mex.isEmpty())
							mex.pop().setPly(0, null);
				}
				if (tex.peek().row() == ROWS - 1)
					p1b = true;
				if (p1t && p1b) {
					for (int i = 0; i < ROWS / 5; i++)
						tex.pop().setPly(0, null);
					p1b = false;
				}
				break;
			case KeyEvent.VK_W:
				if (getNeighbor(tex.peek(), UP) != null && getNeighbor(tex.peek(), UP).getPly() == 0
						&& !tex.peek().isBlockedUp()) {
					tex.peek().setStatus(tex.peek().getStatus());
					tex.push(getNeighbor(tex.peek(), UP));
					tex.peek().setPly(1, p1);
				} else if (getNeighbor(tex.peek(), UP) != null && getNeighbor(tex.peek(), UP).getPly() == 1
						&& !tex.peek().isBlockedUp()) {
					MazeCell yes = getNeighbor(tex.peek(), UP);
					while (tex.peek() != yes) {
						tex.pop().setPly(0, null);
					}
				} else if (getNeighbor(tex.peek(), UP) != null && !mex.isEmpty()
						&& getNeighbor(tex.peek(), UP) == mex.peek()) {
					for (int i = 0; i < ROWS / 5; i++)
						if (!mex.isEmpty())
							mex.pop().setPly(0, null);
				}
				if (tex.peek().row() == 0)
					p1t = true;
				if (p1t && p1b) {
					for (int i = 0; i < ROWS / 5; i++)
						tex.pop().setPly(0, null);
					p1t = false;

				}
				break;
			case KeyEvent.VK_NUMPAD0:
				setVisible(false);
				new MazeFrame();

				break;

			case KeyEvent.VK_Q:
				setVisible(false);
				new MazeFrame();

				break;
			}

		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}// end class
