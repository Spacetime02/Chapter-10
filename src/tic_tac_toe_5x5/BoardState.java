package tic_tac_toe_5x5;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		int val, i, j, k;
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
				if ((val = equalOrUnknown(set1)) != UNKNOWN)
					return val;
				if ((val = equalOrUnknown(set2)) != UNKNOWN)
					return val;
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
				if ((val = equalOrUnknown(set1)) != UNKNOWN)
					return val;
				if ((val = equalOrUnknown(set2)) != UNKNOWN)
					return val;
			}
		return UNKNOWN;
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

	public static final class Transform implements Comparable<Transform> {

		// Reflect happens first.
		// @formatter:off
		public static final int REFLECT       = 0b100;
		public static final int CLOCKWISE_0   = 0b000;
		public static final int CLOCKWISE_90  = 0b001;
		public static final int CLOCKWISE_180 = 0b010;
		public static final int CLOCKWISE_270 = 0b011;

		public static final int ROTATION_MASK   = 0b011;
		public static final int REFLECTION_MASK = 0b100;
		// @formatter:on

		public static final List<Transform> TRANSFORMS;
		public static final Map<Transform, Transform> INVERSION_TABLE;
		public static final Map<Transform, Map<Transform, Transform>> COMPOSITION_TABLE;

		static {
			List<Transform> transforms = new ArrayList<>(8);
			for (int i = 0; i < 8; i++)
				transforms.add(new Transform(i));
			TRANSFORMS = Collections.unmodifiableList(transforms);
		}

		static {
			int[] inverses = new int[] { 0, 3, 2, 1, 4, 5, 6, 7 };
			Map<Transform, Transform> inversionTable = new HashMap<>(8, Float.POSITIVE_INFINITY);
			for (int i = 0; i < 8; i++)
				inversionTable.put(TRANSFORMS.get(i), TRANSFORMS.get(inverses[i]));
			INVERSION_TABLE = Collections.unmodifiableMap(inversionTable);
		}

		static {
			// @formatter:off
			int[][] compositions = new int[][] {
					{ 0, 1, 2, 3, 4, 5, 6, 7 },
					{ 1, 2, 3, 0, 7, 4, 5, 6 },
					{ 2, 3, 0, 1, 6, 7, 4, 5 },
					{ 3, 0, 1, 2, 5, 6, 7, 4 },
					{ 4, 5, 6, 7, 0, 1, 2, 3 },
					{ 5, 6, 7, 4, 3, 0, 1, 2 },
					{ 6, 7, 4, 5, 2, 3, 0, 1 },
					{ 7, 4, 5, 6, 1, 2, 3, 0 }
				};
			// @formatter:on
			Map<Transform, Map<Transform, Transform>> compositionTable = new HashMap<>(8, Float.POSITIVE_INFINITY);
			for (int i = 0; i < 8; i++) {
				int[] row = compositions[i];
				Map<Transform, Transform> compositionRow = new HashMap<>(8, Float.POSITIVE_INFINITY);
				for (int j = 0; j < 8; j++)
					compositionRow.put(TRANSFORMS.get(j), TRANSFORMS.get(row[j]));
				compositionTable.put(TRANSFORMS.get(i), Collections.unmodifiableMap(compositionRow));
			}
			COMPOSITION_TABLE = Collections.unmodifiableMap(compositionTable);
		}

		private int transform;

		private Transform(int transform) {
			this.transform = transform;
		}

		public static Transform get(int transform) {
			if (transform < 0 || transform > 8)
				throw new IllegalArgumentException("Invalid transform code");
			return TRANSFORMS.get(transform);
		}

		public static Transform get(boolean reflect, int rotations) {
			if ((rotations & ~ROTATION_MASK) != 0)
				throw new IllegalArgumentException("Invalid rotations: 0x" + Integer.toHexString(rotations));
			return TRANSFORMS.get(reflect ? REFLECT | rotations : rotations);
		}

		public Transform compose(Transform other) {
			return COMPOSITION_TABLE.get(this).get(other);
		}

		public Transform invert() {
			return INVERSION_TABLE.get(this);
		}

		public Transform reflect() {
			return TRANSFORMS.get(transform ^ REFLECT);
		}

		public BoardState apply(BoardState state) {
			if (transform == CLOCKWISE_0)
				return new BoardState(state);
			BoardState newState = new BoardState();
			if (transform == CLOCKWISE_90)
				for (int i = 0; i < Game.BOARD_SIZE; i++)
					for (int j = 0; i < Game.BOARD_SIZE; j++)
						newState.set(j, Game.BOARD_SIZE - i, state.get(i, j));
			else if (transform == CLOCKWISE_180)
				for (int i = 0; i < Game.BOARD_SIZE; i++)
					for (int j = 0; i < Game.BOARD_SIZE; j++)
						newState.set(Game.BOARD_SIZE - i, Game.BOARD_SIZE - j, state.get(i, j));
			else if (transform == CLOCKWISE_270)
				for (int i = 0; i < Game.BOARD_SIZE; i++)
					for (int j = 0; i < Game.BOARD_SIZE; j++)
						newState.set(Game.BOARD_SIZE - j, i, state.get(i, j));
			else if (transform == (REFLECT | CLOCKWISE_0))
				for (int i = 0; i < Game.BOARD_SIZE; i++)
					for (int j = 0; i < Game.BOARD_SIZE; j++)
						newState.set(Game.BOARD_SIZE - j, Game.BOARD_SIZE - i, state.get(i, j));
			else if (transform == (REFLECT | CLOCKWISE_90))
				for (int i = 0; i < Game.BOARD_SIZE; i++)
					for (int j = 0; i < Game.BOARD_SIZE; j++)
						newState.set(j, Game.BOARD_SIZE - i, state.get(i, j));
			else if (transform == (REFLECT | CLOCKWISE_180))
				for (int i = 0; i < Game.BOARD_SIZE; i++)
					for (int j = 0; i < Game.BOARD_SIZE; j++)
						newState.set(Game.BOARD_SIZE - i, Game.BOARD_SIZE - j, state.get(i, j));
			else if (transform == (REFLECT | CLOCKWISE_270))
				for (int i = 0; i < Game.BOARD_SIZE; i++)
					for (int j = 0; i < Game.BOARD_SIZE; j++)
						newState.set(Game.BOARD_SIZE - j, i, state.get(i, j));
			else
				throw new Error("If you see this message, I am truly sorry. You shouldn't.");
			return newState;
		}

		@Override
		public int hashCode() {
			return transform;
		}

		@Override
		public int compareTo(Transform o) throws NullPointerException {
			return Integer.compare(this.transform, o.transform);
		}

	}

}
