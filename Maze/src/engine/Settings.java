package engine;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class Settings extends JPanel implements ActionListener, MouseListener {

	private MazeFrame parent;
	private JButton[] buttons = { new JButton("Exit") };
	private Color buttonBackground;
	private Mode mode;
	private double mazeFidelity;
	private int aispeed;
	private int stagePreset;
	private String matchName;
	private int r;
	private int c;

	public Settings(MazeFrame parent) {
		super();
		this.parent = parent;
		this.buttonBackground = new Color(Math.min(parent.getBackground().getRed() - 100, 255),
				Math.min(parent.getBackground().getGreen() - 100, 255),
				Math.min(parent.getBackground().getBlue() - 100, 255), 220);

		for (JButton b : buttons) {
			b = new JButton("Exit");
			b.setSize(parent.getWidth() / 20, parent.getHeight() / 20);
			b.setForeground(Color.WHITE);
			b.setBackground(buttonBackground);
			b.addActionListener(this);
			add(b);
		}
		buttons[0].setLocation(parent.getLocation());

		setLayout(null);
		setPreferredSize(parent.getSize());
		setBackground(parent.getBackground());
		setVisible(true);
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
			new MazeFrame(mode, mazeFidelity, aispeed, aispeed, matchName, aispeed, aispeed);

			try {
				Thread.sleep(50 + (r * c) / 1000);
			} catch (Exception e1) {
			}

			parent.setVisible(false);
			System.gc();
		}

	}
}
