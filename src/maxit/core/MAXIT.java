package maxit.core;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;

public class MAXIT {

	private static final Random GRID_RANDY = new Random();

	private final BlockingQueue<Position> userInputQueue = new LinkedBlockingDeque<Position>();

	private int gridSize;

	private Player player1;
	private Player player2;

	private Player horizontalPlayer;
	private Player verticalPlayer;

	private Player currentPlayer;
	private Player currentOpponent;

	private int score1;
	private int score2;

	private int[][] valueGrid;

	private boolean[][] takenGrid;

	private boolean[][] takenBy1grid;

	private Position currentPos = null;

	private Runnable    updateCallback;
	private IntConsumer scoreCallback1;
	private IntConsumer scoreCallback2;

	public MAXIT(int gridSize, int minValue, int maxValue, Player player1, Player player2, boolean horizontal1, Runnable updateCallback, IntConsumer scoreCallback1, IntConsumer scoreCallback2) {
		this(gridSize, (i, j) -> GRID_RANDY.nextInt(maxValue - minValue + 1) + minValue, player1, player2, horizontal1, updateCallback, scoreCallback1, scoreCallback2);
		if (minValue > maxValue)
			throw new IllegalArgumentException("minValue (" + minValue + ") > maxValue (" + maxValue + ").");
	}

	public MAXIT(int gridSize, IntBinaryOperator gridGenerator, Player player1, Player player2, boolean horizontal1, Runnable updateCallback, IntConsumer scoreCallback1, IntConsumer scoreCallback2) {
		this.gridSize = gridSize;

		valueGrid = new int[gridSize][gridSize];

		takenGrid = new boolean[gridSize][gridSize];

		takenBy1grid = new boolean[gridSize][gridSize];

		for (int i = 0; i < gridSize; i++) {
			int[] row = valueGrid[i];
			for (int j = 0; j < gridSize; j++)
				row[j] = gridGenerator.applyAsInt(i, j);
		}

		this.player1 = player1;
		this.player2 = player2;

		this.horizontalPlayer = horizontal1 ? player1 : player2;
		this.verticalPlayer = horizontal1 ? player2 : player1;

		currentPlayer = player1;
		currentOpponent = player2;

		score1 = 0;
		score2 = 0;

		this.updateCallback = updateCallback;
		this.scoreCallback1 = scoreCallback1;
		this.scoreCallback2 = scoreCallback2;
	}

	public static boolean[][] getValidMoveGrid(boolean[][] takenGrid, Position currentPos, boolean horizontal) {
		int gridSize = takenGrid.length;

		boolean[][] validMoveGrid = new boolean[gridSize][gridSize];

		Position[] validMoves = getValidMoves(takenGrid, currentPos, horizontal);

		for (Position validMove : validMoves)
			validMoveGrid[validMove.i][validMove.j] = true;

		return validMoveGrid;
	}

	public static Position[] getValidMoves(boolean[][] takenGrid, Position currentPos, boolean horizontal) {
		int gridSize = takenGrid.length;

		if (currentPos == null) {
			Position[] pos = new Position[gridSize * gridSize];
			for (int i = 0; i < gridSize; i++)
				for (int j = 0; j < gridSize; j++)
					pos[i * gridSize + j] = new Position(i, j);
			return pos;
		}

		int idx = 0;

		Position[] pos = new Position[gridSize];

		if (horizontal) {
			boolean[] row = takenGrid[currentPos.i];
			for (int j = 0; j < gridSize; j++)
				if (!row[j])
					pos[idx++] = new Position(currentPos.i, j);
		} else
			for (int i = 0; i < gridSize; i++)
				if (!takenGrid[i][currentPos.j])
					pos[idx++] = new Position(i, currentPos.j);
		return Arrays.copyOf(pos, idx);
	}

	public static boolean isValid(Position currentPos, boolean[][] takenGrid, boolean horizontal, Position movePos) {
		int gridSize = takenGrid.length;
		if (movePos.i < 0 || movePos.i >= gridSize || movePos.j < 0 || movePos.j >= gridSize || takenGrid[movePos.i][movePos.j])
			return false;
		else if (currentPos == null)
			return true;
		else if (horizontal)
			return currentPos.i == movePos.i;
		else
			return currentPos.j == movePos.j;
	}

	public static boolean hasRemainingMoves(Position currentPos, boolean[][] takenGrid, boolean horizontal) {
		if (currentPos == null)
			return true;
		else if (horizontal) {
			for (boolean taken : takenGrid[currentPos.i])
				if (!taken)
					return true;
		} else
			for (boolean[] takenRow : takenGrid)
				if (!takenRow[currentPos.j])
					return true;
		return false;
	}

	/**
	 * Assumes that the move is valid; use isValid to check!
	 * 
	 * @return the new score
	 */
	public static int simulateMove(int[][] valueGrid, boolean[][] takenGrid, Position movePos, int curScore) {
		takenGrid[movePos.i][movePos.j] = true;
		return curScore + valueGrid[movePos.i][movePos.j];
	}

	/**
	 * Assumes that the move is valid; use isValid to check!
	 * 
	 * @return the new score
	 */
	public static int undoSimulateMove(int[][] valueGrid, boolean[][] takenGrid, Position movePos, int curScore) {
		takenGrid[movePos.i][movePos.j] = false;
		return curScore - valueGrid[movePos.i][movePos.j];
	}

	public void playGame() {
		while (hasRemainingMoves()) {
			int[][] valueGrid = new int[gridSize][];

			boolean[][] takenGrid = new boolean[gridSize][];

			for (int i = 0; i < gridSize; i++) {
				valueGrid[i] = Arrays.copyOf(this.valueGrid[i], gridSize);
				takenGrid[i] = Arrays.copyOf(this.takenGrid[i], gridSize);
			}

			Position movePos = currentPlayer.move(valueGrid, takenGrid, currentPos, isHorizontal(), getCurrentScore(), getOpponentScore(), this::getUserInput);

			doMove(movePos);

			currentPos = movePos;

			userInputQueue.clear();

			updateCallback.run();
		}
	}

	public void reset() {
		currentPlayer = player1;
		currentOpponent = player2;

		scoreCallback1.accept(score1 = 0);
		scoreCallback1.accept(score2 = 0);
	}

	public void doMove(Position movePos) {
		if (!isValid(movePos))
			throw new InvalidMoveException(movePos);
		takenGrid[movePos.i][movePos.j] = true;
		addCurrentScore(valueGrid[movePos.i][movePos.j]);
		takenBy1grid[movePos.i][movePos.j] = isPlayer1();
		swapPlayers();
	}

	private void swapPlayers() {
		Player temp = currentPlayer;
		currentPlayer = currentOpponent;
		currentOpponent = temp;
	}

	public boolean isPlayer1() {
		return currentPlayer == player1;
	}

	public boolean isPlayer2() {
		return currentPlayer == player2;
	}

	public boolean isHorizontal() {
		return currentPlayer == horizontalPlayer;
	}

	public boolean isVertical() {
		return currentPlayer == verticalPlayer;
	}

	public boolean[][] getValidMoveGrid() {
		return getValidMoveGrid(takenGrid, currentPos, isHorizontal());
	}

	public Position[] getValidMoves() {
		return getValidMoves(takenGrid, currentPos, isHorizontal());
	}

	public boolean isValid(Position movePos) {
		return isValid(currentPos, takenGrid, isHorizontal(), movePos);
	}

	public boolean hasRemainingMoves() {
		return hasRemainingMoves(currentPos, takenGrid, isHorizontal());
	}

	public void queueUserInput(Position userInput) {
		while (true)
			try {
				userInputQueue.put(userInput);
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	private Position getUserInput() {
		while (true)
			try {
				Position userInput = userInputQueue.take();
				if (isValid(userInput))
					return userInputQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public Player getHorizontalPlayer() {
		return horizontalPlayer;
	}

	public Player getVerticalPlayer() {
		return verticalPlayer;
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

	public Position getCurrentPos() {
		return currentPos;
	}

	public int getValue(int i, int j) {
		return valueGrid[i][j];
	}

	public int getValue(Position pos) {
		return getValue(pos.i, pos.j);
	}

	public boolean isTaken(int i, int j) {
		return takenGrid[i][j];
	}

	public boolean isTaken(Position pos) {
		return isTaken(pos.i, pos.j);
	}

	public boolean isTakenByPlayer1(int i, int j) {
		return takenBy1grid[i][j] && takenGrid[i][j];
	}

	public boolean isTakenByPlayer1(Position pos) {
		return isTakenByPlayer1(pos.i, pos.j);
	}

	public boolean isTakenByPlayer2(int i, int j) {
		return !takenBy1grid[i][j] && takenGrid[i][j];
	}

	public boolean isTakenByPlayer2(Position pos) {
		return isTakenByPlayer2(pos.i, pos.j);
	}

	public int getScore1() {
		return score1;
	}

	public int getScore2() {
		return score2;
	}

	public int getHorizontalScore() {
		return horizontalPlayer == player1 ? score1 : score2;
	}

	public int getVerticalScore() {
		return horizontalPlayer == player1 ? score2 : score1;
	}

	public int getCurrentScore() {
		return isPlayer1() ? score1 : score2;
	}

	public int getOpponentScore() {
		return isPlayer1() ? score2 : score1;
	}

	private void addCurrentScore(int points) {
		if (isPlayer1())
			scoreCallback1.accept(score1 += points);
		else
			scoreCallback2.accept(score2 += points);
	}

}
