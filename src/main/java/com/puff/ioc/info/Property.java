package com.puff.ioc.info;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Property {
	private String name;
	private String value;
	private Method method;
	private Field field;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

}
