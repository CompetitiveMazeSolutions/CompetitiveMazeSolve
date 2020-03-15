package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import consts.*;

public class ReadyListener implements KeyListener {
	
	private static class KeyInfo {
		final Direction direction;
		final Player player;
		KeyInfo(Direction dir, Player ply) {
			direction = dir;
			player = ply;
		}
	}
	
	private final Map<Integer,KeyInfo> keyMap = new HashMap<>();
	private final MazeFrame parent;
	private boolean p1C,p1R,p2C,p2R;
	private boolean[] teamreach = {false,false,false,false};
	private final Consumer<KeyEvent> pressReaction;

	public ReadyListener(MazeFrame parent) {
		this.parent = parent;
		switch (parent.getMode()) {
		case CPU :
			pressReaction = this::reactInCPU;
			break;
		case T4 :
			pressReaction = this::reactInTeamVersus;
			break;
		case TT :
			pressReaction = this::reactInTimeTrial;
			break;
		case V2 :
			pressReaction = this::reactInVersus;
			break;
		default:
			pressReaction = null;
			break;
		}
		setUpKeyMap(parent.getMode());
		p1C = false;
		p1R = false;
		p2C = false;
		p2R = false;

	}
	
	private void setUpKeyMap(Mode mode) {
		if (mode == Mode.V2 || mode == Mode.T4) {
			keyMap.put(KeyEvent.VK_W, new KeyInfo(Direction.UP, Player.P1));
			keyMap.put(KeyEvent.VK_D, new KeyInfo(Direction.RIGHT, Player.P1));
			keyMap.put(KeyEvent.VK_S, new KeyInfo(Direction.DOWN, Player.P1));
			keyMap.put(KeyEvent.VK_A, new KeyInfo(Direction.LEFT, Player.P1));
		}
		if (mode != Mode.T4) {
			keyMap.put(KeyEvent.VK_UP, new KeyInfo(Direction.UP, Player.P2));
			keyMap.put(KeyEvent.VK_RIGHT, new KeyInfo(Direction.RIGHT, Player.P2));
			keyMap.put(KeyEvent.VK_DOWN, new KeyInfo(Direction.DOWN, Player.P2));
			keyMap.put(KeyEvent.VK_LEFT, new KeyInfo(Direction.LEFT, Player.P2));
		} else {
			keyMap.put(KeyEvent.VK_I, new KeyInfo(Direction.UP, Player.P2));
			keyMap.put(KeyEvent.VK_L, new KeyInfo(Direction.RIGHT, Player.P2));
			keyMap.put(KeyEvent.VK_K, new KeyInfo(Direction.DOWN, Player.P2));
			keyMap.put(KeyEvent.VK_J, new KeyInfo(Direction.LEFT, Player.P2));
			
			keyMap.put(KeyEvent.VK_UP, new KeyInfo(Direction.UP, Player.P3));
			keyMap.put(KeyEvent.VK_RIGHT, new KeyInfo(Direction.RIGHT, Player.P3));
			keyMap.put(KeyEvent.VK_DOWN, new KeyInfo(Direction.DOWN, Player.P3));
			keyMap.put(KeyEvent.VK_LEFT, new KeyInfo(Direction.LEFT, Player.P3));
			
			keyMap.put(KeyEvent.VK_NUMPAD8, new KeyInfo(Direction.UP, Player.P4));
			keyMap.put(KeyEvent.VK_NUMPAD6, new KeyInfo(Direction.RIGHT, Player.P4));
			keyMap.put(KeyEvent.VK_NUMPAD5, new KeyInfo(Direction.DOWN, Player.P4));
			keyMap.put(KeyEvent.VK_NUMPAD4, new KeyInfo(Direction.LEFT, Player.P4));
		}
	}

	private void processKeys() {
		if (p1R && p2R) {
			parent.startGame();
		}

		if (p1C && p2C) {
			parent.resetMaze();
		}
	}
	
	private void setTeamReach(Player p) {
		teamreach[p.ordinal()] = true;
		if (teamreach[p.teammate().ordinal()])
			parent.teamWin(p);
	}
	
	private void reactInCPU(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_CONTROL) {
			parent.resetMaze();
		} else if (keyCode == KeyEvent.VK_SHIFT) {
			if (!parent.isOn() && parent.getMatchTime() == 0)
				parent.startGame();
		} else {
			KeyInfo ki = keyMap.get(keyCode);
			if (ki == null) return;
			parent.playerMove(ki.direction);
		}
	}
	
	private void reactInVersus(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_CONTROL) {
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) 
				p1C = true;
			else 
				p2C = true;
			processKeys();
		} else if (keyCode == KeyEvent.VK_SHIFT) {
			if (!parent.isOn() && parent.getMatchTime() == 0) {
				if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT)
					p1R = true;
				else
					p2R = true;
			}
			processKeys();
		} else {
			KeyInfo ki = keyMap.get(keyCode);
			if (ki == null) return;
			if (ki.direction == Direction.RIGHT && ki.player == Player.P1) {
				MazeCell head = parent.getTex().peek();
				int right = parent.getColumns() - 1;
				if ((head.col() == right && !head.isBlockedDir(Direction.RIGHT))
					||
					(head.col() == right - 1 && parent.getNeighbor(head, Direction.RIGHT).getPly() == Player.P2))
				{
					parent.versusWin(Player.P1);
					return;
				}
			} 
			if (ki.direction == Direction.LEFT && ki.player == Player.P2) {
				MazeCell head = parent.getMex().peek();
				int left = 0;
				if ((head.col() == left && !head.isBlockedDir(Direction.LEFT))
					||
					(head.col() == left + 1 && parent.getNeighbor(head, Direction.LEFT).getPly() == Player.P1))
				{
					parent.versusWin(Player.P2);
					return;
				}
			}
			parent.playerMove(ki.player, ki.direction);
		}
	}
	
	private void reactInTimeTrial(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_CONTROL) {
			parent.resetMaze();
		} else if (keyCode == KeyEvent.VK_SHIFT) {
			if (!parent.isOn() && parent.getMatchTime() == 0)
				parent.startGame();
		} else {
			KeyInfo ki = keyMap.get(keyCode);
			if (ki == null) return;
			if (ki.direction == Direction.LEFT) {
				MazeCell head = parent.getMex().peek();
				int left = 0;
				if ((head.col() == left && !head.isBlockedDir(Direction.LEFT))
					||
					(head.col() == left + 1 && parent.getNeighbor(head, Direction.LEFT).getPly() == Player.P1))
				{
					parent.versusWin(Player.P2);
					return;
				}
			}
			parent.playerMove(Player.P2, ki.direction);
		}
	}
	
	private void reactInTeamVersus(KeyEvent e) {
		int keyCode = e.getKeyCode();
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

		KeyInfo ki = keyMap.get(keyCode);
		if (ki == null) {
			processKeys();
			return;
		}
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
					|| (nxPly == tmate && parent.boostEnd(next, ply,
							Direction.RIGHT) == null)))
				setTeamReach(ply);
			else
				parent.teamPlayerMove(ply, Direction.RIGHT);
			break;
		case LEFT :
			if (tm == 1 && ((mc.col() == 0
					&& !mc.isBlockedDir(Direction.LEFT))
					|| (mc.col() == 1 && nxPly != ply && nxPly != null)
					|| (nxPly == tmate && parent.boostEnd(next, ply,
							Direction.LEFT) == null)))
				setTeamReach(ply);
			else
				parent.teamPlayerMove(ply, Direction.LEFT);
			break;
		case UP :
		case DOWN :
			parent.teamPlayerMove(ply, ki.direction);
			break;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (!parent.isOn() 
			&& keyCode != KeyEvent.VK_CONTROL 
			&& keyCode != KeyEvent.VK_SHIFT)
		{
			return;
		}
		pressReaction.accept(e);
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
