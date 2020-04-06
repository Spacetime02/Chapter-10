package maxit.core.players;

import java.util.Random;

import maxit.core.MAXIT;
import maxit.core.Position;

public class RandomComputerPlayer extends ComputerPlayer {

	private static final Random RANDY = new Random();

	public RandomComputerPlayer(String name) {
		super(name);
	}

	@Override
	protected Position computeMove(int[][] valueGrid, boolean[][] takenGrid, Position currentPos, boolean horizontal, int score, int oppScore, String playerName) {
		Position[] valid = MAXIT.getValidMoves(takenGrid, currentPos, horizontal);
		return valid[RANDY.nextInt(valid.length)];
	}

}
