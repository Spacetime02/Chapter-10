package maxit.util.algo;

import java.util.Objects;

import maxit.util.function.FloatUnaryOperator;

public final class Search {

	private Search() {
	}

	/**
	 * @param op a weakly increasing function.
	 * @return The largest possible float in the range [min, max] for which op is negative.
	 */
	public static float continuousBinarySearch(FloatUnaryOperator op, float min, float max) {
		Objects.requireNonNull(op);
		if (Float.isNaN(min) || Float.isNaN(max) || isPositive(op.applyAsFloat(min)))
			return Float.NaN;
		float mid;

//		while (compare())
//			;

		// if (compare(min, Math.nextUp(max)) > 0 || !Float.isFinite(min) || !Float.isFinite(max) || isPositive(op.applyAsFloat(min)))
//			return Float.NaN;
//		return continuousBinarySearch(op, min, max, min + 0.5f * (max - min));
		return Float.NaN;
	}

	private static float continuousBinarySearch(FloatUnaryOperator op, float min, float max, float mid) {
//		Arrays.binarySearch(arr, x);
		return Float.NaN;
	}

	private static boolean isPositive(float val) {
		return compare(val, 0f) >= 0;
	}

	private static int compare(float f1, float f2) {
		return Float.compare(f1, f2);
	}

}
