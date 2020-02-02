package boggle.util.tuple;

public class Pair<T, U> {

	public final T t;

	public final U u;

	public Pair(T t, U u) {
		this.t = t;
		this.u = u;
	}

	@Override
	public String toString() {
		return "(" + t + ", " + u + ")";
	}

}
