package boggle.util.multiset;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;

// Can be either a signed or unsigned multiset.
public interface Multiset<E> extends Collection<E> {

	int size();

	/**
	 * Equivalent to {@code supportSet().size()}.
	 * 
	 * @author Evan Bailey
	 * @return The size of this multiset's support set.
	 */
	int dimension();

	boolean isEmpty();

	int getMultiplicity(Object o);

	int setMultiplicity(E e, int multiplicity);

	boolean contains(Object o, int multiplicity);

	boolean contains(Object o);

	Iterator<E> iterator();

	/**
	 * Equivalent to {@code supportSet().iterator()}.
	 * 
	 * @author Evan Bailey
	 * @return An iterator iterating over each element only once.
	 */
	Iterator<E> supportIterator();

	Object[] toArray();

	/**
	 * Equivalent to {@code supportSet().toArray()}.
	 * 
	 * @author Evan Bailey
	 * @return An array containing each of this multiset's elements only once.
	 */
	Object[] toSupportArray();

	<T> T[] toArray(T[] a);

	/**
	 * Equivalent to {@code supportSet().toArray(a)}.
	 * 
	 * @author Evan Bailey
	 * @return An array containing each of this multiset's elements only once.
	 */
	<T> T[] toSupportArray(T[] a);

	boolean add(E e, int multiplicity);

	boolean add(E e);

	boolean remove(Object o, int multiplicity);

	boolean remove(Object o);

	/**
	 * Equivalent to {@code supportSet().remove(a)}. This sets the multiplicity of the given element to zero.
	 * 
	 * @author Evan Bailey
	 * @return Whether this multiset was modified as a result of the removal.
	 */
	boolean purge(Object o);

	boolean containsAll(Collection<?> c);

	/**
	 * 
	 * @param m
	 * @return Whether this multiset contains each element of {@code m} with at least the multiplicity that it has in
	 *         {@code m}
	 */
	boolean containsAllMultiple(Collection<?> m);

	void clear();

	boolean equals(Object o);

	int hashCode();

	default Spliterator<E> spliterator() {
		return Spliterators.spliterator(this, 0);
	}

	Set<E> supportSet();

	Map<E, Integer> multiplicityMap();
}
