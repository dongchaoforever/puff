package com.puff.jdbc.dialect;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import com.puff.exception.ConverterExecption;
import com.puff.framework.converter.ConverterUtil;
import com.puff.framework.utils.StringUtil;
import com.puff.jdbc.core.ColumnInfo;
import com.puff.jdbc.core.ColumnProperty;
import com.puff.jdbc.core.DBType;

public abstract class Dialect {

	public abstract DBType getDbType();

	@SuppressWarnings("unchecked")
	public List<Object> fillStatement(PreparedStatement pst, Object... paras) throws SQLException {
		List<Object> obj = null;
		if (paras.length > 0) {
			Object value = paras[0];
			if (value instanceof Collection) {
				obj = fillStatementArray(pst, ((Collection<Object>) value).toArray());
			} else {
				obj = fillStatementArray(pst, paras);
			}
		}
		return obj;
	}

	private List<Object> fillStatementArray(PreparedStatement pst, Object[] paras) throws SQLException {
		List<Object> obj = new ArrayList<Object>();
		for (int i = 0, length = paras.length; i < length;) {
			Object value = paras[i++];
			setValue(pst, value, i);
			obj.add(value);
		}
		return obj;
	}

	private void fillStatementList(PreparedStatement pst, List<Object> paras) throws SQLException {
		for (int i = 0, size = paras.size(); i < size;) {
			Object value = paras.get(i++);
			setValue(pst, value, i);
		}
	}

	private void setValue(PreparedStatement stmt, Object value, int index) throws SQLException {
		if (value == null) {
			stmt.setObject(index, null);
		} else if (String.class.equals(value.getClass())) {
			stmt.setString(index, (String) value);
		} else if (Integer.class.equals(value.getClass())) {
			stmt.setInt(index, (Integer) value);
		} else if (Long.class.equals(value.getClass())) {
			stmt.setLong(index, (Long) value);
		} else if (Double.class.equals(value.getClass())) {
			stmt.setDouble(index, (Double) value);
		} else if (Float.class.equals(value.getClass())) {
			stmt.setFloat(index, (Float) value);
		} else if (value instanceof Calendar) {
			long inMillis = ((Calendar) value).getTimeInMillis();
			stmt.setTimestamp(index, new Timestamp(inMillis));
		} else if (value instanceof Date) {
			long time = ((Date) value).getTime();
			stmt.setTimestamp(index, new Timestamp(time));
		} else if (value instanceof java.util.Date) {
			long time = ((java.util.Date) value).getTime();
			stmt.setTimestamp(index, new Timestamp(time));
		} else if (Boolean.class.equals(value.getClass())) {
			stmt.setBoolean(index, (Boolean) value);
		} else if (Byte.class.equals(value.getClass())) {
			stmt.setByte(index, (Byte) value);
		} else if (Short.class.equals(value.getClass())) {
			stmt.setShort(index, (Short) value);
		} else if (Character.class.equals(value.getClass())) {
			stmt.setString(index, value.toString());
		} else if (BigDecimal.class.equals(value.getClass())) {
			stmt.setBigDecimal(index, (BigDecimal) value);
		} else {
			stmt.setObject(index, value);
		}
	}

	public String buildCountSql(String sql) {
		sql = sql.replaceAll("(\\s)+", " ").toLowerCase();
		sql = replaceFormatSqlOrderBy(sql);
		if (sql.indexOf("distinct") > -1) {
			return new StringBuilder(sql.length() + 24).append("select count(*) cnt from ( ").append(sql).append(" ) temp_ ").toString();
		} else {
			int lastIndex = sql.lastIndexOf("from");
			int index = sql.indexOf("from");
			if (index != -1) {
				if (index != lastIndex) {
					return new StringBuilder(sql.length() + 24).append("select count(*) cnt from ( ").append(sql).append(" ) temp_ ").toString();
				} else {
					String subSql = sql.substring(index);
					return new StringBuilder(subSql.length() + 24).append("select count(*) cnt ").append(subSql).toString();
				}
			}
		}
		return sql;
	}

	public abstract PageSql buildPageSql(String sql, int pageNumber, int pageSize);

	public Object convertValue(ColumnProperty cp, ResultSet rs) throws SQLException, IOException, ConverterExecption {
		Object value = null;
		ColumnInfo info = cp.getColumnInfo();
		int type = info.getType();
		int idx = info.getIdx();
		Type javaType = cp.getJavaType();
		switch (type) {
		case Types.CHAR:
			String str = rs.getString(idx);
			value = StringUtil.empty(str) ? str : ConverterUtil.string2Other(str, javaType);
			break;
		case Types.VARCHAR:
			str = rs.getString(idx);
			value = StringUtil.empty(str) ? str : ConverterUtil.string2Other(str, javaType);
			break;
		case Types.LONGVARCHAR:
			str = rs.getString(idx);
			value = StringUtil.empty(str) ? str : ConverterUtil.string2Other(str, javaType);
			break;
		case Types.NUMERIC:
			BigDecimal bd = rs.getBigDecimal(idx);
			if (bd != null) {
				value = ConverterUtil.bigDecimal2Other(bd, javaType);
			}
			break;
		case Types.DECIMAL:
			bd = rs.getBigDecimal(idx);
			if (bd != null) {
				value = ConverterUtil.bigDecimal2Other(bd, javaType);
			}
			break;
		case Types.BIT:
			value = rs.getBoolean(idx);
			break;
		case Types.TINYINT:
			value = rs.getByte(idx);
			break;
		case Types.SMALLINT:
			Short s = rs.getShort(idx);
			if (s != null) {
				value = ConverterUtil.bigDecimal2Other(new BigDecimal(s), javaType);
			}
			break;
		case Types.INTEGER:
			Integer i = rs.getInt(idx);
			if (i != null) {
				value = ConverterUtil.bigDecimal2Other(new BigDecimal(i), javaType);
			}
			break;
		case Types.BIGINT:
			Long l = rs.getLong(idx);
			if (l != null) {
				value = ConverterUtil.bigDecimal2Other(new BigDecimal(l), javaType);
			}
			break;
		case Types.REAL:
			value = rs.getFloat(idx);
			break;
		case Types.FLOAT:
			Double d = rs.getDouble(idx);
			if (d != null) {
				value = ConverterUtil.double2Other(d, javaType);
			}
			break;
		case Types.DOUBLE:
			d = rs.getDouble(idx);
			if (d != null) {
				value = ConverterUtil.double2Other(d, javaType);
			}
			break;
		case Types.BINARY:
			byte[] bytes = rs.getBytes(idx);
			if (bytes != null) {
				value = ConverterUtil.bytes2Other(bytes, javaType);
			}
			break;
		case Types.VARBINARY:
			bytes = rs.getBytes(idx);
			value = ConverterUtil.bytes2Other(bytes, javaType);
			break;
		case Types.LONGVARBINARY:
			bytes = rs.getBytes(idx);
			if (bytes != null) {
				value = ConverterUtil.bytes2Other(bytes, javaType);
			}
			break;
		case Types.DATE:
			Date date = rs.getDate(idx);
			if (date != null) {
				value = ConverterUtil.date2Other(date, javaType);
			}
			break;
		case Types.TIME:
			Time time = rs.getTime(idx);
			if (time != null) {
				value = ConverterUtil.time2Other(time, javaType);
			}
			break;
		case Types.TIMESTAMP:
			Timestamp timestamp = rs.getTimestamp(idx);
			if (timestamp != null) {
				value = ConverterUtil.timestamp2Other(timestamp, javaType);
			}
			break;
		case Types.BLOB:
			Blob blob = rs.getBlob(idx);
			if (blob != null) {
				bytes = ConverterUtil.handleBlob(blob);
				value = ConverterUtil.bytes2Other(bytes, javaType);
			}
			break;
		case Types.CLOB:
			Clob clob = rs.getClob(idx);
			if (clob != null) {
				value = ConverterUtil.handleClob(clob);
			}
			break;
		case Types.NCLOB:
			clob = rs.getClob(idx);
			if (clob != null) {
				value = ConverterUtil.handleClob(clob);
			}
			break;
		case Types.ARRAY:
			value = rs.getArray(idx);
			break;
		case Types.REF:
			value = rs.getRef(idx);
			break;
		default:
			break;
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public List<Object> fillPageStatement(PreparedStatement pst, PageSql pageSql, Object... paras) throws SQLException {
		List<Object> obj = null;
		if (paras != null && paras.length > 0) {
			Object value = paras[0];
			if (value instanceof Collection) {
				obj = new ArrayList<Object>((List<Object>) value);
			} else {
				obj = new ArrayList<Object>();
				for (Object object : paras) {
					obj.add(object);
				}
			}
			obj.add(pageSql.getStart());
			obj.add(pageSql.getEnd());
			fillStatementList(pst, obj);
		} else {
			obj = fillStatement(pst, pageSql.getStart(), pageSql.getEnd());
		}
		return obj;
	}

	public Object getData(ResultSet rs, int[] types, int i) throws SQLException, IOException {
		switch (types[i]) {
		case Types.CHAR:
			return rs.getString(i);
		case Types.VARCHAR:
			return rs.getString(i);
		case Types.LONGVARCHAR:
			return rs.getString(i);
		case Types.NUMERIC:
			return rs.getBigDecimal(i);
		case Types.DECIMAL:
			return rs.getBigDecimal(i);
		case Types.BIT:
			return rs.getBoolean(i);
		case Types.TINYINT:
			return rs.getByte(i);
		case Types.SMALLINT:
			return rs.getShort(i);
		case Types.INTEGER:
			return rs.getInt(i);
		case Types.BIGINT:
			return rs.getLong(i);
		case Types.REAL:
			return rs.getFloat(i);
		case Types.FLOAT:
			return rs.getDouble(i);
		case Types.DOUBLE:
			return rs.getDouble(i);
		case Types.BINARY:
			return rs.getBytes(i);
		case Types.VARBINARY:
			return rs.getBytes(i);
		case Types.LONGVARBINARY:
			return rs.getBytes(i);
		case Types.DATE:
			return rs.getDate(i);
		case Types.TIME:
			return rs.getTime(i);
		case Types.TIMESTAMP:
			return rs.getTimestamp(i);
		case Types.BLOB:
			Blob blob = rs.getBlob(i);
			return blob != null ? ConverterUtil.handleBlob(blob) : null;
		case Types.CLOB:
			Clob clob = rs.getClob(i);
			return clob != null ? ConverterUtil.handleClob(clob) : null;
		case Types.NCLOB:
			clob = rs.getClob(i);
			return clob != null ? ConverterUtil.handleClob(clob) : null;
		case Types.ARRAY:
			return rs.getArray(i);
		case Types.REF:
			return rs.getRef(i);
		default:
			return null;
		}
	}

	protected String replaceFormatSqlOrderBy(String sql) {
		int index = sql.lastIndexOf("order by");
		if (index > sql.lastIndexOf(")")) {
			String sql1 = sql.substring(0, index);
			String sql2 = sql.substring(index);
			sql2 = sql2.replaceAll(
					"[oO][rR][dD][eE][rR] [bB][yY] [\u4e00-\u9fa5a-zA-Z0-9_.']+((\\s)+(([dD][eE][sS][cC])|([aA][sS][cC])))?(( )*,( )*[\u4e00-\u9fa5a-zA-Z0-9_.']+(( )+(([dD][eE][sS][cC])|([aA][sS][cC])))?)*",
					"");
			return sql1 + sql2;
		}
		return sql;
	}

}
