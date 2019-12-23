package engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Settings extends JFrame implements ActionListener, MouseListener {

	private MazeFrame parent;
	private JPanel UIPanel;
	private JButton[] buttons = { new JButton("Exit") };
	private Color buttonBackground;
	private Mode mode;
	private double mazeFidelity;
	private int aispeed;
	private int stagePreset;
	private String matchName;
	private int rows;
	private int cols;

	public Settings(MazeFrame parent, Mode mode, double mazeFidelity, int aispeed, int stagePreset, String matchName,
			int rows, int cols) {
		super("Settings");
		UIPanel = new JPanel();
		this.parent = parent;
		Color parentBackground = parent.getBackground();
		this.buttonBackground = new Color(Math.max(parentBackground.getRed() - 100, 0),
				Math.max(parentBackground.getGreen() - 100, 0), Math.max(parentBackground.getBlue() - 100, 0), 220);
		this.mode = mode;
		this.mazeFidelity = mazeFidelity;
		this.aispeed = aispeed;
		this.stagePreset = stagePreset;
		this.matchName = matchName;
		this.rows = rows;
		this.cols = cols;

		setLayout(new GridLayout());
		setSize(new Dimension(parent.getWidth() / 2, parent.getHeight() / 2));
		add(UIPanel);
		UIPanel.setSize(getSize());
		UIPanel.setBackground(parent.getBackground());
		UIPanel.setLayout(null);
		for (JButton b : buttons) {
			b.setSize(WIDTH / 5, HEIGHT / 5);
			b.setForeground(Color.WHITE);
			b.setBackground(buttonBackground);
			b.addActionListener(this);
			UIPanel.add(b);
		}
		setVisible(true);
		setFocusable(true);
		requestFocus();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttons[0]) {
			new MazeFrame(mode, mazeFidelity, aispeed, stagePreset, matchName, rows, cols);
			parent.setVisible(false);
			Thread t = parent.getThread();
			if (t != null)
				t.interrupt();
			System.gc();
		}

	}
}
