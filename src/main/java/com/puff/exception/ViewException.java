package com.puff.exception;

/**
 * ViewException
 */
public class ViewException extends RuntimeException {

	private static final long serialVersionUID = -6448434551667513804L;

	public ViewException() {
		super();
	}

	public ViewException(String message) {
		super(message);
	}

	public ViewException(Throwable cause) {
		super(cause);
	}

	public ViewException(String message, Throwable cause) {
		super(message, cause);
	}
}
