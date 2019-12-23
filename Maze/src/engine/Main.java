package engine;

public class Main {

	public static void main(String[] args) {
		// Starting defaults for values and mode
		final Mode mode = Mode.TT;
		final double mazeFidelity = .7;
		final String matchName = null;
		final int rows = 20;
		final int cols = 35;

		new MazeFrame(mode, mazeFidelity, matchName, rows, cols);
	}
}
