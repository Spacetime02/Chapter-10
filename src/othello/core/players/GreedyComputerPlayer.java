package othello.core.players;

import othello.core.Othello;
import othello.core.Position;

public class GreedyComputerPlayer extends ComputerPlayer {

	public GreedyComputerPlayer(String name) {
		super(name);
	}

	@Override
	protected Position computeMove(boolean[][] whiteGrid, boolean[][] takenGrid, boolean isWhite, String playerName) {
		Position[] valid = Othello.getValidMoves(takenGrid, currentPos, horizontal);

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
