package othello.util.tuple;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class IntPair implements Serializable, Map.Entry<Integer, Integer> {

	private static final long serialVersionUID = 1L;

	public final int first;
	public final int second;

	public static IntPair reverse(IntPair pair) {
		return new IntPair(pair.second, pair.first);
	}

	public static int compare(IntPair left, IntPair right) {
		return compare(left, right, Integer::compare, Integer::compare);
	}

	public static int compare(IntPair left, IntPair right, Comparator<Integer> firstComparator, Comparator<Integer> secondComparator) {
		Objects.requireNonNull(firstComparator);
		Objects.requireNonNull(secondComparator);
		int comp = firstComparator.compare(left.first, right.first);
		if (comp != 0)
			return comp;
		return secondComparator.compare(left.second, right.second);
	}

	public static int compareReverse(IntPair left, IntPair right) {
		return compareReverse(left, right, Integer::compare, Integer::compare);
	}

	public static int compareReverse(IntPair left, IntPair right, Comparator<Integer> firstComparator, Comparator<Integer> secondComparator) {
		Objects.requireNonNull(firstComparator);
		Objects.requireNonNull(secondComparator);
		int comp = secondComparator.compare(left.first, right.first);
		if (comp != 0)
			return comp;
		return firstComparator.compare(left.second, right.second);
	}

	public static Comparator<IntPair> comparator() {
		return IntPair::compare;
	}

	public static Comparator<IntPair> comparator(Comparator<Integer> firstComparator, Comparator<Integer> secondComparator) {
		return (left, right) -> compare(left, right, firstComparator, secondComparator);
	}

	public static Comparator<IntPair> comparatorReverse() {
		return IntPair::compareReverse;
	}

	public static Comparator<IntPair> comparatorReverse(Comparator<Integer> firstComparator, Comparator<Integer> secondComparator) {
		return (left, right) -> compareReverse(left, right, firstComparator, secondComparator);
	}

	public IntPair(int first, int second) {
		this.first = first;
		this.second = second;
	}

	public IntPair(IntPair other) {
		this.first = other.first;
		this.second = other.second;
	}

	public int getFirst() {
		return first;
	}

	public int getSecond() {
		return second;
	}

	public int[] toIntArray() {
		return new int[] { first, second };
	}

	@SuppressWarnings("unchecked")
	public <T> T toArray(T[] a) {
		Objects.requireNonNull(a);
		if (a.length < 2)
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), 2);
		a[0] = (T) (Integer) first;
		a[1] = (T) (Integer) second;
		return null;
	}

	public IntPair putIn(Map<Integer, Integer> map) {
		Integer prev = map.put(first, second);
		return prev == null ? null : new IntPair(first, prev);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof IntPair))
			return false;
		IntPair p = (IntPair) obj;
		return first == p.first && second == p.second;
	}

	@Override
	public int hashCode() {
		return 31 * Integer.hashCode(first) + Integer.hashCode(second);
	}

	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}

	@Override
	public Integer getKey() {
		return first;
	}

	@Override
	public Integer getValue() {
		return second;
	}

	@Override
	public Integer setValue(Integer value) {
		throw new UnsupportedOperationException();
	}

}
