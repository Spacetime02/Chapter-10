package maxit.core;

import java.util.Random;
import java.util.function.Supplier;

public class RandomComputerPlayer extends ComputerPlayer {

	private static final Random RANDY = new Random();

	public RandomComputerPlayer(String name) {
		super(name);
	}

	@Override
	protected Position computeMove(int[][] valueGrid, boolean[][] takenGrid, Position currentPos, boolean horizontal, int score, int oppScore, Supplier<Position> userInput) {
		Position[] valid = MAXIT.getValidMoves(takenGrid, currentPos, horizontal);
		return valid[RANDY.nextInt(valid.length)];
	}

}
