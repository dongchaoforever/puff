package com.puff.jdbc.core;

public enum JdbcType {
	BIT(-7), 
	TINYINT(-6), 
	SMALLINT(5), 
	INTEGER(4), 
	BIGINT(-5), 
	FLOAT(6), 
	REAL(7), 
	DOUBLE(8), 
	NUMERIC(2), 
	DECIMAL(3), 
	CHAR(1), 
	VARCHAR(12), 
	LONGVARCHAR(-1), 
	DATE(91), 
	TIME(92), 
	TIMESTAMP(93), 
	BINARY(-2), 
	VARBINARY(-3), 
	LONGVARBINARY(-4), 
	NULL(0), 
	OTHER(1111), 
	JAVA_OBJECT(2000), 
	DISTINCT(2001), 
	STRUCT(2002), 
	ARRAY(2003), 
	BLOB(2004), 
	CLOB(2005), 
	REF(2006), 
	DATALINK(70), 
	BOOLEAN(16),
	ROWID(-8), 
	NCHAR(-15), 
	NVARCHAR(-9), 
	LONGNVARCHAR(-16), 
	NCLOB(2011), 
	SQLXML(2009);

	private int type;

	private JdbcType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
