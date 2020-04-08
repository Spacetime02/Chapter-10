package othello.core;

import java.util.Arrays;

public enum Direction {

	// @formatter:off
	NORTH_WEST(-1, -1), NORTH (-1,  0), NORTH_EAST(-1,  1),
	WEST      ( 0, -1), CENTER( 0,  0), EAST      ( 0,  1),
	SOUTH_WEST( 1, -1), SOUTH ( 1,  0), SOUTH_EAST( 1,  1);

	private static final Direction[][] GRID = {
			{ NORTH_WEST, NORTH , NORTH_EAST, },
			{ WEST      , CENTER, EAST      , },
			{ SOUTH_WEST, SOUTH , SOUTH_EAST, },
	};

	private static final Direction[] NON_CENTER = {
			NORTH_WEST, NORTH, NORTH_EAST,
			WEST      ,        EAST      ,
			SOUTH_WEST, SOUTH, SOUTH_EAST,
	};
	// @formatter:on

	public static Direction[][] getGrid() {
		Direction[][] grid = new Direction[3][];
		for (int i = 0; i < 3; i++)
			grid[i] = Arrays.copyOf(GRID[i], 3);
		return grid;
	}

	public static Direction get(int i, int j) {
		return GRID[i + 1][j + 1];
	}

	public static Direction[] getNonCenter() {
		return Arrays.copyOf(NON_CENTER, 8);
	}

	public final int i, j;

	private Direction(int i, int j) {
		this.i = i;
		this.j = j;
	}

	public int getI() {
		return i;
	}

	public int getJ() {
		return j;
	}

	public Position step(Position pos) {
		return new Position(pos.i + i, pos.j + j);
	}

	public Position step(Position pos, int numSteps) {
		return new Position(pos.i + i * numSteps, pos.j + j * numSteps);
	}

	public Direction reverse() {
		return get(-i, -j);
	}

}
