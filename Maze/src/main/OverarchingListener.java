package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class OverarchingListener implements KeyListener {

	private MazeFrame parent;

	public OverarchingListener(MazeFrame parent) {
		this.parent = parent;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SHIFT:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
				parent.setP1R(true);
			} else {
				parent.setP2R(true);
			}
			if (parent.getMode() == MazeFrame.CPU) {
				(new Thread(parent)).start();
			}
			break;
		case KeyEvent.VK_CONTROL:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
				parent.setP1C(true);
			} else {
				parent.setP2C(true);
			}
			break;
		}
		if (parent.isP1C() && parent.isP2C()) {
			parent.resetMaze();
		}
		if (parent.isP1R() && parent.isP2R()) {
			parent.getSolve().requestFocus();
			for (MazeCell[] out : parent.getCells())
				for (MazeCell in : out)
					in.go();
			parent.setStartTime((int) (System.currentTimeMillis()));
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SHIFT:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
				parent.setP1R(false);
			} else {
				parent.setP2R(false);
			}
			break;
		case KeyEvent.VK_CONTROL:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
				parent.setP1C(false);
			} else {
				parent.setP2C(false);
			}
			break;
		}
	}
}
