package boggle.util.multiset;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * A trie structure representing an unsigned multiset of element type <code>byte[]</code>, stored as a nybble-indexed
 * tree.
 * 
 * @author Evan Bailey
 */
public class Trie implements Multiset<byte[]> {

	private Trie parent;
	private Trie[][] children = new Trie[16][];

	private int size = 0;
	private int dimension = 0;
	private int childCount;

	public Trie() {}

	public Trie(Collection<byte[]> c) {
		addAll(c);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int dimension() {
		return dimension;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public int getMultiplicity(Object o) {
		// TODO getMultiplicity
	}

	@Override
	public int setMultiplicity(byte[] e, int multiplicity) {
		// TODO setMultiplicity
	}

	@Override
	public boolean contains(Object o, int multiplicity) {
		return getMultiplicity(o) >= multiplicity;
	}

	@Override
	public boolean contains(Object o) {
		return contains(o, 1);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c)
			if (!contains(o))
				return false;
		return true;
	}

	@Override
	public boolean containsAllMultiple(Collection<?> c) {
		Multiset<?> ms;
		if (c instanceof Multiset<?>)
			ms = (Multiset<?>) c;
		else
			ms = new HashMultiset<>(c);
		for (Map.Entry<?, Integer> entry : ms.multiplicityMap().entrySet()) {
			Integer val = entry.getValue();
			if (getMultiplicity(entry.getKey()) < (val == null ? 0 : val))
				return false;
		}
		return true;
	}

	@Override
	public Iterator<byte[]> iterator() {
		// TODO iterator
	}

	@Override
	public Iterator<byte[]> supportIterator() {
		// TODO supportIterator
	}

	@Override
	public Object[] toArray() {
		// TODO toArray
	}

	@Override
	public Object[] toSupportArray() {
		// TODO toSupportArray
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO toArray
	}

	@Override
	public <T> T[] toSupportArray(T[] a) {
		// TODO toSupportArray
	}

	@Override
	public boolean add(byte[] e, int multiplicity) {
		// TODO add
	}

	@Override
	public boolean add(byte[] e) {
		return add(e, 1);
	}

	@Override
	public boolean addAll(Collection<? extends byte[]> c) {
		if (c instanceof )
	}

	@Override
	public boolean remove(Object o, int multiplicity) {
		// TODO remove
	}

	@Override
	public boolean remove(Object o) {
		return remove(o, 1);
	}

	@Override
	public boolean purge(Object o) {
		return setMultiplicity(o, 0);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public Set<byte[]> supportSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<byte[], Integer> multiplicityMap() {
		// TODO Auto-generated method stub
		return null;
	}

	public Trie[] getChildren() {
		int ptr = 0;
		Trie[] toReturn = new Trie[childCount];
		for (Trie[] group : children)
			if (group != null)
				for (Trie child : group)
					if (child != null)
						toReturn[ptr++] = child;
	}

}
