package engine;

public class Main {

	public static void main(String[] args) {
		// Starting defaults for values and mode
		final int mode = MazeFrame.TT;
		final double mazeFidelity = .7;
		final String matchName = null;

		new MazeFrame(mode, mazeFidelity, matchName);
	}
}
