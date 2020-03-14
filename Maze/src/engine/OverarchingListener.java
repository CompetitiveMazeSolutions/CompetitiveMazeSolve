package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.BiPredicate;
import consts.Mode;

public class OverarchingListener implements KeyListener {

	private MazeFrame parent;
	private BiPredicate<Boolean, Boolean> startCondition;
	private boolean p1C, p2C, p1R, p2R;

	public OverarchingListener(MazeFrame parent) {
		this.parent = parent;
		switch (parent.getMode()) {
		case CPU: case TT:
			startCondition = (a,b) -> a || b;
			break;
		case V2: case T4:
			startCondition = (a,b) -> a && b;
			break;
		}
		p1C = false;
		p2C = false;
		p1R = false;
		p2R = false;
	}

	private void processKeys() {
		if (p1C || p2C) {
			parent.resetMaze();
		}

		if (startCondition.test(p1R, p2R)) {
			parent.startGame();
			parent.getButton(1).requestFocus();
		} 
	}

	@Override
	public void keyPressed(KeyEvent e) {

		switch (e.getKeyCode()) {
		case KeyEvent.VK_SHIFT:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) {
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

		processKeys();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SHIFT:
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) {
				p1R = false;
			} else {
				p2R = false;
			}
			break;
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
	}
}
