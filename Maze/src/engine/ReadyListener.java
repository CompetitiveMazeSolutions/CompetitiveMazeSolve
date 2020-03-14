package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import consts.*;

public class ReadyListener implements KeyListener {

	private MazeFrame parent;
	private Mode mode;
	private boolean p1C,p1R,p2C,p2R;
	private boolean[] teamreach = {false,false,false,false};

	public ReadyListener(MazeFrame parent) {
		this.parent = parent;
		mode = parent.getMode();
		p1C = false;
		p1R = false;
		p2C = false;
		p2R = false;

	}

	private void processKeys() {
		if (p1R && p2R) {
			parent.startGame();
		}

		if (p1C && p2C) {
			parent.resetMaze();
		}
	}
	
	private void setTeamReach(int i) {
		teamreach[i] = true;
		if (teamreach[i^1])
			parent.teamWin((i>>1) + 1);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (!parent.isOn() && (keyCode != KeyEvent.VK_CONTROL
				&& keyCode != KeyEvent.VK_SHIFT)) {
			return;
		}

		switch(mode) {
		case CPU:
			switch (keyCode) {
			case KeyEvent.VK_RIGHT:
				parent.playerMove(Direction.RIGHT);
				break;
			case KeyEvent.VK_LEFT:
				parent.playerMove(Direction.LEFT);
				break;
			case KeyEvent.VK_DOWN:
				parent.playerMove(Direction.DOWN);
				break;
			case KeyEvent.VK_UP:
				parent.playerMove(Direction.UP);
				break;
			case KeyEvent.VK_CONTROL:
				parent.resetMaze();
				break;
			case KeyEvent.VK_SHIFT:
				if (!parent.isOn() && parent.getMatchTime() == 0) {
					parent.startGame();
				}
				break;
			}
			break;
		case V2:
			switch (keyCode) {
			case KeyEvent.VK_RIGHT :
				parent.playerMove(Player.P2, Direction.RIGHT);
				break;
			case KeyEvent.VK_LEFT :
				MazeCell head = parent.getMex().peek();
				if (head.col() == 0
						&& !head.isBlockedDir(Direction.LEFT))
				{
					parent.versusWin(Player.P2);
					return;
				}
				if (head.col() == 1
						&& parent.getNeighbor(head, Direction.LEFT).getPly() == Player.P1)
				{
					parent.versusWin(Player.P2);
					return;
				}
				parent.playerMove(Player.P2, Direction.LEFT);
				break;
			case KeyEvent.VK_DOWN :
				parent.playerMove(Player.P2, Direction.DOWN);
				break;
			case KeyEvent.VK_UP :
				parent.playerMove(Player.P2, Direction.UP);
				break;
			case KeyEvent.VK_D:
				head = parent.getTex().peek();
				if (head.col() == parent.getColumns() - 1
						&& !head.isBlockedDir(Direction.RIGHT))
				{
					parent.versusWin(Player.P1);
					return;
				}
				if (head.col() == parent.getColumns() - 2
						&& parent.getNeighbor(head, Direction.RIGHT).getPly() == Player.P2)
				{
					parent.versusWin(Player.P1);
					return;
				}
				parent.playerMove(Player.P1, Direction.RIGHT);
				break;
			case KeyEvent.VK_A:
				parent.playerMove(Player.P1, Direction.LEFT);
				break;
			case KeyEvent.VK_S:
				parent.playerMove(Player.P1, Direction.DOWN);
				break;
			case KeyEvent.VK_W:
				parent.playerMove(Player.P1, Direction.UP);
				break;
			case KeyEvent.VK_CONTROL:
				if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) {
					p1C = true;
				} else {
					p2C = true;
				}
				break;
			case KeyEvent.VK_SHIFT:
				if (!parent.isOn() && parent.getMatchTime() == 0) {
					if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) {
						p1R = true;
					} else {
						p2R = true;
					}
				}
				break;
			}
			break;
		case TT:
			switch (keyCode) {
			case KeyEvent.VK_RIGHT:
				parent.playerMove(Player.P2, Direction.RIGHT);
				break;
			case KeyEvent.VK_LEFT:
				MazeCell head = parent.getMex().peek();
				if (head.col() == 0
						&& !head.isBlockedDir(Direction.LEFT))
				{
					parent.versusWin(Player.P2);
					return;
				}
				if (head.col() == 1
						&& parent.getNeighbor(head, Direction.LEFT).getPly() == Player.P2)
				{
					parent.versusWin(Player.P2);
					return;
				}
				parent.playerMove(Player.P2, Direction.LEFT);
				break;
			case KeyEvent.VK_DOWN:
				parent.playerMove(Player.P2, Direction.DOWN);
				break;
			case KeyEvent.VK_UP:
				parent.playerMove(Player.P2, Direction.UP);
				break;
			case KeyEvent.VK_CONTROL:
				parent.resetMaze();
				break;
			case KeyEvent.VK_SHIFT:
				if (!parent.isOn() && parent.getMatchTime() == 0) {
					parent.startGame();
				}
				break;
			}
			break;
		case T4:
			switch (keyCode) {
			case KeyEvent.VK_CONTROL:
				if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) {
					p1C = true;
				} else {
					p2C = true;
				}
				break;
			case KeyEvent.VK_SHIFT:
				if (!parent.isOn() && parent.getMatchTime() == 0) {
					if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) {
						p1R = true;
					} else {
						p2R = true;
					}
				}
				break;
			}

			KeyInfo ki = MazeFrame.KEYS.get(keyCode);
			if (ki == null) break;
			Player ply = ki.player;
			Player tmate = ply.teammate();
			int tm = ply.ordinal() >> 1;
			
			MazeCell mc = parent.getStex(ply.ordinal()).peek();
			MazeCell next = parent.getNeighbor(mc, ki.direction);
			Player nxPly = null;
			if (next != null) nxPly = next.getPly();
			
			switch (ki.direction)
			{
			case RIGHT :
				int cols = parent.getColumns();
				if (tm == 0 && ((mc.col() == cols - 1
						&& !mc.isBlockedDir(Direction.RIGHT))
						|| (mc.col() == cols - 2 && nxPly != ply
								&& nxPly != null)
						|| (nxPly == tmate && parent.boostEnd(next, tm + 1,
								Direction.RIGHT) == null)))
					setTeamReach(ply.ordinal());
				else
					parent.teamPlayerMove(ply, Direction.RIGHT);
				break;
			case LEFT :
				if (tm == 1 && ((mc.col() == 0
						&& !mc.isBlockedDir(Direction.LEFT))
						|| (mc.col() == 1 && nxPly != ply && nxPly != null)
						|| (nxPly == tmate && parent.boostEnd(next, tm + 1,
								Direction.LEFT) == null)))
					setTeamReach(ply.ordinal());
				else
					parent.teamPlayerMove(ply, Direction.LEFT);
				break;
			case UP :
			case DOWN :
				parent.teamPlayerMove(ply, ki.direction);
				break;
			}
			break;
		}


		if (mode == Mode.V2 || mode == Mode.T4)
			processKeys();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_CONTROL:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) {
				p1C = false;
			} else {
				p2C = false;
			}
			break;
		case KeyEvent.VK_SHIFT:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) {
				p1R = false;
			} else {
				p2R = false;
			}
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
