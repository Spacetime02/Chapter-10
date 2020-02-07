package boggle.util.multiset;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import sample_code.weiss.util.ConcurrentModificationException;
import sample_code.weiss.util.NoSuchElementException;

/**
 * A trie structure representing an unsigned multiset of element type <code>byte[]</code>, stored as a nybble-indexed
 * tree.
 * 
 * @author Evan Bailey
 */
public class Trie implements Multiset<byte[]> {

	private Trie parent;
	private Trie[][] children = new Trie[16][];

	private int modCount = 0;

	private int size = 0;
	private int dim = 0;
	private int childCount = 0;
	private int maxDepth = 0;
	private int multiplicity = 0;

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
		return dim;
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
		// if (c instanceof )
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
		// return setMultiplicity(o, 0) > 0;
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
		return toReturn;
	}

	private class Iter implements Iterator<byte[]> {

		private SupportIter supportIter;
		// private byte[] last = null;

		private Iter() {
			supportIter = new SupportIter();
		}

		@Override
		public boolean hasNext() {
			return multiplicity > 0 || supportIter.hasNext();
		}

		@Override
		public byte[] next() {}

	}

	private class SupportIter implements Iterator<byte[]> {

		private final Deque<Trie> trieStack = new ArrayDeque<>(maxDepth);
		private final Deque<byte[]> dataStack = new ArrayDeque<>(maxDepth);

		private final int dim = Trie.this.dim;
		private final int modCount = Trie.this.modCount;

		private int index = 0;

		private byte[] lastData = null;
		private int lastMult = 0;

		private SupportIter() {
			trieStack.push(Trie.this);
			dataStack.push(new byte[] {});
		}

		@Override
		public boolean hasNext() {
			return index < dim;
		}

		@Override
		public byte[] next() {
			if (modCount != Trie.this.modCount)
				throw new ConcurrentModificationException();
			else if (index >= dim)
				throw new NoSuchElementException();
			index++;
			Trie[][] children;
			Trie trie = trieStack.pop();
			Trie child;
			byte[] data = dataStack.pop();
			byte[] childData;
			while (trie.multiplicity == 0) {
				children = trie.children;
				for (int i = 15; i >= 0; i--)
					for (int j = 15; j >= 0; j--) {
						child = children[i][j];
						if (child != null) {
							trieStack.push(child);
							childData = Arrays.copyOf(data, data.length + 1);
							childData[data.length] = (byte) (i << 4 | j);
							dataStack.push(childData);
						}
					}
				trie = trieStack.pop();
				data = dataStack.pop();
			}
			return data;
		}

	}

}
