package bots;

import java.util.ArrayList;
import java.util.Arrays;
import consts.Direction;
import engine.MazeCell;
import engine.MazeFrame;

public abstract class Bot
{
	public static final int BLANK = 0, VISITED = 1, DEAD = 2;
	
	protected MazeFrame game;
	// used for marking cells internally
	private int[][] status;
	
	public Bot(MazeFrame game) {
		this.game = game;
		this.status = new int[game.getRows()][game.getColumns()];
	}
	
	protected ArrayList<MazeCell> blankNeighbors(MazeCell mc) {
		ArrayList<MazeCell> results = new ArrayList<MazeCell>();
		// list out directions
		ArrayList<Direction> dirs = new ArrayList<Direction>(Arrays.asList(Direction.values()));

		if (mc.row() == 0) { // if on ceiling (must prioritize leaving)
			enlistNeighborsInto(results, mc, dirs.remove(2)); // remove down from possible, add to list as priority
		} else if (mc.row() == game.getRows() - 1) { // if on floor (must prioritize leaving)
			enlistNeighborsInto(results, mc, dirs.remove(0)); // remove up from possible, add to list as priority
		}
		while (dirs.size() > 0) { // add each of dirs to results in random order
			int chosenIndex = (int) (Math.random() * dirs.size());
			enlistNeighborsInto(results, mc, dirs.remove(chosenIndex));
		}

		return results;
	}

	// Add neighbor to results if possible
	private void enlistNeighborsInto(ArrayList<MazeCell> results, MazeCell mc, Direction dir) {
		MazeCell inQuestion = game.getNeighbor(mc, dir);
		// if it is real, and blank
		if (inQuestion != null && getStatus(inQuestion) == Bot.BLANK)
			results.add(inQuestion);
	}
	
	
	public int getStatus(MazeCell mc) {
		return status[mc.row()][mc.col()];
	}
	
	protected void setStatus(MazeCell mc, int nStatus) {
		status[mc.row()][mc.col()] = nStatus;
	}
}
