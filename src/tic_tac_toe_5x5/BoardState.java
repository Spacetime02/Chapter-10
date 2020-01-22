package tic_tac_toe_5x5;

import java.awt.Point;
import java.util.Arrays;
import java.util.BitSet;

public final class BoardState implements Comparable<BoardState> {

	// @formatter:off
	public static final int EMPTY    = 0;
	public static final int COMPUTER = 1;
	public static final int HUMAN    = 2;

	public static final int HUMAN_IMMEDIATE    = -Game.CELL_COUNT;
	public static final int HUMAN_LATE         = -1;
	public static final int UNKNOWN            =  0;
	public static final int COMPUTER_LATE      =  1;
	public static final int COMPUTER_IMMEDIATE =  Game.CELL_COUNT;
	// @formatter:on

	private final BitSet data;

	public BoardState() {
		this.data = new BitSet(Game.CELL_COUNT * 2);
	}

	public BoardState(BoardState other) {
		this.data = (BitSet) other.data.clone();
	}

	public int get(Point p) {
		return get(p.x, p.y);
	}

	public int get(int i, int j) {
		return get(index(i, j));
	}

	public int get(int index) {
		int val = 0;
		if (data.get(2 * index))
			val += 2;
		if (data.get(2 * index + 1))
			val += 1;
		return val;
	}

	public void set(Point p, int value) {
		set(p.x, p.y, value);
	}

	public void set(int i, int j, int value) {
		set(index(i, j), value);
	}

	public void set(int index, int value) {
		if (value < 0 || value > 2)
			throw new IllegalArgumentException("Invlaid value");
		data.set(2 * index, value == 2);
		data.set(2 * index + 1, value == 1);
	}

	public void set(BoardState other) {
		data.clear();
		data.or(other.data);
	}

	/**
	 * @return The inverse of the canonicalizing {@code Transform}.
	 */
	public Transform canonicalize(BoardState dest) {
		BoardState maxState = null;
		Transform maxTransform = null;
		for (Transform tf : Transform.TRANSFORMS) {
			BoardState state = transform(tf);
			if (maxState == null || state.compareTo(maxState) > 0) {
				maxTransform = tf;
				maxState = state;
			}
		}
		dest.set(maxState);
		return maxTransform.invert();
	}

	public BoardState transform(Transform transform) {
		return transform.apply(this);
	}

	public static int inverseTransform(int transform) {
		return (transform & 4) == 4 ? transform : 4 - transform;
	}

	public static int index(int i, int j) {
		return i * Game.BOARD_SIZE + j;
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

	public int evaluateImmediate(int depth) {
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
					set2[k] = get(j, i + k);
				}
				if ((state = equalOrUnknown(set1)) != EMPTY)
					return stateToValue(state, depth);
				if ((state = equalOrUnknown(set2)) != EMPTY)
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
				if ((state = equalOrUnknown(set1)) != EMPTY)
					return stateToValue(state, depth);
				if ((state = equalOrUnknown(set2)) != EMPTY)
					return stateToValue(state, depth);
			}
		return UNKNOWN;
	}

	private static int stateToValue(int state, int depth) {
		switch (state) {
			case EMPTY:
				return UNKNOWN;
			case HUMAN:
				return HUMAN_IMMEDIATE + depth;
			case COMPUTER:
				return COMPUTER_IMMEDIATE - depth;
			default:
				throw new Error("If you see this message, I am terribly sorry. It should not be possible");
		}
	}

	private static int equalOrUnknown(int[] vals) {
		int val = vals[0];
		if (val == UNKNOWN)
			return UNKNOWN;
		for (int i = 1; i < vals.length; i++) {
			if (vals[i] != val)
				return UNKNOWN;
		}
		return val;
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
	public boolean equals(Object obj) {
		if (!(obj instanceof BoardState) || obj == null)
			return false;
		return data.equals(((BoardState) obj).data);
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

}
