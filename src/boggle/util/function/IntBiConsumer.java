package boggle.util.function;

import java.util.Objects;

@FunctionalInterface
public interface IntBiConsumer {

	void accept(int value1, int value2);

	default IntBiConsumer andThen(IntBiConsumer after) {
		Objects.requireNonNull(after);
		return (value1, value2) -> {
			accept(value1, value2);
			after.accept(value1, value2);
		};
	}

}
