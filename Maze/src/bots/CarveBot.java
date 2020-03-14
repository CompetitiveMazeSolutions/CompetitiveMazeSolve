package bots;

import java.util.List;
import java.util.Stack;
import engine.MazeCell;
import engine.MazeFrame;

public class CarveBot extends Bot
{
	private Stack<MazeCell> tex;
	public CarveBot(MazeFrame game, MazeCell begi)
	{
		super(game);
		tex = new Stack<>();
		tex.push(begi);
		setStatus(begi, VISITED);
	}
	
	private void stepCarve() {
		List<MazeCell> bop = blankNeighbors(tex.peek());
		if (bop.isEmpty()) {
			// Back up boi
			setStatus(tex.pop(), Bot.DEAD);
		} else {
			// Pick a choice and go in
			MazeCell chosen1 = bop.get(0);
			tex.peek().clearWallDir(tex.peek().directionTo(chosen1));
			chosen1.clearWallDir(chosen1.directionTo(tex.peek()));
			setStatus(chosen1, Bot.VISITED);
			tex.push(chosen1);
			// Go back out occasionally
			if (Math.random() > game.mazeFidelity)
				setStatus(tex.pop(), Bot.BLANK);
		}
	}

	public void carveMaze() {
		while(!tex.empty())
			stepCarve();
	}
}
