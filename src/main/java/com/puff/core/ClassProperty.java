package com.puff.core;

import com.puff.jdbc.core.ClassMethod;

public class ClassProperty {

	private String fieldName;
	private int getMethodIdx;
	private int setMethodIdx;
	private Class<?> javaType;

	public Class<?> getJavaType() {
		return javaType;
	}

	public void setJavaType(Class<?> javaType) {
		this.javaType = javaType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public int getGetMethodIdx() {
		return getMethodIdx;
	}

	public void setGetMethodIdx(int getMethodIdx) {
		this.getMethodIdx = getMethodIdx;
	}

	public int getSetMethodIdx() {
		return setMethodIdx;
	}

	public void setSetMethodIdx(int setMethodIdx) {
		this.setMethodIdx = setMethodIdx;
	}

	public void invokeSet(Object obj, Object value) {
		ClassMethod.invokeMethod(obj, setMethodIdx, value);
	}

	public Object invokeGet(Object obj) {
		return ClassMethod.invokeMethod(obj, getMethodIdx);
	}
}
