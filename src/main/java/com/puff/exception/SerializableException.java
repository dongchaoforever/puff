package com.puff.exception;

/**
 */
public class SerializableException extends RuntimeException {

	private static final long serialVersionUID = 3073643491559111709L;

	public SerializableException(String className) {
		super(className);
	}

	public SerializableException() {
	}
}
