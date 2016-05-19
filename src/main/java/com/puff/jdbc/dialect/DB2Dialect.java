package com.puff.jdbc.dialect;

import com.puff.jdbc.core.DBType;

public class DB2Dialect extends Dialect {

	@Override
	public DBType getDbType() {
		return DBType.DB2;
	}

	@Override
	public PageSql buildPageSql(String sql, int pageNumber, int pageSize) {
		if (pageNumber <= 0) {
			pageNumber = 1;
		}
		StringBuilder sb = new StringBuilder(sql.length() + 100);
		int satrt = (pageNumber - 1) * pageSize;
		int end = pageNumber * pageSize;
		sb.append("select * from ( select temp.*, rownumber() over() as rn from ( ");
		sb.append(sql);
		sb.append(" ) temp ) as a where a.rn between ? and ?");
		PageSql pageSql = new PageSql();
		pageSql.setEnd(end);
		pageSql.setSql(sb.toString());
		pageSql.setStart(satrt);
		return pageSql;
	}
}