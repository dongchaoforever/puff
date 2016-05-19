package com.puff.jdbc.core;

import com.puff.core.ClassProperty;
import com.puff.framework.annotation.PKType;

public class ColumnProperty extends ClassProperty {

	private String columnName;
	private ColumnInfo columnInfo;
	private PKType pkType;
	private String seq_name;
	private boolean alias = false;
	private String function;
	private FieldProcessor fieldProcessor;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public ColumnInfo getColumnInfo() {
		return columnInfo;
	}

	public void setColumnInfo(ColumnInfo columnInfo) {
		this.columnInfo = columnInfo;
	}

	public PKType getPkType() {
		return pkType;
	}

	public void setPkType(PKType pkType) {
		this.pkType = pkType;
	}

	public String getSeq_name() {
		return seq_name;
	}

	public void setSeq_name(String seq_name) {
		this.seq_name = seq_name;
	}

	public boolean isAlias() {
		return alias;
	}

	public void setAlias(boolean alias) {
		this.alias = alias;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public FieldProcessor getFieldProcessor() {
		return fieldProcessor;
	}

	public void setFieldProcessor(FieldProcessor fieldProcessor) {
		this.fieldProcessor = fieldProcessor;
	}
}
