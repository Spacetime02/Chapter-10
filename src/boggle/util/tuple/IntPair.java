package boggle.util.tuple;

public class IntPair {

	public final int value1;
	public final int value2;

	public IntPair(int value1, int value2) {
		this.value1 = value1;
		this.value2 = value2;
	}

	@Override
	public String toString() {
		return "(" + value1 + ", " + value2 + ")";
	}

}
