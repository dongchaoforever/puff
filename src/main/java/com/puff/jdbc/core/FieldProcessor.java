package com.puff.jdbc.core;

public interface FieldProcessor {

	public Object insert(Object value);

	public Object load(Object value);
}