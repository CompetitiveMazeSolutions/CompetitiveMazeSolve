package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ReadyListener implements KeyListener {

	private MazeFrame parent;
	private boolean p1C;
	private boolean p2C;

	public ReadyListener(MazeFrame parent) {
		this.parent = parent;
		p1C = false;
		p2C = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!parent.isOn() && e.getKeyCode() != (KeyEvent.VK_CONTROL))
			return;

		if (parent.getMode() == MazeFrame.CPU) {
			switch (e.getKeyCode()) {
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
				p2C = true;
				break;
			}
			if (p2C) {
				parent.resetMaze();
			}

		} else if (parent.getMode() == MazeFrame.P2){
			switch (e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				parent.playerMove(2, MazeFrame.RIGHT);
				break;
			case KeyEvent.VK_LEFT:
				if (parent.getMex().peek().col() == 0 && !parent.getMex().peek().isBlockedDir(MazeFrame.LEFT)) {
					parent.win(2);
					return;
				}
				if (parent.getMex().peek().col() == 1
						&& parent.getNeighbor(parent.getMex().peek(), MazeFrame.LEFT).getPly() == 1) {
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
			case KeyEvent.VK_D:
				if (parent.getTex().peek().col() == MazeFrame.COLS - 1
						&& !parent.getTex().peek().isBlockedDir(MazeFrame.RIGHT)) {
					parent.win(1);
					return;
				}
				if (parent.getTex().peek().col() == MazeFrame.COLS - 2
						&& parent.getNeighbor(parent.getTex().peek(), MazeFrame.RIGHT).getPly() == 2) {
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
			}
			if (p1C && p2C) {
				parent.resetMaze();
			}
		} else if (parent.getMode() == MazeFrame.TT){
			switch (e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				parent.playerMove(2, MazeFrame.RIGHT);
				break;
			case KeyEvent.VK_LEFT:
				if (parent.getMex().peek().col() == 0 && !parent.getMex().peek().isBlockedDir(MazeFrame.LEFT)) {
					parent.win(2);
					return;
				}
				if (parent.getMex().peek().col() == 1
						&& parent.getNeighbor(parent.getMex().peek(), MazeFrame.LEFT).getPly() == 1) {
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
		}
		}

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
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
