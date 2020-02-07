package boggle.util.multiset;


public class BasicTrie {

	private BasicTrie[] children;
	
	private int multiplicity;
	
	private int deg;
	private int size;
	private int dim;
	
	public BasicTrie() {
		children = new BasicTrie[26];
		deg = 0;
		size = 0;
		dim = 0;
	}
	
	public int size() {
		return size;
	}
	
	public int dimension() {
		return dim;
	}
	
	public 
	
//	public BasicTrie getNode(String str) {
//		if (str == )
//	}
	
}
