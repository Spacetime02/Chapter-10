package maxit.core;

import java.util.Stack;
import java.util.function.IntBinaryOperator;

public class Maxit {

	public static void main(String[] args) {
		Stack<Integer> stac = new Stack<>();
		stac.push(1);
		stac.add(2);
		System.out.println(stac);
	}

	private int gridSize;

	private Player player1; // Vertical
	private Player player2; // Horizontal

	private Player currentPlayer;
	private Player currentOpponent;

	private int[][] grid;

	private boolean[][] taken;

	private Position currentPos = null;

	public Maxit(int gridSize, IntBinaryOperator gridGenerator, Player player1, Player player2) {
		this.gridSize = gridSize;

		grid = new int[gridSize][gridSize];

		taken = new boolean[gridSize][gridSize];

		for (int i = 0; i < gridSize; i++) {
			int[] row = grid[i];
			for (int j = 0; j < gridSize; j++)
				row[j] = gridGenerator.applyAsInt(i, j);
		}

		currentPlayer = this.player1 = player1;
		currentOpponent = this.player2 = player2;
	}

	public boolean isHorizontal() {
		return currentPlayer == player1;
	}

	public boolean isVertical() {
		return currentPlayer == player2;
	}

	public boolean isValid(Position movePos) {
		if (movePos.i < 0 || movePos.i >= gridSize || movePos.j < 0 || movePos.j >= gridSize || taken[movePos.i][movePos.j])
			return false;
		else if (isHorizontal())
			return currentPos.i == movePos.i;
		else
			return currentPos.j == movePos.j;
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
		return grid[i][j];
	}

	public void setValue(int value, int i, int j) {
		grid[i][j] = value;
	}

	public boolean isTaken(Position pos) {
		return taken[pos.i][pos.j];
	}

	public void setTaken(boolean taken, Position pos) {
		this.taken[pos.i][pos.j] = taken;
	}

}
