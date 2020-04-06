package maxit.util;

import java.util.Objects;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;

import maxit.util.function.ByteUnaryOperator;
import maxit.util.function.FloatUnaryOperator;
import maxit.util.function.ShortUnaryOperator;

public final class Search {

	private Search() {}

	/**
	 * @param op a weakly increasing function.
	 * @return The smallest possible x in the range [(byte) Math.min(b1, b2), Math.max(b1, b2)] for which Byte.compare(op.applyAsByte(x), (byte) 0) >= 0.
	 */
	public static byte binarySearch(ByteUnaryOperator op, byte b1, byte b2) {
		Objects.requireNonNull(op);
		return (byte) binarySearch0((long l) -> op.applyAsByte((byte) l), b1, b2);
	}

	/**
	 * @param op a weakly increasing function.
	 * @return The smallest possible x in the range [(short) Math.min(b1, b2), Math.max(b1, b2)] for which Short.compare(op.applyAsShort(x), (short) 0) >= 0.
	 */
	public static short binarySearch(ShortUnaryOperator op, short b1, short b2) {
		Objects.requireNonNull(op);
		return (short) binarySearch0((long l) -> op.applyAsShort((short) l), b1, b2);
	}

	/**
	 * @param op a weakly increasing function.
	 * @return The smallest possible x in the range [Math.min(b1, b2), Math.max(b1, b2)] for which Integer.compare(op.applyAsInt(x), 0) >= 0.
	 */
	public static int binarySearch(IntUnaryOperator op, int b1, int b3) {
		Objects.requireNonNull(op);
		return (int) binarySearch0((long l) -> op.applyAsInt((int) l), b1, b3);
	}

	/**
	 * @param op a weakly increasing function.
	 * @return The smallest possible x in the range [Math.min(b1, b2), Math.max(b1, b2)] for which Long.compare(op.applyAsLong(x), 0L) >= 0.
	 */
	public static long binarySearch(LongUnaryOperator op, int b1, int b2) {
		Objects.requireNonNull(op);
		return binarySearch0(op, b1, b2);
	}

	/**
	 * @param op a weakly increasing function.
	 * @return The smallest possible x in the range [Math.min(b1, b2), Math.max(b1, b2)] for which Float.compare(op.applyAsFloat(x), 0f) >= 0.
	 */
	public static float binarySearch(FloatUnaryOperator op, float b1, float b2) {
		Objects.requireNonNull(op);
		try {
			return toFloat((int) binarySearch0((long l) -> toInt(op.applyAsFloat(toFloat((int) l))), toInt(b1), toInt(b2)));
		} catch (ArithmeticException e) {
//			return Float.NaN;
			throw e;
		}
	}

	/**
	 * @param op a weakly increasing function.
	 * @return The smallest possible x in the range [Math.min(b1, b2), Math.max(b1, b2)] for which Double.compare(op.applyAsFloat(x), 0d) >= 0.
	 */
	public static double binarySearch(DoubleUnaryOperator op, double b1, double b2) {
		Objects.requireNonNull(op);
		try {
			return toDouble(binarySearch0((long l) -> toLong(op.applyAsDouble(toDouble(l))), toLong(b1), toLong(b2)));
		} catch (ArithmeticException e) {
			return Double.NaN;
		}
	}

	private static long binarySearch0(LongUnaryOperator op, long b1, long b2) {
		if (b1 == b2)
			return b1;

		long min = Math.min(b1, b2);
		long max = Math.max(b1, b2);

		if (op.applyAsLong(max) < 0)
			throw new ArithmeticException();

		long mid = mid(min, max);
		do {
			if (isPositive(op.applyAsLong(mid)))
				max = mid;
			else
				min = mid + 1;
			mid = mid(min, max);
		} while (min < max);
		assert isPositive(op.applyAsLong(mid));
		return mid;
	}

	private static boolean isPositive(float val) {
		return compare(val, 0f) >= 0;
	}

	private static boolean isPositive(double val) {
		return compare(val, 0d) >= 0;
	}

	private static int compare(float f1, float f2) {
		return Float.compare(f1, f2);
	}

	private static int compare(double d1, double d2) {
		return Double.compare(d1, d2);
	}

	/*
	 * Preserves order.
	 */
	private static int toInt(float val) {
		if (Float.isFinite(val))
			return isPositive(val) ? Float.floatToIntBits(val) : -Float.floatToIntBits(-val);
		else
			throw new ArithmeticException();
	}

	/*
	 * Preserves order.
	 */
	private static float toFloat(int val) {
		return val >= 0 ? Float.intBitsToFloat(val) : -Float.intBitsToFloat(-val);
	}

	/*
	 * Preserves order.
	 */
	private static long toLong(double val) {
		if (Double.isFinite(val))
			return isPositive(val) ? Double.doubleToLongBits(val) : -Double.doubleToLongBits(-val);
		else
			throw new ArithmeticException();
	}

	/*
	 * Preserves order.
	 */
	private static double toDouble(long val) {
		return val >= 0 ? Double.longBitsToDouble(val) : -Double.longBitsToDouble(-val);
	}

	/**
	 * Avoids rounding errors and overflows.
	 */
	private static long mid(long min, long max) {
		return (min ^ max) < 0 ? Math.floorDiv(max + min, 2) : min + Math.floorDiv(max - min, 2);
	}

}
