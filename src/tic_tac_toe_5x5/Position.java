package tic_tac_toe_5x5;

import java.util.BitSet;

public final class Position {

	public static final int EMPTY = 0;
	public static final int COMPUTER = 1;
	public static final int HUMAN = 2;
	
	// @formatter:off
	public static final int ROTATE_0   = 0;
	public static final int ROTATE_90  = 1;
	public static final int ROTATE_180 = 2;
	public static final int ROTATE_270 = 3;
	public static final int TRANSPOSE  = 4;
	// @formatter:on

	private final BitSet data;

	public Position() {
		this.data = new BitSet(TicTacToe.CELL_COUNT * 2);
	}

	public Position(Position other) {
		this.data = (BitSet) other.data.clone();
	}

	public int get(int i, int j) {
		int index = index(i, j);
		int val = 0;
		if (data.get(index))
			val += 2;
		if (data.get(index + 1))
			val += 1;
		return val;
	}

	public void set(int i, int j, int value) {
		if (value < 0 || value > 2)
			throw new IllegalArgumentException("Invlaid value");
		int index = index(i, j);
		data.set(index, value == 2);
		data.set(index + 1, value == 1);
	}
	
	public int canonicalize() {
		
	}
	
	public Position transform(int transform) {
		
	}
	
	public int inverse(int transform) {
		return transform & TRANSPOSE | (TRANSPOSE - (transform & ~TRANSPOSE));
	}

	private static int index(int i, int j) {
		return 2 * (i * TicTacToe.BOARD_SIZE + j);
	}

}
