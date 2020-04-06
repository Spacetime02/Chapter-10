package maxit.core.players;

import java.util.function.Supplier;

import maxit.core.Position;

public abstract class ComputerPlayer extends Player {

	private static final long MIN_DURATION = 1250L;

	public ComputerPlayer(String name) {
		super(name);
	}

	@Override
	public Position move(int[][] valueGrid, boolean[][] takenGrid, Position currentPos, boolean horizontal, int score, int oppScore, String playerName, Supplier<Position> userInput) {
		long end = System.currentTimeMillis() + MIN_DURATION;

		Position movePos = computeMove(valueGrid, takenGrid, currentPos, horizontal, score, oppScore, playerName);

		Long remainingTime;
		while ((remainingTime = end - System.currentTimeMillis()) > 0)
			try {
				Thread.sleep(remainingTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		return movePos;
	}

	protected abstract Position computeMove(int[][] valueGrid, boolean[][] takenGrid, Position currentPos, boolean horizontal, int score, int oppScore, String playerName);

}
