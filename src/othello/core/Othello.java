package othello.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.IntConsumer;

import othello.core.players.Player;
import othello.util.tuple.IntPair;

public class Othello {

	private final BlockingQueue<Optional<Position>> userInputQueue = new LinkedBlockingDeque<>();

	private int gridSize;

	private Player blackPlayer;
	private Player whitePlayer;

	private Player currentPlayer;
	private Player currentOpponent;

	private int blackScore;
	private int whiteScore;

	private boolean[][] blackGrid;
	private boolean[][] takenGrid;

	private Runnable    updateCallback;
	private IntConsumer blackScoreCallback;
	private IntConsumer whiteScoreCallback;

	public Othello(int gridSize, Player blackPlayer, Player whitePlayer, Runnable updateCallback, IntConsumer scoreCallback1, IntConsumer scoreCallback2) {
		if (gridSize % 2 != 0)
			throw new IllegalArgumentException("Grid size (" + gridSize + ") is odd.");

		this.gridSize = gridSize;
		int half = gridSize / 2;

		blackGrid = new boolean[gridSize][gridSize];
		takenGrid = new boolean[gridSize][gridSize];

		blackGrid[half - 1][half - 1] = blackGrid[half][half] = true;
		takenGrid[half - 1][half - 1] = takenGrid[half - 1][half] = takenGrid[half][half - 1] = takenGrid[half][half] = true;

		this.blackPlayer = blackPlayer;
		this.whitePlayer = whitePlayer;

		currentPlayer = blackPlayer;
		currentOpponent = whitePlayer;

		blackScore = 2;
		whiteScore = 2;

		this.updateCallback = updateCallback;
		this.blackScoreCallback = scoreCallback1;
		this.whiteScoreCallback = scoreCallback2;
	}

	public static boolean[][] getValidMoveGrid(boolean[][] curGrid, boolean[][] takenGrid) {
		Position[] validMoves = getValidMoves(curGrid, takenGrid);

		if (validMoves[0] == null)
			return null;

		int gridSize = takenGrid.length;

		boolean[][] validMoveGrid = new boolean[gridSize][gridSize];

		for (Position validMove : validMoves)
			validMoveGrid[validMove.i][validMove.j] = true;

		return validMoveGrid;
	}

	public static Position[] getValidMoves(boolean[][] curGrid, boolean[][] takenGrid) {
		int gridSize = curGrid.length;

		// Filters possibilities, improving performance
		Position[] adjacent = getAdjacent(takenGrid);

		int numAdjacent = adjacent.length;

		Direction[] dirs = Direction.getNonCenter();

		List<Position> valid = new ArrayList<>();

		for (int p = 0; p < numAdjacent; p++) {
			Position pos = adjacent[p];

			Position stepPos;
			for (int d = 0; d < 8; d++) {
				Direction dir = dirs[d];

				int i;
				int j;

				for (int s = 1; s < gridSize; s++) {
					stepPos = dir.step(pos, s);
					i = stepPos.i;
					j = stepPos.j;

					if (i < 0 || i >= gridSize || j < 0 || j >= gridSize || !takenGrid[i][j] || s == 1 && curGrid[i][j])
						break;
					else if (curGrid[i][j]) {
						valid.add(pos);
						break;
					}
				}
			}
		}

		// If valid is empty, returns { null }.
		return valid.toArray(new Position[1]);
	}

	public static boolean isValid(boolean[][] curGrid, boolean[][] takenGrid, Position movePos) {
		if (movePos == null)
			return getValidMoves(curGrid, takenGrid)[0] == null;
		else if (takenGrid[movePos.i][movePos.j])
			return false;

		int gridSize = curGrid.length;

		Direction[] dirs = Direction.getNonCenter();

		Position stepPos;
		for (int d = 0; d < 8; d++) {
			Direction dir = dirs[d];

			int i;
			int j;

			for (int s = 1;; s++) {
				stepPos = dir.step(movePos, s);
				i = stepPos.i;
				j = stepPos.j;

				if (i < 0 || i >= gridSize || j < 0 || j >= gridSize || !takenGrid[i][j] || s == 1 && curGrid[i][j])
					break;
				else if (curGrid[i][j])
					return true;
			}
		}
		return false;
	}

	private static Position[] getAdjacent(boolean[][] takenGrid) {
		int gridSize = takenGrid.length;

		List<Position> adjacent = new ArrayList<>();

		Direction[] dirs = Direction.getNonCenter();

		for (int i = 0; i < gridSize; i++)
			for (int j = 0; j < gridSize; j++) {
				if (takenGrid[i][j])
					continue;
				Position cur = new Position(i, j);
				for (int d = 0; d < 8; d++) {
					Position adj = dirs[d].step(cur);
					if (adj.i >= 0 && adj.i < gridSize && adj.j >= 0 && adj.j < gridSize && takenGrid[adj.i][adj.j]) {
						adjacent.add(cur);
						break;
					}
				}
			}
		return adjacent.toArray(new Position[adjacent.size()]);
	}

	/**
	 * Assumes that the move is valid; use isValid to check!
	 * 
	 * @return a pair of the new curren score and the new opponent score.
	 */
	public static IntPair simulateMove(boolean[][] curGrid, boolean[][] takenGrid, Position movePos, int curScore, int oppScore) {
		if (movePos != null) {
			int gridSize = curGrid.length;

			int moveI = movePos.i;
			int moveJ = movePos.j;

			takenGrid[moveI][moveJ] = true;
			curGrid[moveI][moveJ] = true;

			int points = 0;

			Direction[] dirs = Direction.getNonCenter();

			Position stepPos;

			List<Position> toChange = new ArrayList<>();
			for (int d = 0; d < 8; d++) {
				Direction dir = dirs[d];

				int i;
				int j;

				toChange.clear();
				for (int s = 1;; s++) {
					stepPos = dir.step(movePos, s);
					i = stepPos.i;
					j = stepPos.j;

					if (i < 0 || i >= gridSize || j < 0 || j >= gridSize || !takenGrid[i][j] || s == 1 && curGrid[i][j])
						break;
					else if (curGrid[i][j]) {
						points += --s;
						while (s > 0) {
							stepPos = toChange.get(--s);
							curGrid[stepPos.i][stepPos.j] = true;
						}
						break;
					} else
						toChange.add(stepPos);
				}
			}
			return new IntPair(curScore + points + 1, oppScore - points);
		} else
			return new IntPair(curScore, oppScore);
	}

	public void playGame() {
		boolean prevForfeit = false;
		boolean keepPlaying = true;
		while (keepPlaying) {
			boolean[][] curGrid   = new boolean[gridSize][];
			boolean[][] takenGrid = new boolean[gridSize][];

			boolean isBlackPlayer = isBlackPlayer();

			for (int i = 0; i < gridSize; i++) {
				if (isBlackPlayer)
					curGrid[i] = Arrays.copyOf(blackGrid[i], gridSize);
				else {
					boolean[] curRow = new boolean[gridSize];
					for (int j = 0; j < gridSize; j++)
						curRow[j] = !blackGrid[i][j];
					curGrid[i] = curRow;
				}
				takenGrid[i] = Arrays.copyOf(this.takenGrid[i], gridSize);
			}

			Position movePos = currentPlayer.move(curGrid, takenGrid, getCurrentScore(), getOpponentScore(), currentPlayer.getName(), this::getUserInput);
			doMove(movePos);
			if (movePos == null)
				if (prevForfeit)
					keepPlaying = false;
				else
					prevForfeit = true;
			else
				prevForfeit = false;

			userInputQueue.clear();

			updateCallback.run();
		}
	}

	public void doMove(Position movePos) {
		if (!isValid(movePos))
			throw new InvalidMoveException(movePos);

		if (movePos != null) {
			int moveI = movePos.i;
			int moveJ = movePos.j;

			boolean isBlackPlayer = isBlackPlayer();

			takenGrid[moveI][moveJ] = true;
			blackGrid[moveI][moveJ] = isBlackPlayer;

			int points = 0;

			Direction[] dirs = Direction.getNonCenter();

			Position stepPos;

			List<Position> toChange = new ArrayList<>();
			for (int d = 0; d < 8; d++) {
				Direction dir = dirs[d];

				int i;
				int j;

				toChange.clear();
				for (int s = 1;; s++) {
					stepPos = dir.step(movePos, s);
					i = stepPos.i;
					j = stepPos.j;

					if (i < 0 || i >= gridSize || j < 0 || j >= gridSize || !takenGrid[i][j] || s == 1 && blackGrid[i][j] == isBlackPlayer)
						break;
					else if (blackGrid[i][j] == isBlackPlayer) {
						points += --s;
						while (s > 0) {
							stepPos = toChange.get(--s);
							blackGrid[stepPos.i][stepPos.j] = isBlackPlayer;
						}
						break;
					} else
						toChange.add(stepPos);
				}
			}

			addCurrentScore(points + 1);
			addOpponentScore(-points);
		}
		swapPlayers();
	}

	private void swapPlayers() {
		Player temp = currentPlayer;
		currentPlayer = currentOpponent;
		currentOpponent = temp;
	}

	public boolean isBlackPlayer() {
		return currentPlayer == blackPlayer;
	}

	public boolean isWhitePlayer() {
		return currentPlayer == whitePlayer;
	}

	public boolean[][] getValidMoveGrid() {
		return getValidMoveGrid(getCurrentGrid(), takenGrid);
	}

	public Position[] getValidMoves() {
		return getValidMoves(getCurrentGrid(), takenGrid);
	}

	public boolean isValid(Position movePos) {
		return isValid(getCurrentGrid(), takenGrid, movePos);
	}

	public void queueUserInput(Position userInput) {
		while (true)
			try {
				userInputQueue.put(Optional.ofNullable(userInput));
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	private Position getUserInput() {
		while (true)
			try {
				Position userInput = userInputQueue.take().orElse(null);
				if (isValid(userInput))
					return userInput;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	public Player getWhitePlayer() {
		return blackPlayer;
	}

	public Player getBlackPlayer() {
		return whitePlayer;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public Player getCurrentOpponent() {
		return currentOpponent;
	}

	public int getGridSize() {
		return gridSize;
	}

	public boolean isBlack(int i, int j) {
		return blackGrid[i][j] && takenGrid[i][j];
	}

	public boolean isBlack(Position pos) {
		return isBlack(pos.i, pos.j);
	}

	public boolean isWhite(int i, int j) {
		return !blackGrid[i][j] && takenGrid[i][j];
	}

	public boolean isWhite(Position pos) {
		return isWhite(pos.i, pos.j);
	}

	public boolean isCurrent(int i, int j) {
		return isBlackPlayer() ? isBlack(i, j) : isWhite(i, j);
	}

	public boolean isCurrent(Position pos) {
		return isCurrent(pos.i, pos.j);
	}

	public boolean isTaken(int i, int j) {
		return takenGrid[i][j];
	}

	public boolean isTaken(Position pos) {
		return isTaken(pos.i, pos.j);
	}

	public boolean isEmpty(int i, int j) {
		return !takenGrid[i][j];
	}

	public boolean isEmpty(Position pos) {
		return isEmpty(pos.i, pos.j);
	}

	public int getBlackScore() {
		return blackScore;
	}

	public int getWhiteScore() {
		return whiteScore;
	}

	public int getCurrentScore() {
		return isBlackPlayer() ? blackScore : whiteScore;
	}

	public int getOpponentScore() {
		return isBlackPlayer() ? whiteScore : blackScore;
	}

	private void addCurrentScore(int points) {
		if (isBlackPlayer())
			blackScoreCallback.accept(blackScore += points);
		else
			whiteScoreCallback.accept(whiteScore += points);
	}

	private void addOpponentScore(int points) {
		if (isBlackPlayer())
			whiteScoreCallback.accept(whiteScore += points);
		else
			blackScoreCallback.accept(blackScore += points);
	}

	/**
	 * Do not directly expose the results to external code!
	 */
	private boolean[][] getCurrentGrid() {
		if (isBlackPlayer())
			return blackGrid;
		else {
			boolean[][] whiteGrid = new boolean[gridSize][];
			for (int i = 0; i < gridSize; i++) {
				boolean[] blackRow = blackGrid[i];
				boolean[] whiteRow = new boolean[gridSize];
				for (int j = 0; j < gridSize; j++)
					whiteRow[j] = !blackRow[j];
				whiteGrid[i] = whiteRow;
			}
			return whiteGrid;
		}
	}

}
