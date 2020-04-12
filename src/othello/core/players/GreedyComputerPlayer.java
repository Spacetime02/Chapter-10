package othello.core.players;

import java.util.Arrays;

import othello.core.Othello;
import othello.core.Position;
import othello.util.tuple.IntPair;

public class GreedyComputerPlayer extends ComputerPlayer {

	public GreedyComputerPlayer(String name, int delay) {
		super(name, delay);
	}

	private static boolean[][] copyGrid(boolean[][] grid) {
		int gridSize = grid.length;

		boolean[][] copyGrid = new boolean[gridSize][];

		for (int i = 0; i < gridSize; i++)
			copyGrid[i] = Arrays.copyOf(grid[i], gridSize);

		return copyGrid;
	}

	@Override
	protected Position computeMove(boolean[][] curGrid, boolean[][] takenGrid, int curScore, int oppScore, String playerName) {
		Position[] valid = Othello.getValidMoves(curGrid, takenGrid);

		if (valid.length == 1)
			return valid[0];

		Position bestPos = null;

		int bestVal = 0;

		for (Position movePos : valid) {
			boolean[][] curGridCopy   = copyGrid(curGrid);
			boolean[][] takenGridCopy = copyGrid(takenGrid);

			IntPair scores = Othello.simulateMove(curGridCopy, takenGridCopy, movePos, curScore, oppScore);

			int val = scores.first - scores.second;

			if (bestPos == null || val > bestVal) {
				bestPos = movePos;
				bestVal = val;
			}
		}

		return bestPos;
	}

}
