package othello.core.players;

import java.util.function.Supplier;

import othello.core.Position;

public abstract class Player {

	private final String name;

	public Player(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract Position move(int[][] valueGrid, boolean[][] takenGrid, Position currentPos, boolean horizontal, int score, int oppScore, String playerName, Supplier<Position> userInput);

}
