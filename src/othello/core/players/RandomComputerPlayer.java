package othello.core.players;

import java.util.Random;

import othello.core.Othello;
import othello.core.Position;

public class RandomComputerPlayer extends ComputerPlayer {

	private static final Random RANDY = new Random();

	public RandomComputerPlayer(String name, int delay) {
		super(name, delay);
	}

	@Override
	protected Position computeMove(boolean[][] curGrid, boolean[][] takenGrid, int curScore, int oppScore, String playerName) {
		Position[] valid = Othello.getValidMoves(curGrid, takenGrid);
		return valid[RANDY.nextInt(valid.length)];
	}

}
