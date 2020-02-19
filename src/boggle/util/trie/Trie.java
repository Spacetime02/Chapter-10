package boggle.util.trie;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

import sample_code.weiss.util.NoSuchElementException;

public abstract class Trie implements Collection<String> {

	public static Trie unmodifiableTrie(Trie trie) {
		return trie == null ? null : new Unmodifiable(trie);
	}

	@Override
	public Iterator<String> iterator() {
		return new Iter();
	}

	public Iterator<char[]> arrayIterator() {
		return new ArrIter();
	}

	public abstract int getMultiplicity();

	public abstract boolean containsEmpty();

	@Override
	public abstract String[] toArray();

	public abstract char[][] toCharArrays();

	public abstract <T> T[] toCharArrays(T[] a);

	public abstract boolean add(char[] e);

	public abstract boolean addAllArrays(Collection<? extends char[]> c);

	public abstract Trie after(char c);

	public abstract Trie[] getChildren();

	public abstract Trie after(String prefix);

	public abstract Trie after(char[] prefix);

	public abstract String valueWithParents();

	@Override
	public String toString() {
		return Arrays.toString(toArray());
	}

	private class Iter implements Iterator<String> {

		private final Iterator<char[]> arrIter = arrayIterator();

		@Override
		public boolean hasNext() {
			return arrIter.hasNext();
		}

		@Override
		public String next() {
			return String.valueOf(arrIter.next());
		}

		@Override
		public void remove() {
			arrIter.remove();
		}

	}

	private class ArrIter implements Iterator<char[]> {

		private final Deque<Trie> trieStack = new ArrayDeque<>();
		private final Deque<char[]> arrStack = new ArrayDeque<>();

		{
			trieStack.push(Trie.this);
			arrStack.push(new char[0]);
		}

		private int index = 0;
		private Trie current = null;
		private char[] curArr = null;
		private int multiplicity = 0;
		private boolean removable = false;

		private ArrIter() {
			trieStack.push(Trie.this);
			arrStack.push(new char[0]);
		}

		@Override
		public boolean hasNext() {
			return index < size();
		}

		@Override
		public char[] next() {
			if (!hasNext())
				throw new NoSuchElementException();
			while (multiplicity == 0) {
				current = trieStack.pop();
				curArr = arrStack.pop();
				multiplicity = current.getMultiplicity();
				// System.out.printf("%02d/%02d %02d*%s%n", index, size(), multiplicity, String.valueOf(curArr));
				char[] nextArr;
				Trie child;
				for (char c = 'Z'; c >= 'A'; c--) {
					child = current.after(c);
					if (child != null) {
						trieStack.push(child);
						nextArr = Arrays.copyOf(curArr, curArr.length + 1);
						nextArr[curArr.length] = c;
						arrStack.push(nextArr);
					}
				}
			}
			multiplicity--;
			index++;
			removable = true;
			return Arrays.copyOf(curArr, curArr.length);
		}

		@SuppressWarnings("unlikely-arg-type")
		@Override
		public void remove() {
			if (!removable)
				throw new IllegalStateException();
			Trie.this.remove(curArr);
			removable = false;
			index--;
		}

	}

	private static class Unmodifiable extends Trie {

		private Trie backing;

		private Unmodifiable(Trie backing) {
			this.backing = backing;
		}

		@Override
		public int size() {
			return backing.size();
		}

		@Override
		public boolean isEmpty() {
			return backing.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return backing.contains(o);
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return backing.toArray(a);
		}

		@Override
		public boolean add(String e) {
			throw new UnsupportedOperationException("add");
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException("remove");
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return backing.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends String> c) {
			throw new UnsupportedOperationException("add");
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException("remove");
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException("retain");
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException("clear");
		}

		@Override
		public int getMultiplicity() {
			return backing.getMultiplicity();
		}

		@Override
		public boolean containsEmpty() {
			return backing.containsEmpty();
		}

		@Override
		public String[] toArray() {
			return backing.toArray();
		}

		@Override
		public char[][] toCharArrays() {
			return backing.toCharArrays();
		}

		@Override
		public <T> T[] toCharArrays(T[] a) {
			return backing.toCharArrays(a);
		}

		@Override
		public boolean add(char[] e) {
			throw new UnsupportedOperationException("add");
		}

		@Override
		public boolean addAllArrays(Collection<? extends char[]> c) {
			throw new UnsupportedOperationException("add");
		}

		@Override
		public Trie after(char c) {
			return backing.after(c);
		}

		@Override
		public Trie[] getChildren() {
			return backing.getChildren();
		}

		@Override
		public Trie after(String prefix) {
			return backing.after(prefix);
		}

		@Override
		public Trie after(char[] prefix) {
			return backing.after(prefix);
		}

		@Override
		public String valueWithParents() {
			return backing.valueWithParents();
		}

		@Override
		public String toString() {
			return backing.toString();
		}

	}

}
