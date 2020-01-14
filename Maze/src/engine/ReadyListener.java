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


	private boolean[] teamreach;

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

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (!parent.isOn() && (keyCode != KeyEvent.VK_CONTROL
				&& keyCode != KeyEvent.VK_SHIFT)) {
			return;
		}

		if (mode == Mode.CPU) {
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

		} else if (mode == Mode.V2) {
			switch (keyCode) {
			case KeyEvent.VK_RIGHT :
				parent.playerMove(2, MazeFrame.RIGHT);
				break;
			case KeyEvent.VK_LEFT :
				if (parent.getMex().peek().col() == 0
						&& !parent.getMex().peek().isBlockedDir(MazeFrame.LEFT))
				{
					parent.win(2);
					return;
				}
				if (parent.getMex().peek().col() == 1 && parent
						.getNeighbor(parent.getMex().peek(), MazeFrame.LEFT)
						.getPly() == 1)
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
				if (parent.getTex().peek().col() == parent.getColumns() - 1
						&& !parent.getTex().peek()
								.isBlockedDir(MazeFrame.RIGHT))
				{
					parent.win(1);
					return;
				}
				if (parent.getTex().peek().col() == parent.getColumns() - 2
						&& parent.getNeighbor(parent.getTex().peek(),
								MazeFrame.RIGHT).getPly() == 2)
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
		} else if (mode == Mode.TT) {
			switch (keyCode) {
			case KeyEvent.VK_RIGHT:
				parent.playerMove(2, MazeFrame.RIGHT);
				break;
			case KeyEvent.VK_LEFT:
				if (parent.getMex().peek().col() == 0
						&& !parent.getMex().peek().isBlockedDir(MazeFrame.LEFT))
				{
					parent.win(2);
					return;
				}
				if (parent.getMex().peek().col() == 1 && parent
						.getNeighbor(parent.getMex().peek(), MazeFrame.LEFT)
						.getPly() == 2)
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
		} else if (mode == Mode.T4) {

			switch(keyCode){
			case KeyEvent.VK_CONTROL:
				parent.resetMaze();
				break;
			case KeyEvent.VK_SHIFT:
				if (!parent.isOn() && parent.getMatchTime() == 0) {
					if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) {
						p1R = true;
					} else {
						p2R = true;
					}
				}
				break;}


			for(int i=0; i<4; i++){

				if (keyCode == MazeFrame.KEYS[i][MazeFrame.RIGHT]){
					if (parent.getStex(i).peek().col() == parent.getColumns() - 1
							&& !parent.getStex(i).peek()
									.isBlockedDir(MazeFrame.RIGHT))
					{
						teamreach[i]=true;
						return;
					}
					/*if (parent.getTex().peek().col() == parent.getColumns() - 2
							&& parent.getNeighbor(parent.getTex().peek(),
									MazeFrame.RIGHT).getPly() == 2)
					{
						teamreach[i]=true;
						return;
					}*/
					parent.playerMove(i/2+1, i%2+1, MazeFrame.RIGHT);
				}else if ((keyCode == MazeFrame.KEYS[i][MazeFrame.LEFT])){
					if (parent.getStex(i).peek().col() == 0
							&& !parent.getStex(i).peek().isBlockedDir(MazeFrame.LEFT))
					{
						teamreach[i]=true;
						return;
					}
					/*if (parent.getMex().peek().col() == 1 && parent
							.getNeighbor(parent.getMex().peek(), MazeFrame.LEFT)
							.getPly() == 2)
					{
						teamreach[i]=true;
						return;
					}*/
					parent.playerMove(i/2+1, i%2+1, MazeFrame.LEFT);
				}else if ((keyCode == MazeFrame.KEYS[i][MazeFrame.DOWN])){
					parent.playerMove(i/2+1, i%2+1, MazeFrame.DOWN);
				}else if ((keyCode == MazeFrame.KEYS[i][MazeFrame.UP])){
					parent.playerMove(i/2+1, i%2+1, MazeFrame.UP);
				}
				}
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
