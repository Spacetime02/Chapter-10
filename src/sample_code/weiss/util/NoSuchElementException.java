package sample_code.weiss.util;

public class NoSuchElementException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a NoSuchElementException with no detail message.
	 */
	public NoSuchElementException() {}

	/*
	 * Constructs a NoSuchElementException with a detail message.
	 * 
	 * @param msg the detail mesage pertaining to this exception.
	 */
	public NoSuchElementException(String msg) {
		super(msg);
	}
}