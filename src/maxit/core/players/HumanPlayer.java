package maxit.core.players;

import java.util.function.Supplier;

import maxit.core.MAXIT;
import maxit.core.Position;

public class HumanPlayer extends Player {

	public HumanPlayer(String name) {
		super(name);
	}

	@Override
	public Position move(int[][] valueGrid, boolean[][] takenGrid, Position currentPos, boolean horizontal, int score, int oppScore, String playerName, Supplier<Position> userInput) {
		Position pos;
		do
			pos = userInput.get();
		while (!MAXIT.isValid(currentPos, takenGrid, horizontal, pos));
		return pos;
	}

}
