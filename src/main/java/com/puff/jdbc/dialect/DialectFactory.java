package com.puff.jdbc.dialect;

public class DialectFactory {

	public Dialect getDialect(String driverClass) {
		if (driverClass.indexOf("oracle") != -1) {
			return new OracleDialect();
		}
		if (driverClass.indexOf("mysql") != -1) {
			return new MysqlDialect();
		}
		if (driverClass.indexOf("sqlserver") != -1) {
			return new SqlServerDialect();
		}
		if (driverClass.indexOf("postgres") != -1) {
			return new PostgreSQLDialect();
		}
		return new OracleDialect();
	}
}
