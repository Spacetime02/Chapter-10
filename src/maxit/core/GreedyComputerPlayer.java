package maxit.core;

import java.util.function.Supplier;

public class GreedyComputerPlayer extends ComputerPlayer {

	public GreedyComputerPlayer(String name) {
		super(name);
	}

	@Override
	protected Position computeMove(int[][] valueGrid, boolean[][] takenGrid, Position currentPos, boolean horizontal, int score, int oppScore, Supplier<Position> userInput) {
		Position[] valid = MAXIT.getValidMoves(takenGrid, currentPos, horizontal);

		Position best = null;

		int bestVal = 0;

		for (Position pos : valid) {
			int val = valueGrid[pos.i][pos.j];
			if (best == null || val > bestVal) {
				best = pos;
				bestVal = val;
			}
		}

		return best;
	}

}
