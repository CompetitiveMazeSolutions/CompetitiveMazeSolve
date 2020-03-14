package bots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import consts.Direction;
import consts.Player;
import engine.MazeCell;
import engine.MazeFrame;

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
			// If bot is at the end
			if (tex.peek() == end) {
				// Bot wins
				game.botWin(Player.P1); 
				return false;
			}

			// Priority queue of direction choices
			Direction[] dirspq = MazeFrame.getBestDir(tex.peek(), end);
			// If neither win conditions are met, move on.
			for (int i = 0; i < 4; i++) {
				MazeCell option = game.getNeighbor(tex.peek(), dirspq[i]);
				// If cell enterable and unvisited
				if (option != null
						&& !(tex.peek().isBlockedDir(dirspq[i]))
						&& getStatus(option) == Bot.BLANK) {
					// Move into cell
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
	
	

	@Override
	public void run()
	{
		while(solveStep())
			pause(aispeed);
		
	}
	
	private void pause(int t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
		}
	}
}
