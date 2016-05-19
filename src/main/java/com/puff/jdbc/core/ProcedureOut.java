package com.puff.jdbc.core;

public class ProcedureOut {

	private String name;

	private Object value;

	private JdbcType jdbcType;

	public ProcedureOut() {
		super();
	}

	public ProcedureOut(String name, JdbcType jdbcType) {
		super();
		this.name = name;
		this.jdbcType = jdbcType;
	}

	public ProcedureOut(String name, Object value, JdbcType jdbcType) {
		this(name, jdbcType);
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public JdbcType getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(JdbcType jdbcType) {
		this.jdbcType = jdbcType;
	}

}
