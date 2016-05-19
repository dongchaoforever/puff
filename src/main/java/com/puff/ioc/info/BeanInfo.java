package com.puff.ioc.info;

import java.util.ArrayList;
import java.util.List;

import com.puff.framework.annotation.BeanScope;

public class BeanInfo {
	private String id;
	private Class<?> clazz;
	private String className;
	private BeanScope scope;
	private boolean transaction;
	private List<Property> properties = new ArrayList<Property>();
	private Object instance;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public BeanScope getScope() {
		return scope;
	}

	public void setScope(BeanScope scope) {
		this.scope = scope;
	}

	public boolean isTransaction() {
		return transaction;
	}

	public void setTransaction(boolean transaction) {
		this.transaction = transaction;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public Object getInstance() {
		return instance;
	}

	public void setInstance(Object instance) {
		this.instance = instance;
	}

	public void addProperties(Property propInfo) {
		properties.add(propInfo);
	}

	@Override
	public String toString() {
		return "BeanInfo [id=" + id + ", className=" + className + ", scope=" + scope + ", properties=" + properties + ", instance=" + instance + "]";
	}

}