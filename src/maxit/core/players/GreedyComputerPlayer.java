package maxit.core.players;

import maxit.core.MAXIT;
import maxit.core.Position;

public class GreedyComputerPlayer extends ComputerPlayer {

	public GreedyComputerPlayer(String name) {
		super(name);
	}

	@Override
	protected Position computeMove(int[][] valueGrid, boolean[][] takenGrid, Position currentPos, boolean horizontal, int score, int oppScore, String playerName) {
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
