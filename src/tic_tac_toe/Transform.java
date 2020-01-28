package tic_tac_toe;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class Transform implements Comparable<Transform> {

	// Reflect happens first.
	// @formatter:off
	public static final int REFLECT       = 0b100;
	public static final int CLOCKWISE_0   = 0b000;
	public static final int CLOCKWISE_90  = 0b001;
	public static final int CLOCKWISE_180 = 0b010;
	public static final int CLOCKWISE_270 = 0b011;

	public static final int REFLECTION_MASK = 0b100;
	public static final int ROTATION_MASK   = 0b011;
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
//		if (transform == CLOCKWISE_0)
//			return new BoardState(state);
		BoardState newState = new BoardState();
		int n = Game.BOARD_SIZE;
		for (Point p = new Point(); p.x < n; p.x++)
			for (p.y = 0; p.y < n; p.y++)
				newState.set(apply(p), state.get(p));
		return newState;
	}

	private Point apply(Point point) {
		int i = point.x;
		int j = point.y;
		int n = Game.BOARD_SIZE - 1;
		switch (transform) {
			case CLOCKWISE_0:
				return new Point(i, j);
			case CLOCKWISE_90:
				return new Point(j, n - i);
			case CLOCKWISE_180:
				return new Point(n - i, n - j);
			case CLOCKWISE_270:
				return new Point(n - j, i);
			case REFLECT | CLOCKWISE_0:
				return new Point(n - j, n - i);
			case REFLECT | CLOCKWISE_90:
				return new Point(n - i, j);
			case REFLECT | CLOCKWISE_180:
				return new Point(j, i);
			case REFLECT | CLOCKWISE_270:
				return new Point(i, n - j);
			default:
				throw new Error("If you see this message, I am terribly sorry. It should not be possible");
		}
	}

	// tester
	public static void mainn(String[] args) {
		BoardState state = new BoardState();
		Random randy = new Random();
		for (int index = 0; index < Game.CELL_COUNT; index++)
			state.set(index, randy.nextInt(3));
		BoardState transformed;
		BoardState restored;
		for (int t = 0; t < 8; t++) {
			Transform tf = Transform.get(t);
			transformed = tf.apply(state);
			restored = tf.invert().apply(transformed);
			System.out.println(tf + "\n\nState:\n" + state + "\n\nTransformed:\n" + transformed + "\n\nRestored:\n" + restored + "\n\n\n");
			if (!restored.equals(state))
				System.out.println("Fail " + t);
		}
		System.out.println("Done");
	}

	@Override
	public int hashCode() {
		return transform;
	}

	@Override
	public int compareTo(Transform o) throws NullPointerException {
		return Integer.compare(this.transform, o.transform);
	}

	@Override
	public String toString() {
		return "T_" + Integer.toHexString(transform);
	}

}