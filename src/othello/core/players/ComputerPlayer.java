package othello.core.players;

import java.util.function.Supplier;

import othello.core.Position;

public abstract class ComputerPlayer extends Player {

	private int delay;

	public ComputerPlayer(String name, int delay) {
		super(name);
		this.delay = delay;
	}

	@Override
	public Position move(boolean[][] curGrid, boolean[][] takenGrid, int curScore, int oppScore, String playerName, Supplier<Position> userInput) {
		long end = System.currentTimeMillis() + delay;

		Position movePos = computeMove(curGrid, takenGrid, curScore, oppScore, playerName);

		long remainingTime;
		while ((remainingTime = end - System.currentTimeMillis()) > 0)
			try {
				Thread.sleep(remainingTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		return movePos;
	}

	protected abstract Position computeMove(boolean[][] curGrid, boolean[][] takenGrid, int curScore, int oppScore, String playerName);

}
