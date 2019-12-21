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
			}
		} else {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				parent.playerMove(2, MazeFrame.RIGHT);
				break;
			case KeyEvent.VK_LEFT:
				if (parent.getMex().peek().col() == 0 && !parent.getMex().peek().isBlockedDir(MazeFrame.LEFT)) {
					parent.win(2);
					break;
				}
				if (parent.getMex().peek().col() == 1
						&& parent.getNeighbor(parent.getMex().peek(), MazeFrame.LEFT).getPly() == 1) {
					parent.win(2);
					break;
				}
				parent.playerMove(2, MazeFrame.LEFT);
				break;
			case KeyEvent.VK_DOWN:
				parent.playerMove(2, MazeFrame.DOWN);
				/*
				 * if (mex.peek().row() == ROWS - 1) p2b = true; if (p2t && p2b) { for (int i =
				 * 0; i < ROWS / 5; i++) mex.pop().setPly(0, null); p2b = false; }
				 */
				break;
			case KeyEvent.VK_UP:
				parent.playerMove(2, MazeFrame.UP);
				/*
				 * if (mex.peek().row() == 0) p2t = true; if (p2t && p2b) { for (int i = 0; i <
				 * ROWS / 5; i++) mex.pop().setPly(0, null); p2t = false; }
				 */
				break;
			case KeyEvent.VK_D:
				if (parent.getTex().peek().col() == MazeFrame.COLS - 1
						&& !parent.getTex().peek().isBlockedDir(MazeFrame.RIGHT)) {
					parent.win(1);
					break;
				}
				if (parent.getTex().peek().col() == MazeFrame.COLS - 2
						&& parent.getNeighbor(parent.getTex().peek(), MazeFrame.RIGHT).getPly() == 2) {
					parent.win(1);
					break;
				}
				parent.playerMove(1, MazeFrame.RIGHT);
				break;
			case KeyEvent.VK_A:
				parent.playerMove(1, MazeFrame.LEFT);
				break;
			case KeyEvent.VK_S:
				parent.playerMove(1, MazeFrame.DOWN);
				/*
				 * if (tex.peek().row() == ROWS - 1) p1b = true; if (p1t && p1b) { for (int i =
				 * 0; i < ROWS / 5; i++) tex.pop().setPly(0, null); p1b = false; }
				 */
				break;
			case KeyEvent.VK_W:
				parent.playerMove(1, MazeFrame.UP);
				/*
				 * if (tex.peek().row() == 0) p1t = true; if (p1t && p1b) { for (int i = 0; i <
				 * ROWS / 5; i++) tex.pop().setPly(0, null); p1t = false;
				 *
				 * }
				 */
				break;
			case KeyEvent.VK_CONTROL:
				if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
					p1C = true;
				} else {
					p2C = true;
				}
				break;
			}
			if (p1C && p2C) {
				parent.resetMaze();
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {

		switch (e.getKeyCode()) {
		case KeyEvent.VK_CONTROL:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
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
