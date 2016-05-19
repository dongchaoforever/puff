package com.puff.jdbc.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.puff.exception.JdbcException;
import com.puff.framework.annotation.PKType;
import com.puff.framework.container.DBInfoContainer;
import com.puff.framework.utils.StringUtil;

/**
 * 
 * @author Chao
 * 
 */
public class SqlBuilder {

	private static final String INSERT_KEY = "______insert__";
	private static final String QUERY_KEY = "______query__";

	/**
	 * 
	 * @param o
	 * @return
	 */
	public static final SQL buildInsertSQL(Object o, DataBase dataBase) {
		SQL sql = getInsertSQL(o, dataBase);
		String cache_sql = SQLCache.getSql(o.getClass().getName() + INSERT_KEY);
		if (StringUtil.empty(cache_sql)) {
			StringBuilder sbSQL = new StringBuilder();
			sbSQL.append("insert into " + sql.getTableName() + " ");
			StringBuilder columnName = new StringBuilder();
			columnName.append("(");
			StringBuilder valueName = new StringBuilder();
			valueName.append("(");
			int i = 0;
			for (String cName : sql.getCloumnName()) {
				if (i++ > 0) {
					columnName.append(",");
					valueName.append(",");
				}
				columnName.append(cName);
				valueName.append("?");
			}
			columnName.append(")");
			valueName.append(")");
			sbSQL.append(columnName + " values " + valueName);
			cache_sql = sbSQL.toString();
			SQLCache.cache(o.getClass().getName() + INSERT_KEY, cache_sql);
		}
		sql.setSql(cache_sql);
		return sql;
	}

	public static final String buildDeleteSQL(Class<?> clazz) {
		String pkName = DBInfoContainer.getPkName(clazz);
		if (StringUtil.empty(pkName)) {
			throw new JdbcException("can not build delte sql , because the class:" + clazz + " has not primarykey ");
		}
		String tableName = DBInfoContainer.getTableName(clazz);
		StringBuilder deleteSQL = new StringBuilder(100).append("delete from " + tableName + " where " + pkName + "=?");
		return deleteSQL.toString();
	}

	public static final SQL buildUpdateSQL(Object obj) {
		SQL sql = getUpdateSQL(obj, true);
		String pkName = sql.getPrimaryKey();
		if (StringUtil.empty(pkName)) {
			throw new JdbcException("can not build update sql , because the class:" + obj.getClass() + " has not primarykey ");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("update " + sql.getTableName() + " set ");
		List<String> cloumnNames = sql.getCloumnName();
		int i = 0;
		for (String cloumn : cloumnNames)
			if (!cloumn.equals(pkName)) {
				if (i++ > 0) {
					sb.append(",");
				}
				sb.append(cloumn + " = ? ");
			}
		sql.setSql(sb.append(" where " + pkName + "=? ").toString());
		return sql;
	}

	public static final SQL buildUpdateWithoutNullSQL(Object obj) {
		SQL sql = getUpdateSQL(obj, false);
		String pkName = sql.getPrimaryKey();
		if (StringUtil.empty(pkName)) {
			throw new JdbcException("can not build update sql , because the class:" + obj.getClass() + " has not primarykey ");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("update " + sql.getTableName() + " set ");
		List<String> cloumnNames = sql.getCloumnName();
		int i = 0;
		for (String cloumn : cloumnNames)
			if (!cloumn.equals(pkName)) {
				if (i++ > 0) {
					sb.append(",");
				}
				sb.append(cloumn + " = ? ");
			}
		sql.setSql(sb.append(" where " + pkName + "=? ").toString());
		return sql;
	}

	public final static String buildQuerySQL(Class<?> clazz) {
		String sql = SQLCache.getSql(clazz.getName() + QUERY_KEY);
		if (StringUtil.empty(sql)) {
			String tableName = DBInfoContainer.getTableName(clazz);
			StringBuilder querySQL = new StringBuilder(100);
			querySQL.append("select ");
			List<String> columnNames = DBInfoContainer.getColumns(clazz);
			for (int i = 0, size = columnNames.size(); i < size; i++) {
				if (i != 0) {
					querySQL.append(",");
				}
				querySQL.append(columnNames.get(i));
			}
			querySQL.append(" from ").append(tableName).append(" ");
			sql = querySQL.toString();
			SQLCache.cache(clazz.getName() + QUERY_KEY, sql);
		}
		return sql;
	}

	public static final String buildQueryByIdSQL(Class<?> clazz) {
		String buildQuerySQL = buildQuerySQL(clazz);
		String pkName = DBInfoContainer.getPkName(clazz);
		if (StringUtil.empty(pkName)) {
			throw new JdbcException("can not buildQueryByIdSQL , because the class:" + clazz + " has not primarykey ");
		}
		StringBuilder querySQL = new StringBuilder();
		querySQL.append(buildQuerySQL).append("where " + pkName + "=?");
		return querySQL.toString();
	}

	private static SQL getInsertSQL(Object obj, DataBase dataBase) {
		SQL sql = new SQL();
		List<String> columnName = new ArrayList<String>();
		List<Object> paramValue = new ArrayList<Object>();
		List<ColumnProperty> list = DBInfoContainer.getColumnProperty(obj.getClass());
		for (ColumnProperty cp : list) {
			if (cp.isAlias()) {
				continue;
			}
			String name = cp.getColumnName();
			columnName.add(name);
			try {
				if (cp.getPkType() != null) {
					PKType keyType = cp.getPkType();
					boolean auto = (keyType.equals(PKType.AUTO) || keyType.equals(PKType.AUTO_SEQUENCE))
							&& (dataBase.getDbType().equals(DBType.MYSQL) || dataBase.getDbType().equals(DBType.POSTGRESQL));
					sql.setAuto(auto);
					sql.setPrimaryKeyPro(cp);
					if (auto) {
						columnName.remove(name);
						continue;
					}
					Object value = PKGenerator.getPKValue(obj, cp, dataBase);
					cp.invokeSet(obj, value);
					paramValue.add(value);
					sql.setPrimaryKey(name);
					sql.setPrimaryKeyValue((Serializable) value);
				} else {
					Object value = cp.invokeGet(obj);
					FieldProcessor processor = cp.getFieldProcessor();
					if (processor != null) {
						value = processor.insert(value);
					}
					paramValue.add(value);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		sql.setCloumnName(columnName);
		sql.setParamsValue(paramValue);
		sql.setTableName(DBInfoContainer.getTableName(obj));
		return sql;
	}

	private static SQL getUpdateSQL(Object obj, boolean canNull) {
		SQL sql = new SQL();
		List<String> columnName = new ArrayList<String>();
		List<Object> paramValue = new ArrayList<Object>();
		List<ColumnProperty> list = DBInfoContainer.getColumnProperty(obj.getClass());
		for (ColumnProperty cp : list) {
			if (cp.isAlias()) {
				continue;
			}
			String name = cp.getColumnName();
			try {
				Object value = cp.invokeGet(obj);
				if (!canNull && value == null) {
					continue;
				}
				columnName.add(name);
				FieldProcessor processor = cp.getFieldProcessor();
				if (processor != null) {
					value = processor.insert(value);
				}
				if (cp.getPkType() != null) {
					sql.setPrimaryKey(name);
					sql.setPrimaryKeyValue((Serializable) value);
				}
				paramValue.add(value);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		sql.setCloumnName(columnName);
		sql.setParamsValue(paramValue);
		sql.setTableName(DBInfoContainer.getTableName(obj));
		Serializable pkValue = sql.getPrimaryKeyValue();
		List<Object> paramsValue = sql.getParamsValue();
		paramsValue.remove(pkValue);
		paramsValue.add(pkValue);
		return sql;
	}

	private static class SQLCache {
		private static final Map<String, String> map = new ConcurrentHashMap<String, String>();

		public static String getSql(String key) {
			return map.get(key);
		}

		public static void cache(String key, String cache_sql) {
			if (map.size() > 10000) {
				map.clear();
			}
			map.put(key, cache_sql);
		}
	}

}
