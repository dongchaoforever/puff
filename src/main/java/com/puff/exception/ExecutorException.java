package com.puff.exception;


public class ExecutorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6239998455794970759L;

	public ExecutorException() {
		super();
	}

	public ExecutorException(String message) {
		super(message);
	}
	
	public ExecutorException(Throwable cause) {
		super(cause);
	}
	
	public ExecutorException(String message, Throwable cause) {
		super(message, cause);
	}


}
