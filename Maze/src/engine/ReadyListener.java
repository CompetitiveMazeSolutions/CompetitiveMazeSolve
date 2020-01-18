package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ReadyListener implements KeyListener {

	private MazeFrame parent;
	private Mode mode;
	private boolean p1C;
	private boolean p1R;
	private boolean p2C;
	private boolean p2R;


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
				parent.playerMove(MazeFrame.RIGHT);
				break;
			case KeyEvent.VK_LEFT:
				parent.playerMove(MazeFrame.LEFT);
				break;
			case KeyEvent.VK_DOWN:
				parent.playerMove(MazeFrame.DOWN);
				break;
			case KeyEvent.VK_UP:
				parent.playerMove(MazeFrame.UP);
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
				parent.playerMove(2, MazeFrame.RIGHT);
				break;
			case KeyEvent.VK_LEFT :
				MazeCell head = parent.getMex().peek();
				if (head.col() == 0
						&& !head.isBlockedDir(MazeFrame.LEFT))
				{
					parent.win(2);
					return;
				}
				if (head.col() == 1
						&& parent.getNeighbor(head, MazeFrame.LEFT).getPly() == 1)
				{
					parent.win(2);
					return;
				}
				parent.playerMove(2, MazeFrame.LEFT);
				break;
			case KeyEvent.VK_DOWN :
				parent.playerMove(2, MazeFrame.DOWN);
				break;
			case KeyEvent.VK_UP :
				parent.playerMove(2, MazeFrame.UP);
				break;
			case KeyEvent.VK_D:
				head = parent.getTex().peek();
				if (head.col() == parent.getColumns() - 1
						&& !head.isBlockedDir(MazeFrame.RIGHT))
				{
					parent.win(1);
					return;
				}
				if (head.col() == parent.getColumns() - 2
						&& parent.getNeighbor(head, MazeFrame.RIGHT).getPly() == 2)
				{
					parent.win(1);
					return;
				}
				parent.playerMove(1, MazeFrame.RIGHT);
				break;
			case KeyEvent.VK_A:
				parent.playerMove(1, MazeFrame.LEFT);
				break;
			case KeyEvent.VK_S:
				parent.playerMove(1, MazeFrame.DOWN);
				break;
			case KeyEvent.VK_W:
				parent.playerMove(1, MazeFrame.UP);
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
				parent.playerMove(2, MazeFrame.RIGHT);
				break;
			case KeyEvent.VK_LEFT:
				MazeCell head = parent.getMex().peek();
				if (head.col() == 0
						&& !head.isBlockedDir(MazeFrame.LEFT))
				{
					parent.win(2);
					return;
				}
				if (head.col() == 1
						&& parent.getNeighbor(head, MazeFrame.LEFT).getPly() == 2)
				{
					parent.win(2);
					return;
				}
				parent.playerMove(2, MazeFrame.LEFT);
				break;
			case KeyEvent.VK_DOWN:
				parent.playerMove(2, MazeFrame.DOWN);
				break;
			case KeyEvent.VK_UP:
				parent.playerMove(2, MazeFrame.UP);
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

			for (int i = 0; i < 4; i++)
			{
				int tm = i >> 1;
				int ply = i & 1;
				int tmate = i ^ 1;
				MazeCell mc = parent.getStex(i).peek();
				if (keyCode == MazeFrame.KEYS[i][MazeFrame.RIGHT])
				{
					int cols = parent.getColumns();
					MazeCell next = parent.getNeighbor(mc, MazeFrame.RIGHT);
					int nxPly = 0;
					if (next != null) nxPly = next.getPly();
					if (tm == 0 && ((mc.col() == cols - 1 && !mc.isBlockedDir(MazeFrame.RIGHT))
							|| (mc.col() == cols - 2 && nxPly != i + 1 && nxPly != 0)
							|| (nxPly == tmate + 1 && parent.boostEnd(next, tm + 1, MazeFrame.RIGHT) == null)))
						setTeamReach(i);
					else
						parent.playerMove(tm + 1, ply + 1, MazeFrame.RIGHT);
				} 
				else if ((keyCode == MazeFrame.KEYS[i][MazeFrame.LEFT]))
				{
					MazeCell next = parent.getNeighbor(mc, MazeFrame.LEFT);
					int nxPly = 0;
					if (next != null) nxPly = next.getPly();
					if (tm == 1 && ((mc.col() == 0 && !mc.isBlockedDir(MazeFrame.LEFT))
							|| (mc.col() == 1 && nxPly != i + 1 && nxPly != 0)
							|| (nxPly == tmate + 1 && parent.boostEnd(next, tm + 1, MazeFrame.LEFT) == null)))
						setTeamReach(i);
					else
						parent.playerMove(tm + 1, ply + 1, MazeFrame.LEFT);
				} 
				else if ((keyCode == MazeFrame.KEYS[i][MazeFrame.DOWN]))
					parent.playerMove(tm + 1, ply + 1, MazeFrame.DOWN);
				else if ((keyCode == MazeFrame.KEYS[i][MazeFrame.UP]))
					parent.playerMove(tm + 1, ply + 1, MazeFrame.UP);
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
