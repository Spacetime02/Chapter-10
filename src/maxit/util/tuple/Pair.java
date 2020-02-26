package maxit.util.tuple;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class Pair<F, S> implements Serializable, Map.Entry<F, S> {

	private static final long serialVersionUID = 1L;

	public final F first;
	public final S second;

	public static <F, S> Pair<S, F> reverse(Pair<F, S> pair) {
		return new Pair<>(pair.second, pair.first);
	}

	public static <F extends Comparable<? super F>, S extends Comparable<? super S>> int compareFirst(Pair<F, S> left, Pair<F, S> right) {
		return compare(left, right, Comparator.naturalOrder(), Comparator.naturalOrder());
	}

	public static <F, S> int compare(Pair<F, S> left, Pair<F, S> right, Comparator<F> firstComparator, Comparator<S> secondComparator) {
		Objects.requireNonNull(firstComparator);
		Objects.requireNonNull(secondComparator);
		int comp = firstComparator.compare(left.first, right.first);
		if (comp != 0)
			return comp;
		return secondComparator.compare(left.second, right.second);
	}

	public static <F extends Comparable<? super F>, S extends Comparable<? super S>> int compareReverse(Pair<F, S> left, Pair<F, S> right) {
		return compareReverse(left, right, Comparator.naturalOrder(), Comparator.naturalOrder());
	}

	public static <F, S> int compareReverse(Pair<F, S> left, Pair<F, S> right, Comparator<F> firstComparator, Comparator<S> secondComparator) {
		Objects.requireNonNull(firstComparator);
		Objects.requireNonNull(secondComparator);
		int comp = secondComparator.compare(left.second, right.second);
		if (comp != 0)
			return comp;
		return firstComparator.compare(left.first, right.first);
	}

	public static <F extends Comparable<? super F>, S extends Comparable<? super S>> Comparator<Pair<F, S>> comparator() {
		return Pair::compareFirst;
	}

	public static <F, S> Comparator<Pair<F, S>> comparatorTFirst(Comparator<F> firstComparator, Comparator<S> secondComparator) {
		return (left, right) -> compare(left, right, firstComparator, secondComparator);
	}

	public static <F extends Comparable<? super F>, S extends Comparable<? super S>> Comparator<Pair<F, S>> comparatorReverse() {
		return Pair::compareReverse;
	}

	public static <F, S> Comparator<Pair<F, S>> comparatorUFirst(Comparator<F> firstComparator, Comparator<S> secondComparator) {
		return (left, right) -> compareReverse(left, right, firstComparator, secondComparator);
	}

	public Pair() {
		this(null, null);
	}

	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	public Pair(Pair<F, S> other) {
		this.first = other.first;
		this.second = other.second;
	}

	public F getFirst() {
		return first;
	}

	public S getSecond() {
		return second;
	}

	public Object[] toArray() {
		return new Object[] { first, second };
	}

	public Pair<F, S> putIn(Map<F, S> map) {
		return new Pair<>(first, map.put(first, second));
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair<?, ?>))
			return false;
		Pair<?, ?> p = (Pair<?, ?>) obj;
		return first.equals(p.first) && second.equals(p.second);
	}

	@Override
	public int hashCode() {
		return 31 * first.hashCode() + second.hashCode();
	}

	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}

	@Override
	public F getKey() {
		return getFirst();
	}

	@Override
	public S getValue() {
		return getSecond();
	}

	@Override
	public S setValue(S value) {
		throw new UnsupportedOperationException();
	}

}
