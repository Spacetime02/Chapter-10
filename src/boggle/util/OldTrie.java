package boggle.util;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class OldTrie extends AbstractCollection<String> implements Collection<String> {

	private final char lowerBound;
	private final int degree;
	private final Node root;

	private int size;
	private int uniqueSize;
	private int depth;

	public OldTrie(char low, char high) {
		lowerBound = low;
		degree = high - low + 1;
		root = new Node(degree);
		size = 0;
		uniqueSize = 0;
		depth = 0;
	}

	public OldTrie(OldTrie other) {
		lowerBound = other.lowerBound;
		degree = other.degree;
		root = new Node(degree);
		size = 0;
		uniqueSize = 0;
		depth = 0;
		UniqueIterator iter = other.new UniqueIterator();
		while (iter.hasNext())
			add(iter.next(), iter.multiplicity());
	}

	private static class Node {

		private int multiplicity;
		public Node[] children;

		private Node(int degree) {
			multiplicity = 0;
			children = new Node[degree];
		}

	}

	public int uniqueSize() {
		return uniqueSize;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	public int getMultiplicity(Object o) {
		Node n = node((String) o);
		return n == null ? 0 : n.multiplicity;
	}

	public boolean contains(Object o, int multiplicity) {
		return getMultiplicity(o) >= multiplicity;
	}

	@Override
	public boolean contains(Object o) {
		return contains(o, 1);
	}

	public UniqueIterator uniqueIterator() {
		return new UniqueIterator();
	}

	@Override
	public MultiplicitousIterator iterator() {
		return new MultiplicitousIterator();
	}

	@Override
	public String[] toArray() {
		Iterator<String> iter = iterator();
		String[] arr = new String[size];
		for (int i = 0; i < size; i++)
			arr[i] = iter.next();
		return arr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		T[] arr = a;
		if (a.length < size)
			arr = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
		else if (a.length > size)
			a[size] = null;
		Iterator<String> iter = iterator();
		for (int i = 0; i < size; i++)
			arr[i] = (T) iter.next();
		return arr;
	}

	public boolean add(String str, int multiplicity) {
		if (multiplicity < 0)
			return remove(str, -multiplicity);
		else if (multiplicity == 0)
			return false;
		if (str.length() > depth)
			depth = str.length();
		Node n = mkNode(str);
		if (n.multiplicity == 0)
			uniqueSize++;
		size += multiplicity;
		n.multiplicity += multiplicity;
		return true;
	}

	@Override
	public boolean add(String str) {
		return add(str, 1);
	}

	public boolean remove(Object o, int multiplicity) {
		String str = (String) o;
		Node n = node(str);
		if (multiplicity < 0)
			return add((String) o, -multiplicity);
		else if (multiplicity == 0 || n == null || n.multiplicity == 0)
			return false;
		else if (n.multiplicity > multiplicity) {
			size -= multiplicity;
			n.multiplicity -= multiplicity;
		} else {
			size -= n.multiplicity;
			uniqueSize--;
			n.multiplicity = 0;
			simplify(str);
		}
		return true;
	}

	@Override
	public boolean remove(Object o) {
		return remove(o, 1);
	}

	public boolean removeAll(Object o) {
		return remove(o, Integer.MAX_VALUE);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object obj : c)
			if (!contains(obj))
				return false;
		return true;
	}

	public boolean addAll(OldTrie trie) {
		boolean modified = false;
		UniqueIterator iter = trie.new UniqueIterator();
		while (iter.hasNext())
			modified |= add(iter.next(), iter.multiplicity());
		return modified;
	}

	@Override
	public boolean addAll(Collection<? extends String> c) {
		boolean modified = false;
		for (String str : c)
			modified |= add(str);
		return modified;
	}

	public boolean removeAll(Map<?, Integer> m) {
		boolean modified = false;
		for (Entry<?, Integer> entry : m.entrySet())
			modified |= remove(entry.getKey(), entry.getValue());
		return modified;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		for (Object obj : c)
			modified |= remove(obj);
		return modified;
	}

	@Override
	public boolean removeIf(Predicate<? super String> filter) {
		boolean modified = false;
		UniqueIterator iter = uniqueIterator();
		while (iter.hasNext()) {
			if (filter.test(iter.next())) {
				iter.remove();
				modified = true;
			}
		}
		return modified;
	}

	public boolean retainAll(Map<?, Integer> m) {
		boolean modified = false;
		Iterator<String> it = uniqueIterator();
		while (it.hasNext()) {
			String str = it.next();
			Integer mult = m.get(str);
			if (mult == null || mult == 0) {
				removeAll(str);
				modified = true;
			} else {
				Node n = node(str);
				if (mult < n.multiplicity) {
					remove(str, n.multiplicity - mult);
					modified = true;
				}
			}
		}
		return modified;
	}

	@Override
	public void clear() {
		root.children = new Node[degree];
		root.multiplicity = 0;
		size = 0;
		uniqueSize = 0;
	}

	private Node node(String str) {
		int len = str.length();
		Node node = root, child;
		for (int i = 0; i < len; i++) {
			child = node.children[str.charAt(i) - lowerBound];
			if (child == null)
				return null;
			node = child;
		}
		return node;
	}

	private Node mkNode(String str) {
		int len = str.length(), idx;
		Node node = root, child;
		for (int i = 0; i < len; i++) {
			idx = str.charAt(i) - lowerBound;
			child = node.children[idx];
			if (child == null)
				node = node.children[idx] = new Node(degree);
			else
				node = child;
		}
		return node;
	}

	private void simplify(String str) {
		int len = str.length();
		Node[] nodes = new Node[len];
		int nodeCt = len;
		Node node = root, child;
		for (int i = 0; i < len; i++) {
			child = node.children[str.charAt(i) + lowerBound];
			if (child == null) {
				nodeCt = i;
				break;
			}
			node = nodes[i] = child;
		}
		for (int i = nodeCt - 1; i >= 0; i--)
			if (nodes[i].multiplicity == 0)
				nodes[i - 1].children[str.charAt(i) + lowerBound] = null;
	}

	private class UniqueIterator implements Iterator<String> {

		private final Deque<Node> nodeStack;
		private final Deque<String> strStack;

		private int index;
		private int multiplicity;
		private String lastVal;

		private UniqueIterator() {
			nodeStack = new ArrayDeque<>(depth);
			strStack = new ArrayDeque<>(depth);
			nodeStack.push(root);
			strStack.push("");
			index = 0;
		}

		@Override
		public boolean hasNext() {
			return index < uniqueSize;
		}

		@Override
		public String next() {
			if (!hasNext())
				throw new NoSuchElementException();
			index++;
			Node node = nodeStack.pop(), child;
			String current = strStack.pop();
			while (node.multiplicity == 0) {
				for (int i = degree - 1; i >= 0; i--) {
					child = node.children[i];
					if (child != null) {
						nodeStack.push(child);
						strStack.push(current + (char) (i + lowerBound));
						System.out.println("pushed " + strStack.peek());
					}
				}
				System.out.println();
				System.out.println(Arrays.toString(nodeStack.stream().mapToInt(n -> n.multiplicity).toArray()));
				// System.out.println(nodeStack.size());
				System.out.println(strStack);
				node = nodeStack.pop();
				current = strStack.pop();
			}
			System.out.println(current);
			// System.out.println();
			// System.out.println(nodeStack.size());
			// System.out.println(strStack);
			System.out.println("m" + (multiplicity = node.multiplicity));
			return lastVal = current;
			// if (node == root)
			// return lastVal = "";
			// int i = strStack.size();
			// char[] arr = new char[i];
			// Iterator<Character> iter = strStack.iterator();
			// while (i > 0) {
			// arr[--i] = current;
			// current = iter.next();
			// }
			// return lastVal = String.valueOf(arr);
		}

		@Override
		public void remove() {
			if (lastVal == null)
				throw new IllegalStateException();
			removeAll(lastVal);
			lastVal = "";
		}

		public int multiplicity() {
			return multiplicity;
		}

		void decMultiplicity() {
			multiplicity--;
		}

		public String repeat() {
			return lastVal;
		}

		void clearRepeat() {
			lastVal = null;
		}

	}

	private class MultiplicitousIterator extends UniqueIterator {

		@Override
		public boolean hasNext() {
			return multiplicity() > 0 || super.hasNext();
		}

		@Override
		public String next() {
			if (!hasNext())
				throw new NoSuchElementException();
			if (multiplicity() == 0)
				super.next();
			decMultiplicity();
			return repeat();
		}

		@Override
		public void remove() {
			if (repeat() == null)
				throw new IllegalStateException();
			removeAll(repeat());
			clearRepeat();
		}

	}

//	public static class DescendantView extends Collection<String> {
//
//		private DescendantView(int index) {
//			
//		}
//
//	}

	public void printStructure(PrintStream stream) {
		// stream.println(uniqueSize);
		Iterator<String> iter = uniqueIterator();
		while (iter.hasNext())
			iter.next();
		// stream.println(iter.next());
		// stream.println(Arrays.toString(toArray()));
		// printStructure(stream, root, 0, "");
	}

	private void printStructure(PrintStream stream, Node node, int depth, String str) {
		for (int i = 0; i < depth; i++)
			stream.print("|");
		stream.printf("%1d %s%n", node.multiplicity, str);
		depth++;
		for (int i = 0; i < degree; i++) {
			Node child = node.children[i];
			if (child != null)
				printStructure(stream, child, depth, str + (char) (i + lowerBound));
		}

	}
}
