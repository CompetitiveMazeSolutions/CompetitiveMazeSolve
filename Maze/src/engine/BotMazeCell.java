package engine;

import java.awt.Color;
import bots.Bot;
import consts.*;

// Extension onto MazeCell to implement special BotMode Coloring
public class BotMazeCell extends MazeCell
{
	private Color[] colors = { defaultBG, Color.BLUE, Color.BLACK, Color.CYAN }; // Color of each status
	private Color[] colorsP = { Color.YELLOW, Color.GREEN, Color.YELLOW.darker(), Color.ORANGE }; // Colors w/ player
	private boolean pHead, playered, isPath;
	private Color pathColor;
	private Bot bot;
	
	public BotMazeCell(int r, int c, Bot bot)
	{
		super(r, c);
		playered = false;
		pHead = false;
		isPath = false;
		this.bot = bot;
	}
	
	@Override
	protected Color computeHue() {
		if (isPath)
			return new Color(pathColor.getRed(), pathColor.getGreen(), pathColor.getBlue(), 150);
		int status = bot.getStatus(this);
		if (status == Bot.BLANK && getPly() == null) {
			int roll = (int) (Math.random() * 35) + 130;
			return (hide) ? Color.WHITE : new Color(roll, roll, roll);
		}
		if (!playered) {
			// is a bot
			return new Color(colors[status].getRed(), colors[status].getGreen(), colors[status].getBlue(), 150);
		} else {
			if (pHead) {
				pHead = false;
				return colorsP[3];
			}
			return new Color(colorsP[status].getRed(), colorsP[status].getGreen(), colorsP[status].getBlue(), 150);
		}
	}
	
	@Override
	public void setPly(Player ply, Color x) {
		if (ply == Player.P2) {
			pHead = true;
			playered = true;
		}
		if (x != null) {
			isPath = true;
			pathColor = x;
		}
		super.setPly(ply, x);
	}
}
