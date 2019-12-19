package FORKIDS;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class Settings extends JPanel implements ActionListener, MouseListener {

	private MazeFrame parent;
	private JButton exit;

	public Settings(MazeFrame parent) {
		super();
		this.parent = parent;
		Color backgroundColor = new Color(parent.getBackground().getRed() + 80, parent.getBackground().getGreen() + 80,
				parent.getBackground().getBlue() + 80, 200);

		exit = new JButton("Exit");
		exit.setLocation(parent.getLocation());
		exit.setSize(parent.getWidth() / 20, parent.getHeight() / 20);
		exit.setForeground(Color.WHITE);
		exit.setBackground(backgroundColor);
		exit.addActionListener(this);

		setLayout(null);
		setPreferredSize(parent.getSize());
		setBackground(parent.getBackground());
		add(exit);
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
		if (e.getSource() == exit) {
			parent.resetMaze();
		}

	}
}
