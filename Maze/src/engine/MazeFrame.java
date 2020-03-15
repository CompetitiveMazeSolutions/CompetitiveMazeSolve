package engine;
//package FORKIDS;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import bots.*;
import consts.*;

public class MazeFrame extends JFrame implements ActionListener {

	private final int rows; // 20 and 35 best
	private final int cols;
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
	private Stack<MazeCell>	tex, // Left side stack
							mex; // Right side stack
	private ArrayList<Stack<MazeCell>> chui;
	private ReadyListener embededListener; // Put inside I'm Ready button, called on move
	private OverarchingListener frameListener; // Goes everywhere else
	private Color borderColor = Color.BLACK;
	private SolveBot solver;
	private CarveBot carver;
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
		this.carver = new CarveBot(this, mazeFidelity);
		if (mode == Mode.CPU)
			this.solver = new SolveBot(this, aispeed);
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
		if (mode == Mode.CPU) {
			for (int i = 0; i < cells.length; i++) {
				for (int j = 0; j < cells[i].length; j++) {
					cells[i][j] = new BotMazeCell(i, j, solver);
					maze.add(cells[i][j]);
				}
			}
		} else {
			for (int i = 0; i < cells.length; i++) {
				for (int j = 0; j < cells[i].length; j++) {
					cells[i][j] = new MazeCell(i, j);
					maze.add(cells[i][j]);
				}
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
		
		if (mode == Mode.CPU)
			solver.setEndPoints(begi, end);

		// Make beginning and end
		begi.clearWallDir(Direction.LEFT);
		end.clearWallDir(Direction.RIGHT);

		// Push beginning to bot and end to p2
		tex.push(begi);
		mex.push(end);

		// Set to be playered
		if (mode == Mode.CPU) {
			mex.peek().setPly(Player.P2, null);
			tex.peek().setPly(Player.P1, null);
		}

		// Sets color and player of stack starts
		if (mode == Mode.TT || mode == Mode.V2) {
			mex.peek().setPly(Player.P2, p2);
			if (mode == Mode.V2)
				tex.peek().setPly(Player.P1, p1);
		}else if(mode == Mode.T4){
			for(Player p : Player.values())
				chui.get(p.ordinal()).peek().setPly(p, colorTeams[p.ordinal()]);
		}
		if (mode != Mode.CPU)
			for (int i = 0; i < rows; i++) {
				cells[i][0].clearWallDir(Direction.LEFT);
				cells[i][cols - 1].clearWallDir(Direction.RIGHT);
			}
		carver.setStartPoint(begi);
		carver.carveMaze();

		if(mode==Mode.T4)
			begi.setPly(Player.P1, colorTeams[0]);
	}

	private void skipOverTo(MazeCell mc, Stack<MazeCell> stex, Player p, Color pCo) {
		if (mc == null)
			return;
		if (mc.getPly() == null) {
			// can skip over
			stex.peek().repaint();
			stex.push(mc);
			mc.setPly(p, pCo);
		} else if (mc.getPly() == p) {
			// skipping back
			MazeFrame.splicePath(stex, mc);
		}
	}

	// Moves players in P2 mode
	public void playerMove(Player player, Direction dir) {
		// Default stacks	
		Stack<MazeCell> mex=null,tex=null;
		Color pCo=null;
		Player enemy=null;
		switch(player) {
		case P1:
			mex = this.tex; tex = this.mex;
			pCo = p1;
			enemy = Player.P2;
			break;
		case P2:
			mex = this.mex; tex = this.tex;
			pCo = p2;
			enemy = Player.P1;
			break;
		}
		// Skippable cells based on mode
		Player skipO = null;
		if (mode == Mode.V2) {
			skipO = enemy;
		} else if (mode == Mode.TT) {
			skipO = player;
		}
		// Convenience Variables
		MazeCell head = mex.peek();
		MazeCell nextOver = getNeighbor(head, dir);
		if (nextOver == null)
			return;

		if (!head.isBlockedDir(dir) && nextOver.getPly() != enemy) {
			skipOverTo(nextOver, mex, player, pCo);
		} else if (!tex.isEmpty() && nextOver == tex.peek()) {
			// into enemy head
			attack(tex);
		} else if (nextOver.getPly() == skipO) {
			// cell the player lands in
			skipOverTo(getNeighbor(nextOver, dir), mex, player, pCo);
		}
	}

	// playerMove case for bot mode
	public void playerMove(Direction dir) {
		MazeCell nextOver = getNeighbor(mex.peek(), dir);
		if (nextOver != null && !mex.peek().isBlockedDir(dir)) {
			mex.peek().repaint();
			mex.push(nextOver);
			nextOver.setPly(Player.P2, null);
			if (nextOver == begi)
				botWin(Player.P2);
		}
	}
	//playerMove case for teams mode
	public void teamPlayerMove(Player player, Direction dir){
		Player me = player;
		Player tmate = me.teammate();
		int enem = (player.ordinal() >> 1)^1;
		
		Color pCo = colorTeams[me.ordinal()];
		Stack<MazeCell> myStack = chui.get(me.ordinal());
		ArrayList<Stack<MazeCell>> attackable = new ArrayList<>();
		attackable.add(chui.get(tmate.ordinal()));
		attackable.add(chui.get(2*enem));
		attackable.add(chui.get(2*enem+1));
		// Convenience Variables
		MazeCell head = myStack.peek();
		MazeCell nextOver = getNeighbor(head, dir);
		if (nextOver == null)
			return;
		Player nextPly = nextOver.getPly();

		if (!head.isBlockedDir(dir) && (nextPly == null || nextPly == me)) {
			skipOverTo(nextOver, myStack, me, pCo);
			return;
		}
		for (Stack<MazeCell> stex : attackable) {
			if (stex.size()>1 && nextOver == stex.peek()) {
				// into teammate or enemy head
				attack(stex);
				return;
			}
		}
		if (nextPly == tmate && getNeighbor(nextOver, dir)!=null) {
			// find end of potential boost
			skipOverTo(boostEnd(nextOver, me, dir), myStack, me, pCo);
		} else if (nextOver.getPly() != null && nextOver.getPly() != me) {
			// cell the player lands in
			skipOverTo(getNeighbor(nextOver, dir), myStack, me, pCo);
		}
	}

	/**************** UTILITY METHODS ****************/
	// Applies gradient to player stack
	private static void applyGradient(Stack<MazeCell> stack, Color start, Color end, Player p)
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
			mc.setPly(p,
					new Color((int) (r1 * (1 - ratio) + r2 * ratio),
							(int) (g1 * (1 - ratio) + g2 * ratio),
							(int) (b1 * (1 - ratio) + b2 * ratio)));
		}
	}
	
	// Deletes (rows/5) cells from victim
	private void attack(Stack<MazeCell> bonked)
	{
		for (int i = 0; i < rows / 5 && bonked.size() > 1; ++i)
			bonked.pop().setPly(null, null);
	}
	
	// Pop stack until back on dest
	private static void splicePath(Stack<MazeCell> stack, MazeCell dest)
	{
		while (stack.peek() != dest)
			stack.pop().setPly(null, null);
	}

	// If the possible cell is valid
	private boolean isInBounds(int r, int c) {
		return r >= 0 && r < rows && c >= 0 && c < cols;
	}

	// Computes the end of a potential boost given skipped team-mate cell
	public MazeCell boostEnd(MazeCell start, Player p, Direction dir)
	{
		MazeCell movingCell = getNeighbor(start, dir);
		while (movingCell != null && p.isEnemyOf(movingCell.getPly()))
			movingCell = getNeighbor(movingCell, dir);
		return movingCell;
	}

	// Returns the closest MazeCell in direction
	public MazeCell getNeighbor(MazeCell mc, Direction dir) {
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
	
	public void playerWin(Player player, Stack<MazeCell> stex,  Color dabiddle, Color badiddle) {
		on = false;
		MazeFrame.applyGradient(stex, dabiddle, badiddle, player);
		// Display match time
		matchTime = (int) (((int) (System.currentTimeMillis()) - startTime));
		JOptionPane.showMessageDialog(this, (double) matchTime / 1000 + " seconds");
	}

	// Called at the end of the game
	public void botWin(Player player) {
		if (player == Player.P1) // Bot win
			playerWin(player, tex, Color.WHITE, Color.BLACK);
		else if (player == Player.P2) // Player in bot mode win
			playerWin(player, mex, beg, plead);
	}
	
	public void versusWin(Player player) {
		Color badiddle = new Color((int) (Math.random() * 256), (int) (Math.random() * 256),
				(int) (Math.random() * 256));
		if (player == Player.P1) { // Player 1 in two-player win
			playerWin(player, tex, beg, badiddle);
		} else if (player == Player.P2) { // Player 2 in two-player win
			playerWin(player, mex, plead, badiddle);
		}
		
	}
	
	public void teamWin(Player player){
		on=false;
		Player[] teamps = {player, player.teammate()};
		for(Player p : teamps){
			Color badiddle = new Color((int) (Math.random() * 256), (int) (Math.random() * 256),
					(int) (Math.random() * 256));
			MazeFrame.applyGradient(chui.get(p.ordinal()), colorTeams[p.ordinal()], badiddle, p);
		}
		matchTime = (int) (((int) (System.currentTimeMillis()) - startTime));
		JOptionPane.showMessageDialog(this, "Team "+((player.ordinal()>>1)+1)+" Win\n"+(double) matchTime / 1000 + " seconds");
	}

	public void startGame() {
		if (!on) {
			for (MazeCell[] out : cells)
				for (MazeCell in : out)
					in.go();
			on = true;
			startTime = (int) (System.currentTimeMillis());
			if (mode == Mode.CPU) {
				botThread = new Thread(solver);
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

	public int getRows() {return rows;}

	public int getColumns() {return cols;}

	public boolean isOn() {return on;}

	public Mode getMode() {return mode;}

	public double getStartTime() {return startTime;}

	public void setStartTime(double startTime) {this.startTime = startTime;}

	public double getMatchTime() {return matchTime;}

	public Thread getThread(){return botThread;}

}// end class
