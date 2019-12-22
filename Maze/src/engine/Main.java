package engine;

public class Main {

	public static void main(String[] args) {
		// Starting defaults for values and mode
		final int mode = MazeFrame.P2;
		final double mazeFidelity = .7;

		new MazeFrame(mode, mazeFidelity);
	}
}
