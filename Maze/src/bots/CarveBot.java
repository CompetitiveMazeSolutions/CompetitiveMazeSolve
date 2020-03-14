package bots;

import java.util.ArrayList;
import java.util.Stack;
import engine.*;

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
		// Create a new array of neighbors
		ArrayList<MazeCell> bop = blankNeighbors(tex.peek());
		// If no options
		if (bop.isEmpty()) {
			// Back up boi
			setStatus(tex.pop(), Bot.DEAD);
		} else {
			// Pick a random choice
			MazeCell chosen1 = bop.get(0);
			// Clear walls to new cell and from next space to this one
			tex.peek().clearWallDir(MazeFrame.getDirectionFrom(tex.peek(), chosen1));
			chosen1.clearWallDir(MazeFrame.getDirectionFrom(chosen1, tex.peek()));
			// Make the new cell visited
			setStatus(chosen1, Bot.VISITED);
			// Push it to carve stack
			tex.push(chosen1);
			// Randomly reset new space and make it reachable again
			if (Math.random() > game.mazeFidelity)
				setStatus(tex.pop(), Bot.BLANK);
		}
	}

	public void carveMaze() {
		while(!tex.empty())
			stepCarve();
	}
}
