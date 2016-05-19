package com.puff.jdbc.dialect;

import com.puff.jdbc.core.DBType;

/**
 * OracleDialect.
 */
public class OracleDialect extends Dialect {

	@Override
	public DBType getDbType() {
		return DBType.ORACLE;
	}

	@Override
	public PageSql buildPageSql(String sql, int pageNumber, int pageSize) {
		if (pageNumber <= 0) {
			pageNumber = 1;
		}
		StringBuilder sb = new StringBuilder(sql.length() + 100);
		int satrt = (pageNumber - 1) * pageSize;
		int end = pageNumber * pageSize;
		sb.append("select * from ( select row_.*, rownum rownum_ from ( ");
		sb.append(sql);
		sb.append(" ) row_ ) where rownum_ > ?").append(" and rownum_ <= ?");
		PageSql pageSql = new PageSql();
		pageSql.setEnd(end);
		pageSql.setSql(sb.toString());
		pageSql.setStart(satrt);
		return pageSql;
	}

}
