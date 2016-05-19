package com.puff.jdbc.dialect;

import com.puff.jdbc.core.DBType;

public class MysqlDialect extends Dialect {

	@Override
	public DBType getDbType() {
		return DBType.MYSQL;
	}

	@Override
	public PageSql buildPageSql(String sql, int pageNumber, int pageSize) {
		if (pageNumber <= 0) {
			pageNumber = 1;
		}
		int start = pageNumber - 1;
		StringBuilder sb = new StringBuilder(sql.length() + 30);
		sb.append(sql).append(" limit ? , ? ");
		PageSql pageSql = new PageSql();
		pageSql.setStart(start * pageSize);
		pageSql.setEnd(pageSize);
		pageSql.setSql(sb.toString());
		return pageSql;
	}

}