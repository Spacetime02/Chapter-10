package sample_code.weiss.util;

public class EmptyStackException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmptyStackException() {}

	/*
	 * Constructs a EmptyStackException with a detail message.
	 * 
	 * @param msg the detail mesage pertaining to this exception.
	 */
	public EmptyStackException(String msg) {
		super(msg);
	}
}