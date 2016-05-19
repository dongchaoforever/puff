package com.puff.exception;


public class JdbcException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6239998455794970759L;

	public JdbcException() {
		super();
	}

	public JdbcException(String message) {
		super(message);
	}
	
	public JdbcException(Throwable cause) {
		super(cause);
	}
	
	public JdbcException(String message, Throwable cause) {
		super(message, cause);
	}


}
