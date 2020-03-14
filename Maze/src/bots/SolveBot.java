package bots;

import java.util.Stack;
import consts.*;
import engine.*;

public class SolveBot extends Bot implements Runnable
{
	private MazeCell end;
	private int aispeed;
	public SolveBot(MazeFrame game, int aispeed)
	{
		super(game);
		this.aispeed = aispeed;
	}
	
	public void setEndPoints(MazeCell begi, MazeCell end) {
		setStatus(begi, Bot.VISITED);
		this.end = end;
	}
	
	private boolean solveStep() {
		if (game.isOn()) {
			Stack<MazeCell> tex = game.getTex();
			if (tex.peek() == end) {
				// Bot wins if at end
				game.botWin(Player.P1); 
				return false;
			}
			// Priority queue of direction choices
			Direction[] dirspq = SolveBot.getBestDir(tex.peek(), end);
			for (int i = 0; i < 4; i++) {
				MazeCell option = game.getNeighbor(tex.peek(), dirspq[i]);
				// If cell enterable and unvisited, advance
				if (option != null
						&& !(tex.peek().isBlockedDir(dirspq[i]))
						&& getStatus(option) == Bot.BLANK) {
					tex.push(option);
					option.setPly(Player.P1, null);
					setStatus(option, Bot.VISITED);
					return true;
				}
			}
			// If not able to move in any direction, move backwards
			MazeCell gone = tex.pop();
			setStatus(gone, Bot.DEAD);
			gone.setPly(null, null);
			return true;
		}
		return false;
	}
	
	// Returns a set of directions in order of importance
	private static Direction[] getBestDir(MazeCell orig, MazeCell dest) {
			Direction[] moves = new Direction[4];
			int yDis = dest.row() - orig.row();
			int xDis = dest.col() - orig.col();
			// If closer in y than in x
			if (Math.abs(xDis) <= Math.abs(yDis)) {
				if (yDis <= 0) {
					moves[0] = Direction.UP;
					moves[2] = Direction.DOWN;
				} else {
					moves[0] = Direction.DOWN;
					moves[2] = Direction.UP;
				}
				if (xDis <= 0) {
					moves[1] = Direction.RIGHT;
					moves[3] = Direction.LEFT;
				} else {
					moves[1] = Direction.RIGHT;
					moves[3] = Direction.LEFT;
				}
			} else {
				if (xDis <= 0) {
					moves[0] = Direction.RIGHT;
					moves[3] = Direction.LEFT;
				} else {
					moves[0] = Direction.RIGHT;
					moves[3] = Direction.LEFT;
				}
				if (yDis <= 0) {
					moves[1] = Direction.UP;
					moves[2] = Direction.DOWN;
				} else {
					moves[1] = Direction.DOWN;
					moves[2] = Direction.UP;
				}
			}
			return moves;
		}
	

	@Override
	public void run()
	{
		while(solveStep())
			pause(aispeed);
		
	}
	
	private void pause(int t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {}
	}
}
