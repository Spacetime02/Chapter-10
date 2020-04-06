package othello.core.players;

import java.util.Random;

import othello.core.Othello;
import othello.core.Position;

public class RandomComputerPlayer extends ComputerPlayer {

	private static final Random RANDY = new Random();

	public RandomComputerPlayer(String name) {
		super(name);
	}

	@Override
	protected Position computeMove(int[][] valueGrid, boolean[][] takenGrid, Position currentPos, boolean horizontal, int score, int oppScore, String playerName) {
		Position[] valid = Othello.getValidMoves(takenGrid, currentPos, horizontal);
		return valid[RANDY.nextInt(valid.length)];
	}

}
