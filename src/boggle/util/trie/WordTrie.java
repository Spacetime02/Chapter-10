package boggle.util.trie;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public final class WordTrie extends Trie {

	private WordTrie parent;
	private final WordTrie[] children = new WordTrie[26];
	private int mult = 0;
	private int size = 0;

	public WordTrie() {
		this(null);
	}

	private WordTrie(WordTrie parent) {
		this.parent = parent;
	}

	public WordTrie(Collection<?> c) {
		for (Object obj : c) {
			if (obj instanceof String)
				add((String) obj);
			else if (obj instanceof char[])
				add((char[]) obj);
			else
				throw new IllegalArgumentException();
		}
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean contains(Object o) {
		if (o == null)
			return false;
		WordTrie after;
		if ((after = afterMutable(o)) == null)
			return false;
		return after != null && after.mult > 0;
	}

	@Override
	public int getMultiplicity() {
		return mult;
	}

	@Override
	public boolean containsEmpty() {
		return mult > 0;
	}

	@Override
	public String[] toArray() {
		String[] arr = new String[size];
		int index = 0;
		Iterator<String> iter = iterator();
		while (iter.hasNext())
			arr[index++] = iter.next();
		return arr;
	}

	@Override
	public char[][] toCharArrays() {
		char[][] arr = new char[size][];
		int index = 0;
		Iterator<char[]> iter = arrayIterator();
		while (iter.hasNext())
			arr[index++] = iter.next();
		return arr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		T[] arr;
		if (a == null || a.length < size)
			arr = (T[]) Array.newInstance(a.getClass().componentType(), size);
		else
			arr = a;
		Iterator<String> iter = iterator();
		int index = 0;
		while (iter.hasNext())
			arr[index++] = (T) iter.next();
		if (arr.length > size)
			arr[size] = null;
		return arr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toCharArrays(T[] a) {
		T[] arr;
		if (a == null || a.length < size)
			arr = (T[]) Array.newInstance(a.getClass().componentType(), size);
		else
			arr = a;
		Iterator<char[]> iter = arrayIterator();
		int index = 0;
		while (iter.hasNext())
			arr[index++] = (T) iter.next();
		if (arr.length > size)
			arr[size] = null;
		return arr;
	}

	@Override
	public boolean add(String str) {
		if (str == null)
			return false;
		add(str, 0);
		return true;
	}

	private void add(String str, int index) {
		if (index == str.length())
			mult++;
		else {
			char c = str.charAt(index);
			WordTrie trie = getChildMutable(c);
			if (trie == null)
				setChild(c, trie = new WordTrie());
			trie.add(str, index + 1);
		}
		if (index == 0) {
			WordTrie trie = this;
			do
				trie.size++;
			while ((trie = trie.parent) != null);
		} else
			size++;
	}

	@Override
	public boolean add(char[] arr) {
		if (arr == null)
			return false;
		add(arr, 0);
		return true;
	}

	private void add(char[] arr, int index) {
		if (index == arr.length)
			mult++;
		else {
			char c = arr[index];
			WordTrie trie = getChildMutable(c);
			if (trie == null)
				setChild(c, trie = new WordTrie());
			trie.add(arr, index + 1);
		}
		if (index == 0) {
			WordTrie trie = this;
			do
				trie.size++;
			while ((trie = trie.parent) != null);
		} else
			size++;
	}

	@Override
	public boolean remove(Object o) {
		if (o == null)
			return false;
		WordTrie after;
		if ((after = afterMutable(o)) == null)
			return false;
		if (after != null && after.mult > 0) {
			after.mult--;
			WordTrie trie = this;
			do
				trie.size--;
			while ((trie = trie.parent) != null);
			if (after.mult == 0)
				reduce(o);
			return true;
		}
		return false;
	}

	private void reduce(Object o) {
		if (o instanceof String)
			reduce((String) o, 0);
		else if (o instanceof char[])
			reduce((char[]) o, 0);
	}

	private WordTrie reduce(String str, int index) {
		char c = str.charAt(index);
		if (index < str.length() - 1)
			setChild(c, getChildMutable(c).reduce(str, index + 1));
		if (mult > 0)
			return this;
		for (WordTrie child : children)
			if (child != null)
				return this;
		return null;
	}

	private WordTrie reduce(char[] arr, int index) {
		char c = arr[index];
		if (index < arr.length - 1)
			setChild(c, getChildMutable(c).reduce(arr, index + 1));
		if (mult > 0)
			return this;
		for (WordTrie child : children)
			if (child != null)
				return this;
		return null;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object obj : c)
			if (!contains(obj))
				return false;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends String> c) {
		for (String str : c)
			add(str);
		return true;
	}

	@Override
	public boolean addAllArrays(Collection<? extends char[]> c) {
		for (char[] arr : c)
			add(arr);
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean mod = false;
		for (Object o : c)
			mod |= remove(o);
		return mod;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean mod = false;
		Iterator<char[]> arrIter = arrayIterator();
		while (arrIter.hasNext()) {
			char[] arr = arrIter.next();
			if (!(c.contains(arr) || c.contains(String.valueOf(arr)))) {
				arrIter.remove();
				mod = true;
			}
		}
		return mod;
	}

	@Override
	public void clear() {
		mult = 0;
		size = 0;
		for (int i = 0; i < 26; i++) {
			children[i].parent = null;
			children[i] = null;
		}
	}

	@Override
	public Trie after(char c) {
		return Trie.unmodifiableTrie(getChildMutable(c));
	}

	private WordTrie getChildMutable(char c) {
		if (c < 'A' || c > 'Z')
			return null;
		return children[c - 'A'];
	}

	private void setChild(char c, WordTrie trie) {
		int index = c - 'A';
		if (children[index] != null)
			children[index].parent = null;
		children[index] = trie;
		if (trie != null)
			children[index].parent = this;
	}

	@Override
	public Trie[] getChildren() {
		Trie child;
		Trie[] toReturn = new WordTrie[26];
		int pointer = 0;
		for (int i = 0; i < 26; i++)
			if ((child = children[i]) != null)
				toReturn[pointer++] = child;
		return Arrays.copyOf(toReturn, pointer);
	}

	@Override
	public Trie after(String prefix) {
		return after(prefix);
	}

	@Override
	public Trie after(char[] prefix) {
		return afterMutable(prefix);
	}

	private WordTrie afterMutable(Object o) {
		if (o instanceof String)
			return afterMutable((String) o, 0);
		else if (o instanceof char[])
			return afterMutable((char[]) o, 0);
		else
			return null;
	}

	private WordTrie afterMutable(String prefix, int index) {
		if (index == prefix.length())
			return this;
		char c = prefix.charAt(index);
		WordTrie trie = getChildMutable(c);
		return trie == null ? null : trie.afterMutable(prefix, index + 1);
	}

	private WordTrie afterMutable(char[] prefix, int index) {
		if (index == prefix.length)
			return this;
		char c = prefix[index];
		WordTrie trie = getChildMutable(c);
		return trie == null ? null : trie.afterMutable(prefix, index + 1);
	}

	@Override
	public String valueWithParents() {
		if (parent == null)
			return "";
		for (char c = 'A'; c <= 'Z'; c++)
			if (parent.getChildMutable(c) == this)
				return parent.valueWithParents() + c;
		throw new RuntimeException("Parent does not contain child!");
	}

}
