package othello.core;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;

import othello.core.players.Player;

public class Othello {

	private final BlockingQueue<Position> userInputQueue = new LinkedBlockingDeque<Position>();

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
	private IntConsumer scoreCallback1;
	private IntConsumer scoreCallback2;

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

		blackScore = 0;
		whiteScore = 0;

		this.updateCallback = updateCallback;
		this.scoreCallback1 = scoreCallback1;
		this.scoreCallback2 = scoreCallback2;
	}

	public static boolean[][] getValidMoveGrid(boolean[][] curGrid, boolean[][] takenGrid) {
		int gridSize = takenGrid.length;

		boolean[][] validMoveGrid = new boolean[gridSize][gridSize];

		Position[] validMoves = getValidMoves(curGrid, takenGrid);

		for (Position validMove : validMoves)
			validMoveGrid[validMove.i][validMove.j] = true;

		return validMoveGrid;
	}

	public static Position[] getValidMoves(boolean[][] curGrid, boolean[][] takenGrid) {
		int gridSize = curGrid.length;
	}
	
	private static Position[] getAdjacent(boolean[][] takenGrid) {
		for ()
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
			boolean[][] curGrid   = new boolean[gridSize][];
			boolean[][] takenGrid = new boolean[gridSize][];

			for (int i = 0; i < gridSize; i++) {
				if (isBlackPlayer())
					curGrid[i] = Arrays.copyOf(blackGrid[i], gridSize);
				else {
					boolean[] curRow = new boolean[gridSize];
					for (int j = 0; j < gridSize; j++)
						curRow[j] = !blackGrid[i][j];
					curGrid[i] = curRow;
				}
				takenGrid[i] = Arrays.copyOf(this.takenGrid[i], gridSize);
			}

			doMove(currentPlayer.move(curGrid, takenGrid, getCurrentScore(), getOpponentScore(), currentPlayer.getName(), this::getUserInput));

			userInputQueue.clear();

			updateCallback.run();
		}
	}

	public void reset() {
		currentPlayer = blackPlayer;
		currentOpponent = whitePlayer;

		scoreCallback1.accept(blackScore = 0);
		scoreCallback1.accept(whiteScore = 0);
	}

	public void doMove(Position movePos) {
		if (!isValid(movePos))
			throw new InvalidMoveException(movePos);
		takenGrid[movePos.i][movePos.j] = true;
		addCurrentScore(valueGrid[movePos.i][movePos.j]);
		takenBy1grid[movePos.i][movePos.j] = isBlackPlayer();
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
		return getValidMoveGrid(takenGrid, currentPos, isHorizontal());
	}

	public Position[] getValidMoves() {
		return getValidMoves(, takenGrid);
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
	
	public boolean isCurrent(Position pos) {
		
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
			scoreCallback1.accept(blackScore += points);
		else
			scoreCallback2.accept(whiteScore += points);
	}

}
