package engine;
//package FORKIDS;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Stack;
import java.util.function.BiConsumer;

import javax.imageio.ImageIO;
import javax.swing.*;

public class MazeFrame extends JFrame implements ActionListener {

	public static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3; // directions
	public static final int BORING = 0, BOXES = 1; // carve mode
	public static final int BOT = -2, P1CPU = -1, BLANK = 0, P1 = 1, P2 = 2;

	public static final int[][] KEYS = {
			{KeyEvent.VK_W, KeyEvent.VK_D, KeyEvent.VK_S, KeyEvent.VK_A},
			{KeyEvent.VK_I, KeyEvent.VK_L, KeyEvent.VK_K, KeyEvent.VK_J},
			{KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT},
			{KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD6, KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD4},
	};


	private int rows; // 20 and 35 best
	private int cols;
	private Mode mode; // Gamemode
	private int aispeed; // Speed of bot
	private double startTime; // Time game is started
	private double mazeFidelity; // Called at the end of CarveStep
	private String matchName;// Will be added onto the fileOutput if saved

	private JPanel controls, lBorder, rBorder, tBorder, maze;
	private JButton[] buttons = { new JButton("THIS ONE'S TRASH"), new JButton("I'M READY"), new JButton("SETTINGS"),
			new JButton("SAVE"), new JButton("EXIT") }; // All buttons in control panel
	private JButton readyButton;
	private MazeCell[][] cells; // All cells
	private MazeCell begi, end, begi2, end2; // Start and end cells
	private Stack<MazeCell> tex; // Left side stack
	private Stack<MazeCell> mex; // Right side stack
	private ArrayList<Stack<MazeCell>> chui;
	private ReadyListener embededListener; // Put inside I'm Ready button, called on move
	private OverarchingListener frameListener; // Goes everywhere else
	private Color borderColor = Color.BLACK;
	private Thread botThread;

	private boolean on;
	public int matchTime;

	// Colors for end gradient
	private Color beg = new Color((int) (Math.random() * 256), (int) (Math.random() * 256),
			(int) (Math.random() * 256));
	private Color plead = new Color((int) (Math.random() * 256), (int) (Math.random() * 256),
			(int) (Math.random() * 256));
	private Color[] colorTeams = {beg,beg,beg,beg};

	private final Color p1 = beg;
	private final Color p2 = plead;

	/** Constructors **/

	public MazeFrame(Mode mode, double mazeFidelity, String matchName, int r, int c) {
		this(mode, mazeFidelity, (int) (200 - (200 * (1 - mazeFidelity))), matchName, r, c);
	}

	public MazeFrame(Mode mode, double mazeFidelity, int aispeed, String matchName, int r, int c) {
		super("Maze");

		this.matchName = matchName;
		this.mode = mode;
		this.mazeFidelity = mazeFidelity;
		this.aispeed = aispeed;
		this.rows = r;
		this.cols = c;
		embededListener = new ReadyListener(this);
		frameListener = new OverarchingListener(this);

		if (matchName == null || matchName.equals("")) {
			this.matchName = "unnamed";
		}

		setUpControlPanel();// make the buttons & put them in the north
		instantiateCells();// give birth to all the mazeCells & get them onto the screen
		carveARandomMaze();// this will knock down walls to create a maze
		setUpEdges(); // Fills in other edges with black

		// finishing touches
		setBackground(Color.WHITE);
		setForeground(Color.WHITE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);
		setFocusable(true);
		addKeyListener(frameListener);
		requestFocus();
	}

	/** End Constructors **/

	/************ Setup Methods ************/

	// Puts black borders on
	private void setUpEdges() {
		lBorder = new JPanel();
		rBorder = new JPanel();
		tBorder = new JPanel();
		lBorder.setBackground(borderColor);
		rBorder.setBackground(borderColor);
		tBorder.setBackground(borderColor);
		add(lBorder, BorderLayout.WEST);
		add(rBorder, BorderLayout.EAST);
		add(tBorder, BorderLayout.NORTH);
	}

	// Makes the maze itself
	private void instantiateCells() {

		// Initialize new stacks
		tex = new Stack<MazeCell>();
		mex = new Stack<MazeCell>();
		chui = new ArrayList<Stack<MazeCell>>();
		if(mode==Mode.T4){
			for(int i = 0; i<4; i++)
				chui.add(new Stack<MazeCell>());
		}
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

		// Set colors in teams
		if(mode == Mode.T4){
			colorTeams = new Color[4]; //color for each player
			colorTeams[0] = beg; //t1 p1
			colorTeams[2] = plead; //t2 p1
			int differ = 100; //total amount of RGB difference
			for(int i=1; i<4; i+=2){
				ArrayList<Double> colorAssign = new ArrayList<Double>();
				colorAssign.add(Math.random()); //random percent
				colorAssign.add(Math.random()*(1-colorAssign.get(0))); //random of remaining percent
				colorAssign.add(1-colorAssign.get(1)-colorAssign.get(0)); //remaining percent
				int[] newColor = new int[3];
				int[] oldColor = {colorTeams[i-1].getRed(), colorTeams[i-1].getGreen(), colorTeams[i-1].getBlue()};
				for(int j=0; j<3; j++){ //for red, green, and blue
					double percentChosen = colorAssign.remove((int)(Math.random()*colorAssign.size())); //use a percent from list
					int addTest = (int)(oldColor[j]+differ*percentChosen); //if increase value
					int subtractTest = (int)(oldColor[j]-differ*percentChosen); //if decrease value
					
					if(addTest<255 && subtractTest>0){ //if in bounds, randomly choose direction
						if(Math.random()>=.5){
							newColor[j] = addTest;
						}else{
							newColor[j] = subtractTest;
						}
					}else if (subtractTest<0){ //if cant go down, go up
						newColor[j] = addTest;
					}else{
						newColor[j] = subtractTest; //if cant go up, go down
					}
				}
				colorTeams[i]=new Color(newColor[0],newColor[1],newColor[2]); //set color to adjusted red, green, and blue

			}
		}


		// Put the maze on the screen
		this.add(maze, BorderLayout.CENTER);
	}

	// Sets up bottom of screen
	private void setUpControlPanel() {

		// Create control panel
		controls = new JPanel();
		controls.setBackground(Color.BLACK);

		// Initialize all buttons and add them to the panel
		for (JButton b : buttons) {
			controls.add(b);
			b.setBackground(Color.GRAY);
			b.setForeground(Color.WHITE);
			b.addActionListener(this);
		}

		readyButton = buttons[1];

		// Add ready listener and set panel to bottom
		buttons[1].addKeyListener(embededListener);
		buttons[2].addKeyListener(embededListener);
		buttons[3].addKeyListener(embededListener);
		buttons[0].addKeyListener(frameListener);
		buttons[4].addKeyListener(frameListener);
		add(controls, BorderLayout.SOUTH);
	}

	// Called when a new maze is not carved
	private void carveARandomMaze() {

		maze.setBackground(MazeCell.lineColor);

		// Pick beginning and end
		begi = cells[(int) (Math.random() * (rows * .5) + rows * .25)][0];
		end = cells[(int) (Math.random() * (rows * .5) + rows * .25)][cols - 1];

		if(mode==Mode.T4){
			begi = cells[(int) (Math.random() * (rows * .5))][0];
			begi2 = cells[(int) (Math.random() * (rows * .5) + rows * .5)][0];
			end2 = cells[(int) (Math.random() * (rows * .5))][cols - 1];
			end = cells[(int) (Math.random() * (rows * .5) + rows * .5)][cols - 1];
			chui.get(0).push(begi);
			chui.get(1).push(begi2);
			chui.get(2).push(end);
			chui.get(3).push(end2);
		}

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
		if (mode == Mode.TT || mode == Mode.V2) {
			mex.peek().setPly(2, p2);
			if (mode == Mode.V2)
				tex.peek().setPly(1, p1);

			for (int i = 0; i < rows; i++) {
				cells[i][0].clearWallDir(LEFT);
				cells[i][cols - 1].clearWallDir(RIGHT);
			}
		}else if(mode == Mode.T4){
			for(int i=0; i<4; i++){
				chui.get(i).peek().setPly(i+1, colorTeams[i]);
			}
			for (int i = 0; i < rows; i++) {
				cells[i][0].clearWallDir(LEFT);
				cells[i][cols - 1].clearWallDir(RIGHT);
			}
		}

		// Carve the maze
		while (!tex.isEmpty())
			stepCarve();

		// Finish it off
		stepCarve();

		if (!(mode == Mode.CPU))
			begi.setStatus(BLANK);
		if(mode==Mode.T4)
			begi.setPly(1, colorTeams[0]);



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
			MazeCell chosen1 = bop.get(0);
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

	// Takes the next step in solving the maze in CPU mode
	private boolean solveStep() {
		if (on) {
			// If player has reached the beginning
			if (mex.peek() == begi) {
				// Player wins
				win(P1CPU); // 0 since its not the same as P2 mode
				return false;
			}

			// If bot is at the end
			if (tex.peek() == end) {
				// Bot wins
				win(BOT); // -1 for bot
				return false;
			}

			// Priority queue of direction choices
			int[] dirspq = getBestDir(tex.peek(), end);
			// If neither win conditions are met, move on.
			for (int i = 0; i < 4; i++) {
				MazeCell option = getNeighbor(tex.peek(), dirspq[i]);
				// If cell enterable and unvisited
				if (option != null
						&& !(tex.peek().isBlockedDir(dirspq[i]))
						&& option.isBlank()) {
					// Move into cell
					tex.push(option);
					option.setStatus(MazeCell.VISITED);
					return true;
				}
			}
			// If not able to move in any direction, move backwards
			tex.pop().setStatus(MazeCell.DEAD);
			return true;
		}
		return false;
	}

	// Moves players in P2 mode
	public void playerMove(int player, int dir) {

		// Default stacks
		Stack<MazeCell> mex = this.tex;
		Stack<MazeCell> tex = this.mex;
		Color pCo = p1;

		// Skippable cells based on mode
		int enemy = player % 2 + 1;
		int skipO = 0;

		if (mode == Mode.V2) {
			skipO = enemy;
		} else if (mode == Mode.TT) {
			skipO = player;
		}

		// Set stacks to player stacks
		if (player == 2) {
			pCo = p2;
			mex = this.mex;
			tex = this.tex;
		}

		// Convenience Variables
		MazeCell head = mex.peek();
		MazeCell nextOver = getNeighbor(head, dir);

		if (nextOver == null)
			return;

		if (!head.isBlockedDir(dir) && nextOver.getPly() != enemy) {
			if (nextOver.getPly() == 0) {
				// into blank
				head.repaint();
				mex.push(nextOver);
				nextOver.setPly(player, pCo);

			} else if (nextOver.getPly() == player) {
				// into own
				MazeFrame.splicePath(mex, nextOver);
			}
		} else if (!tex.isEmpty() && nextOver == tex.peek()) {
			// into enemy head
			attack(tex);

		} else if (nextOver.getPly() == skipO) {
			// cell the player lands in
			MazeCell nextOverPlus = getNeighbor(nextOver, dir);
			if (nextOverPlus == null)
				return;

			if (nextOverPlus.getPly() == 0) {
				// can skip over
				head.repaint();
				mex.push(nextOverPlus);
				nextOverPlus.setPly(player, pCo);

			} else if (nextOverPlus.getPly() == player) {
				// skipping back
				MazeFrame.splicePath(mex, nextOverPlus);
			}
		}
	}

	// playerMove case for bot mode
	public void playerMove(int dir) {
		MazeCell nextOver = getNeighbor(mex.peek(), dir);
		if (nextOver != null && !mex.peek().isBlockedDir(dir)) {
			mex.peek().setPHead(false);
			mex.push(nextOver);
			nextOver.setPlayered(true);
		}
	}
	//playerMove case for teams mode
	public void playerMove(int team, int player, int dir){
		int me = (team-1)*2+(player-1);
		int tmate = me ^ 1;
		int enem = (team-1)^1;
		Color pCo = colorTeams[me];
		Stack<MazeCell> myStack = chui.get(me);
		Stack<MazeCell> teamStack = chui.get(tmate);

		Stack<MazeCell> en1Stack = chui.get(2*enem);
		Stack<MazeCell> en2Stack = chui.get(2*enem+1);

		// Convenience Variables
		MazeCell head = myStack.peek();
		MazeCell nextOver = getNeighbor(head, dir);
		if (nextOver == null)
			return;
		int nextPly = nextOver.getPly();

		if (!head.isBlockedDir(dir) && (nextPly == 0 || nextPly == me+1)) {
			if (nextPly == 0) {
				// into blank
				head.repaint();
				myStack.push(nextOver);
				nextOver.setPly(me+1, pCo);

			} else if (nextPly == me+1) {
				// into own
				MazeFrame.splicePath(myStack, nextOver);
			}
		} else if (en1Stack.size()>1 && nextOver == en1Stack.peek()) {
			// into enemy head
			attack(en1Stack);

		} else if (en2Stack.size()>1 && nextOver == en2Stack.peek()) {
			// into enemy head
			attack(en2Stack);

		} else if (teamStack.size()>1 && nextOver == teamStack.peek()) {
			// into teammate head
			attack(teamStack);

		} else if (nextPly == tmate+1 && getNeighbor(nextOver, dir)!=null) {
			// find end of potential boost
			MazeCell movingCells = boostEnd(nextOver, team, dir);
			if (movingCells == null)
				return;
			// if blank to go into
			if (movingCells.getPly() == 0) {
				// can skip over
				head.repaint();
				myStack.push(movingCells);
				movingCells.setPly(me+1, pCo);

			} else if (movingCells.getPly() == me+1) {
				// skipping back
				MazeFrame.splicePath(myStack, movingCells);
			}
		} else if (nextOver.getPly() != 0 && nextOver.getPly()!= me+1) {
			// cell the player lands in
			MazeCell nextOverPlus = getNeighbor(nextOver, dir);
			if (nextOverPlus == null)
				return;

			if (nextOverPlus.getPly() == 0) {
				// can skip over
				head.repaint();
				myStack.push(nextOverPlus);
				nextOverPlus.setPly(me+1, pCo);

			} else if (nextOverPlus.getPly() == me+1) {
				// skipping back
				MazeFrame.splicePath(myStack, nextOverPlus);
			}
		}
	}

	/**************** UTILITY METHODS ****************/
	// Applies gradient to player stack
	// painter is function that updates a cell's color
	private static void applyGradient(Stack<MazeCell> stack, Color start, Color end,
			BiConsumer<MazeCell, Color> painter)
	{
		// Obtain color components
		int r1 = start.getRed(), r2 = end.getRed();
		int g1 = start.getGreen(), g2 = end.getGreen();
		int b1 = start.getBlue(), b2 = end.getBlue();
		// Ratio is from 0 to 1 in increments of 1 / sizzle
		int sizzle = stack.size() - 1;
		int i = 0;
		double ratio;
		for (MazeCell mc : stack) {
			ratio = (double) i++ / sizzle;
			painter.accept(mc,
					new Color((int) (r1 * (1- ratio) + r2 * ratio),
							(int) (g1 * (1- ratio) + g2 * ratio),
							(int) (b1 * (1- ratio) + b2 * ratio)));
		}
	}
	
	// Deletes (rows/5) cells from victim
	private void attack(Stack<MazeCell> bonked)
	{
		for (int i = 0; i < rows / 5; ++i)
			if (bonked.size()>1)
				bonked.pop().setPly(0, null);
	}
	
	// Pop stack until back on dest
	private static void splicePath(Stack<MazeCell> stack, MazeCell dest)
	{
		while (stack.peek() != dest)
			stack.pop().setPly(0, null);
	}

	// If the possible cell is valid
	private boolean isInBounds(int r, int c) {
		return r >= 0 && r < rows && c >= 0 && c < cols;
	}

	// Computes the end of a potential boost given skipped team-mate cell
	public MazeCell boostEnd(MazeCell start, int team, int dir)
	{
		int enem = ((team - 1) ^ 1);
		MazeCell movingCell = getNeighbor(start, dir);
		while (movingCell != null && (movingCell.getPly() - 1) >> 1 == enem)
			movingCell = getNeighbor(movingCell, dir);
		return movingCell;
	}

	// Returns an array of directional neighbor cells to the given cells
	private ArrayList<MazeCell> blankNeighbors(MazeCell mc) {
		ArrayList<MazeCell> results = new ArrayList<MazeCell>();
		// list out directions
		ArrayList<Integer> dirs = new ArrayList<Integer>();
		dirs.add(UP);
		dirs.add(RIGHT);
		dirs.add(DOWN);
		dirs.add(LEFT);

		if (mc.row() == 0) { // if on ceiling (must prioritize leaving)
			if (enlistNeighbors(mc, dirs.remove(2)))
				results.add(getNeighbor(mc, DOWN)); // remove down from possible, add to list as priority

		} else if (mc.row() == rows - 1) { // if on floor (must prioritize leaving)
			if (enlistNeighbors(mc, dirs.remove(0)))
				results.add(getNeighbor(mc, UP)); // remove up from possible, add to list as priority
		}
		while (dirs.size() > 0) { // add each of dirs to results in random order
			int chosenIndex = (int) (Math.random() * dirs.size());
			int chosenDir = dirs.remove(chosenIndex);
			if (enlistNeighbors(mc, chosenDir))
				results.add(getNeighbor(mc, chosenDir));
		}

		return results;
	}

	// If can add neighbor to results
	private boolean enlistNeighbors(MazeCell mc, int dir) {
		MazeCell inQuestion = getNeighbor(mc, dir);
		// if it is real, and blank
		return (inQuestion != null && inQuestion.isBlank());
	}

	// Returns the closest MazeCell in direction
	public MazeCell getNeighbor(MazeCell mc, int dir) {
		int r = mc.row();
		int c = mc.col();
		switch (dir) {
		// If exists, return cell in direction
		case LEFT:
			c--;
			break;
		case RIGHT:
			c++;
			break;
		case DOWN:
			r++;
			break;
		case UP:
			r--;
			break;
		default:
			return null;
		}
		if (isInBounds(r, c)) return cells[r][c];
		else return null;
	}

	// Returns a set of directions in order of importance
	private static int[] getBestDir(MazeCell orig, MazeCell dest) {
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
	private static int getDirectionFrom(MazeCell orig, MazeCell dest) {
		int yDis = dest.row() - orig.row();
		int xDis = dest.col() - orig.col();
		// Find the best direction to dest
		if (yDis < 0)
			return UP;
		if (yDis > 0)
			return DOWN;
		if (xDis > 0)
			return RIGHT;
		if (xDis < 0)
			return LEFT;
		// Default case is nonexistent direction
		return -1;
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
		new Settings(this, mode, mazeFidelity, aispeed, matchName, rows, cols);
	}

	// Fully resets this maze's frame
	public void resetMaze() {
		new MazeFrame(mode, mazeFidelity, null, rows, cols);
		if (botThread != null)
			botThread.interrupt();
		setVisible(false);
		System.gc();
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
			File outputDir = new File("./output");
			// If the directory does not exist, create it
			if (!outputDir.exists())
				outputDir.mkdir();
			// Name file
			String name;
			if (matchTime != 0) {
				name = (int) (matchTime / 1000) + " seconds " + matchName;
			} else {
				name = matchName;
			}
			File outputFile = new File("./output", name + ".JPEG");
			// If duplicates exist, distinguish by number
			for (int id = 1; outputFile.exists(); id++) {
				outputFile = new File("./output", name + "(" + id + ").JPEG");
			}
			// Write it to disk
			ImageIO.write(imagebuf, "jpeg", outputFile);
			Runtime.getRuntime().exec("explorer.exe /select," + outputFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Called at the end of the game
	public void win(int player) {

		// Turn off maze actions
		on = false;

		// Apply gradient to winning player
		if (player == BOT) { // Bot win
			MazeFrame.applyGradient(tex, Color.WHITE, Color.BLACK, MazeCell::setGrad);
		} else if (player == P1CPU) { // Player in bot mode win
			MazeFrame.applyGradient(mex, beg, plead, MazeCell::setGrad);
		} else if (player == P1) { // Player 1 in two-player win
			Color badiddle = new Color((int) (Math.random() * 256), (int) (Math.random() * 256),
					(int) (Math.random() * 256));
			MazeFrame.applyGradient(tex, beg, badiddle, (MazeCell mc, Color c) -> {
				mc.setPly(1, c);
			});
		} else if (player == P2) { // Player 2 in two-player win
			Color badiddle = new Color((int) (Math.random() * 256), (int) (Math.random() * 256),
					(int) (Math.random() * 256));
			MazeFrame.applyGradient(mex, plead, badiddle, (MazeCell mc, Color c) -> {
				mc.setPly(2, c);
			});
		}

		// Display match time
		matchTime = (int) (((int) (System.currentTimeMillis()) - startTime));
		JOptionPane.showMessageDialog(this, (double) matchTime / 1000 + " seconds");
	}
	
	public void teamWin(int team){
		on=false;
		for(int i=2*(team-1); i<=2*(team-1)+1; i++){
			Color badiddle = new Color((int) (Math.random() * 256), (int) (Math.random() * 256),
					(int) (Math.random() * 256));
			MazeFrame.applyGradient(chui.get(i), colorTeams[i], badiddle, (MazeCell mc, Color c) -> {
				mc.setPly(1, c);});
		}
		matchTime = (int) (((int) (System.currentTimeMillis()) - startTime));
		JOptionPane.showMessageDialog(this, "Team "+team+" Win\n"+(double) matchTime / 1000 + " seconds");
	}

	public void startGame() {
		if (!on) {
			for (MazeCell[] out : cells)
				for (MazeCell in : out)
					in.go();
			on = true;
			startTime = (int) (System.currentTimeMillis());
			if (mode == Mode.CPU) {
				botThread = new Thread(() -> {
					while (solveStep())
						pause(aispeed);
				});
				botThread.start();
			}
			readyButton.requestFocus();
		}
	}

	/***********************************************/

	// Called any time that you press a button
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttons[0]) {
			resetMaze();
		} else if (e.getSource() == buttons[1]) {
			startGame();
		} else if (e.getSource() == buttons[2]) {
			openSettings();
		} else if (e.getSource() == buttons[3]) {
			saveMaze();
		} else if (e.getSource() == buttons[4]) {
			System.exit(0);
		}
	}// end action performed

	/**************************/
	/* ACCESSORS AND MUTATORS */
	/**************************/

	public JPanel getGameWindow() {return maze;}

	public JButton getButton(int i) {return buttons[i];}

	public Stack<MazeCell> getMex() {return mex;}

	public Stack<MazeCell> getTex() {return tex;}

	public Stack<MazeCell> getStex(int p) {return chui.get(p);}

	public MazeCell[][] getCells() {return cells;}

	public int getRows() {return rows;}

	public int getColumns() {return cols;}

	public boolean isOn() {return on;}

	public void setOn(boolean on) {this.on = on;}

	public Mode getMode() {return mode;}

	public void setMode(Mode mode) {this.mode = mode;}

	public double getStartTime() {return startTime;}

	public void setStartTime(double startTime) {this.startTime = startTime;}

	public double getMatchTime() {return matchTime;}

	public Thread getThread() {return botThread;}

}// end class
