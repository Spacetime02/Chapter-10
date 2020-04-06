package maxit.core;

public class InvalidMoveException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidMoveException() {
		super();
	}

	public InvalidMoveException(String s) {
		super(s);
	}

	public InvalidMoveException(Position movePos) {
		super(getErrMsg(movePos));
	}

	public InvalidMoveException(Throwable cause) {
		super(cause);
	}

	public InvalidMoveException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidMoveException(Position move, Throwable cause) {
		super(getErrMsg(move));
	}

	private static String getErrMsg(Position movePos) {
		return "Illegal move at position " + movePos + ".";
	}

}
