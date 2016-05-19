package com.puff.exception;

public class ConverterExecption extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4883895115068054824L;

	public ConverterExecption() {
		super();
	}

	public ConverterExecption(String message, Throwable cause) {
		super(message, cause);
	}

	public ConverterExecption(String message) {
		super(message);
	}

	public ConverterExecption(Throwable cause) {
		super(cause);
	}

	public ConverterExecption(Object source, Class<?> targetType, Throwable error) {
		super(getMessage(source, targetType), error);
	}

	public ConverterExecption(Object source, Class<?> targetType) {
		super(getMessage(source, targetType));
	}

	private static String getMessage(Object source, Class<?> targetType) {
		return "can not convert instance:" + source + " of type:" + source.getClass() + " into type:" + targetType;
	}

}
