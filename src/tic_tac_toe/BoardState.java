package tic_tac_toe;

import java.awt.Point;
import java.util.Arrays;
import java.util.BitSet;

final class BoardState implements Comparable<BoardState> {

	// @formatter:off
	public static final int EMPTY    = 0;
	public static final int COMPUTER = 1;
	public static final int HUMAN    = 2;

	public static final Integer UNKNOWN      =  null;
	public static final Integer HUMAN_WIN    = -Game.CELL_COUNT - 1;
	public static final Integer TIE          =  0;
	public static final Integer COMPUTER_WIN =  Game.CELL_COUNT + 1;
	// @formatter:on

	public BitSet getData() {
		BitSet set = new BitSet(Game.CELL_COUNT * 2);
		set.or(data);
		return set;
	}

	private static int equalOrEmpty(int[] cells) {
		int val = cells[0];
		if (val == EMPTY)
			return EMPTY;
		for (int i = 1; i < cells.length; i++) {
			if (cells[i] != val)
				return EMPTY;
		}
		return val;
	}

	public static int index(int i, int j) {
		return i * Game.BOARD_SIZE + j;
	}

	private static int stateToValue(int state, int depth) {
		switch (state) {
			case EMPTY:
				return UNKNOWN;
			case HUMAN:
				return HUMAN_WIN + depth;
			case COMPUTER:
				return COMPUTER_WIN - depth;
			default:
				throw new Error("If you see this message, I am terribly sorry. It should not be possible");
		}
	}

	private final BitSet data;

	public BoardState() {
		this.data = new BitSet(Game.CELL_COUNT * 2);
	}

	public BoardState(BoardState other) {
		this.data = (BitSet) other.data.clone();
	}

	public Transform canonicalize() {
		return canonicalize(this);
	}

	// TODO add transformations to compress table.
	/**
	 * @return The inverse of the canonicalizing {@code Transform}.
	 */
	public Transform canonicalize(BoardState dest) {
		BoardState og = new BoardState(this);
		if (dest == null)
			dest = this;
		BoardState maxState = null;
		Transform maxTransform = null;
		for (Transform tf : Transform.TRANSFORMS) {
			BoardState state = tf.apply(this);
			if (!og.equals(this))
				throw new Error();
			if (maxState == null || state.compareTo(maxState) > 0) {
				maxTransform = tf;
				maxState = new BoardState(state);
			}
		}
		dest.set(maxState);
		return maxTransform.invert();
		// return Transform.get(Transform.CLOCKWISE_0);
	}

	public void clear() {
		data.clear();
	}

	@Override
	public int compareTo(BoardState o) {
		for (int i = 0; i < Game.BOARD_SIZE; i++)
			for (int j = 0; j < Game.BOARD_SIZE; j++) {
				int comp = Integer.compare(get(i, j), o.get(i, j));
				if (comp != 0)
					return comp;
			}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BoardState) || obj == null)
			return false;
		BoardState other = (BoardState) obj;
		if (data.size() != other.data.size())
			return false;
		for (int i = 0; i < Game.CELL_COUNT * 2; i++)
			if (data.get(i) != other.data.get(i))
				return false;
		return true;
	}

	public Integer evaluateImmediate() {
		int depth = data.cardinality(); // 0b01 and 0b10 contribute 1 while 0b00 contributes 0.
		int state, i, j, k;
		int[] set1, set2;
		for (i = 0; i < Game.BOARD_SIZE; i++)
			for (j = 0; j <= Game.BOARD_SIZE - Game.MATCH_SIZE; j++) {
				set1 = new int[Game.MATCH_SIZE];
				set2 = new int[Game.MATCH_SIZE];
				for (k = 0; k < Game.MATCH_SIZE; k++) {
					// -
					set1[k] = get(i, j + k);
					// |
					set2[k] = get(j + k, i);
				}
				// System.out.println("- " + Arrays.toString(set1));
				if ((state = equalOrEmpty(set1)) != EMPTY)
					return stateToValue(state, depth);
				// System.out.println("| " + Arrays.toString(set2));
				if ((state = equalOrEmpty(set2)) != EMPTY)
					return stateToValue(state, depth);
			}
		for (i = 0; i <= Game.BOARD_SIZE - Game.MATCH_SIZE; i++)
			for (j = 0; j <= Game.BOARD_SIZE - Game.MATCH_SIZE; j++) {
				set1 = new int[Game.MATCH_SIZE];
				set2 = new int[Game.MATCH_SIZE];
				for (k = 0; k < Game.MATCH_SIZE; k++) {
					// \
					set1[k] = get(i + k, j + k);
					// /
					set2[k] = get(i + Game.MATCH_SIZE - k - 1, j + k);
				}
				if ((state = equalOrEmpty(set1)) != EMPTY)
					return stateToValue(state, depth);
				if ((state = equalOrEmpty(set2)) != EMPTY)
					return stateToValue(state, depth);
			}
		// System.out.println(depth);
		return depth == Game.CELL_COUNT ? TIE : UNKNOWN;
	}

	public int get(int index) {
		int val = 0;
		if (data.get(2 * index))
			val += 2;
		if (data.get(2 * index + 1))
			val += 1;
		return val;
	}

	public int get(int i, int j) {
		return get(index(i, j));
	}

	public int get(Point p) {
		return get(p.x, p.y);
	}

	public Point[] getCells(int state) {
		Point[] arr = new Point[Game.CELL_COUNT];
		int ptr = 0;
		for (Point p = new Point(); p.x < Game.BOARD_SIZE; p.x++)
			for (p.y = 0; p.y < Game.BOARD_SIZE; p.y++)
				if (get(p) == state)
					arr[ptr++] = new Point(p);
		return Arrays.copyOf(arr, ptr);
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

	public void set(BoardState other) {
		data.clear();
		data.or(other.data);
	}

	public void set(int index, int player) {
		if (player < 0 || player > 2)
			throw new IllegalArgumentException("Invlaid player");
		data.set(2 * index, player == 2);
		data.set(2 * index + 1, player == 1);
	}

	public void set(int i, int j, int player) {
		if (i >= Game.BOARD_SIZE || j >= Game.BOARD_SIZE)
			throw new IllegalArgumentException();
		set(index(i, j), player);
	}

	public void set(Point p, int player) {
		set(p.x, p.y, player);
	}

	public void setAll(int player) {
		for (int index = 0; index < Game.CELL_COUNT; index++)
			set(index, player);
	}

	// @Override
	// public String toString() {
	// int side = Game.BOARD_SIZE * 2;
	// StringBuilder builder = new StringBuilder((side - 1) * side - 1);
	// char[] names = { ' ', 'C', 'H' };
	// for (int i = 0; i < Game.BOARD_SIZE; i++) {
	// for (int j = 0; j < Game.BOARD_SIZE; j++) {
	// builder.append(names[get(i, j)]);
	// if (j < Game.BOARD_SIZE - 1)
	// builder.append('|');
	// }
	// if (i < Game.BOARD_SIZE - 1)
	// builder.append('\n');
	// if (i < Game.BOARD_SIZE - 1) {
	// for (int j = 1; j < side; j++)
	// builder.append('-');
	// builder.append('\n');
	// }
	// }
	// return builder.toString();
	// }

	@Override
	public String toString() {
		char[] names = { '_', 'C', 'H' };
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < Game.BOARD_SIZE; i++) {
			for (int j = 0; j < Game.BOARD_SIZE; j++)
				builder.append(names[get(i, j)]);
			if (i < Game.BOARD_SIZE - 1)
				builder.append('\n');
		}
		return builder.toString();
	}

	// tester
	public static void mainn(String[] args) {
		BoardState state = new BoardState();
		for (int i = 0; i < 3; i++)
			state.set(i, 0, COMPUTER);
		System.out.println(state);
		System.out.println(state.evaluateImmediate());
	}

}
