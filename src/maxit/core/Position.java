package maxit.core;

import java.io.Serializable;

import maxit.util.tuple.IntPair;

public class Position implements Serializable, Comparable<Position> {

	private static final long serialVersionUID = 1L;

	public final int i;
	public final int j;

	public Position(int i, int j) {
		this.i = i;
		this.j = j;
	}

	public Position(Position pos) {
		this(pos.i, pos.j);
	}

	public Position(IntPair pair) {
		this(pair.first, pair.second);
	}

	public IntPair toIntPair() {
		return new IntPair(i, j);
	}

	@Override
	public int compareTo(Position o) {
		int comp = Integer.compare(i, o.i);
		if (comp != 0)
			return comp;
		return Integer.compare(j, o.j);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Position))
			return false;
		Position pos = (Position) obj;
		return i == pos.i && j == pos.j;
	}

	@Override
	public int hashCode() {
		return 31 * Integer.hashCode(i) + Integer.hashCode(j);
	}

	@Override
	public String toString() {
		return "(" + i + ", " + j + ")";
	}

}
