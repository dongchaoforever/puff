package com.puff.jdbc.core;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.puff.exception.ExceptionUtil;
import com.puff.framework.container.DBInfoContainer;
import com.puff.framework.utils.StringUtil;
import com.puff.jdbc.dialect.Dialect;

public class BeanResultSetHandler<T> {
	private Dialect dialect;
	private Class<T> clazz;
	private ResultSet rs;
	private List<ColumnProperty> columnPropertyList;
	private Map<String, ColumnInfo> columnInfo;

	public BeanResultSetHandler(Dialect dialect, Class<T> clazz, ResultSet rs) {
		this.dialect = dialect;
		this.clazz = clazz;
		this.rs = rs;
		this.columnPropertyList = DBInfoContainer.getColumnProperty(clazz);
		this.columnInfo = getColumnInfo();
	}

	public T getResult() throws Exception {
		if (columnPropertyList == null && columnPropertyList.size() == 0) {
			return null;
		}
		T t = clazz.newInstance();
		for (ColumnProperty cp : columnPropertyList) {
			String columnName = cp.getColumnName();
			if (columnInfo.containsKey(columnName)) {
				Object value = null;
				try {
					cp.setColumnInfo(columnInfo.get(columnName));
					FieldProcessor fieldProcessor = cp.getFieldProcessor();
					if (fieldProcessor != null) {
						value = fieldProcessor.load(rs.getObject(cp.getColumnInfo().getIdx()));
					} else {
						value = dialect.convertValue(cp, rs);
					}
					if (value != null) {
						cp.invokeSet(t, value);
					}
				} catch (IllegalArgumentException e) {
					String err = "the class {0} field {1} is {2} ,but the value is {3}";
					throw new IllegalArgumentException(StringUtil.replaceArgs(err, clazz.getName(), cp.getFieldName(), cp.getJavaType(), value.getClass().getName()), e);
				} catch (Exception e) {
					ExceptionUtil.throwRuntime(e);
				}
			}
		}
		return t;
	}

	private Map<String, ColumnInfo> getColumnInfo() {
		Map<String, ColumnInfo> map = new HashMap<String, ColumnInfo>();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			for (int i = 1; i <= count; i++) {
				String columnName = StringUtil.empty(rsmd.getColumnLabel(i), rsmd.getColumnName(i)).toLowerCase();
				map.put(columnName, new ColumnInfo(columnName, i, rsmd.getColumnType(i)));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return map;
	}
}
