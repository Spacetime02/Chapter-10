package othello.util.function;

import java.util.Objects;

@FunctionalInterface
public interface ByteUnaryOperator {

	byte applyAsByte(byte operand);

	default ByteUnaryOperator compose(ByteUnaryOperator before) {
		Objects.requireNonNull(before);
		return (byte v) -> applyAsByte(before.applyAsByte(v));
	}

	default ByteUnaryOperator andThen(ByteUnaryOperator after) {
		Objects.requireNonNull(after);
		return (byte t) -> after.applyAsByte(applyAsByte(t));
	}

	static ByteUnaryOperator identity() {
		return t -> t;
	}

}
