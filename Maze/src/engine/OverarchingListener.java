package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class OverarchingListener implements KeyListener {

	private MazeFrame parent;
	private int mode;
	private boolean p1C;
	private boolean p2C;
	private boolean p1R;
	private boolean p2R;

	public OverarchingListener(MazeFrame parent) {
		this.parent = parent;
		mode = parent.getMode();
		p1C = false;
		p2C = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {

		switch (e.getKeyCode()) {
		case KeyEvent.VK_SHIFT:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
				p1R = true;
			} else {
				p2R = true;
			}
			break;
		case KeyEvent.VK_CONTROL:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) {
				p1C = true;
			} else {
				p2C = true;
			}
			break;
		}
		if (p1C || p2C) {
			parent.resetMaze();
		}
		if (p1R && p2R) {
			parent.getSolve().requestFocus();
			for (MazeCell[] out : parent.getCells())
				for (MazeCell in : out)
					in.go();
			parent.setStartTime((int) (System.currentTimeMillis()));
			if (mode == MazeFrame.CPU)
				new Thread(parent).start();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SHIFT:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
				p1R = false;
			} else {
				p2R = false;
			}
			break;
		case KeyEvent.VK_CONTROL:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
				p1C = false;
			} else {
				p2C = false;
			}
			break;
		}
	}
}
