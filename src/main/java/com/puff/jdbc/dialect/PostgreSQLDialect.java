package com.puff.jdbc.dialect;

import com.puff.jdbc.core.DBType;

/**
 * PostgreSQLDialect.
 */
public class PostgreSQLDialect extends Dialect {

	@Override
	public DBType getDbType() {
		return DBType.POSTGRESQL;
	}

	@Override
	public PageSql buildPageSql(String sql, int pageNumber, int pageSize) {
		if (pageNumber <= 0) {
			pageNumber = 1;
		}
		StringBuilder sb = new StringBuilder(sql.length() + 30);
		sb.append(sql).append(" limit ? offset ? ");
		PageSql pageSql = new PageSql();
		pageSql.setStart(pageSize);
		pageSql.setEnd((pageNumber - 1) * pageSize);
		pageSql.setSql(sb.toString());
		return pageSql;
	}

}
