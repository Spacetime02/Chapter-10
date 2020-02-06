package boggle.util.multiset;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HashMultiset<E> implements Multiset<E> {

	private final HashMap<E, Integer> map;

	private int size;

	public HashMultiset() {
		map = new HashMap<>();
		size = 0;
	}

	public HashMultiset(int initialCapacity) {
		map = new HashMap<>(initialCapacity);
		size = 0;
	}

	public HashMultiset(int initialCapacity, float loadFactor) {
		map = new HashMap<>(initialCapacity, loadFactor);
		size = 0;
	}

	public HashMultiset(Map<E, Integer> multiplicityMap) {
		map = new HashMap<>(multiplicityMap);
		size = 0;
		for (Integer i : multiplicityMap.values())
			size += i;
	}

	public HashMultiset(Collection<E> c) {
		map = new HashMap<>(c.size());
		if (c instanceof Multiset<?>)
			for (Map.Entry<E, Integer> e : map.entrySet())
				add(e.getKey(), e.getValue());
		else
			for (E e : c)
				add(e);
		size = c.size();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int dimension() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public int getMultiplicity(Object o) {
		return map.getOrDefault(o, 0);
	}

	@Override
	public int setMultiplicity(E e, int multiplicity) {
		if (multiplicity < 0)
			throw new IllegalArgumentException();
		Integer old;
		if (multiplicity == 0)
			old = map.remove(e);
		else
			old = map.put(e, multiplicity);
		return old == null ? 0 : old;
	}

}
