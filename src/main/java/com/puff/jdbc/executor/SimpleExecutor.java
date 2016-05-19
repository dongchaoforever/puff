package com.puff.jdbc.executor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.puff.exception.JdbcException;
import com.puff.framework.container.DBInfoContainer;
import com.puff.framework.converter.ConverterUtil;
import com.puff.framework.utils.StringUtil;
import com.puff.jdbc.core.BeanResultSetHandler;
import com.puff.jdbc.core.DataBase;
import com.puff.jdbc.core.DbManager;
import com.puff.jdbc.core.PageRecord;
import com.puff.jdbc.core.Procedure;
import com.puff.jdbc.core.ProcedureOut;
import com.puff.jdbc.core.Record;
import com.puff.jdbc.core.SQL;
import com.puff.jdbc.core.SqlBuilder;
import com.puff.jdbc.dialect.Dialect;
import com.puff.jdbc.dialect.PageSql;

@SuppressWarnings("unchecked")
public class SimpleExecutor implements SqlExecutor {
	private boolean showSql;
	private DataBase dataBase;
	private Dialect dialect;

	private static final Object[] NULL_PARAM = new Object[] {};
	private static final Map<String, SimpleExecutor> pool = new ConcurrentHashMap<String, SimpleExecutor>();

	private SimpleExecutor() {

	}

	public static SimpleExecutor getInstance() {
		return getInstance(DbManager.PUFF_DEFAULT_DATABASE);
	}

	public static SimpleExecutor getInstance(String dataBaseName) {
		SimpleExecutor simpleExecutor = pool.get(dataBaseName);
		if (simpleExecutor == null) {
			DataBase dataBase = DbManager.getDataBase(dataBaseName);
			simpleExecutor = new SimpleExecutor();
			simpleExecutor.dataBase = dataBase;
			simpleExecutor.dialect = dataBase.getDialect();
			simpleExecutor.showSql = dataBase.isShowSql();
			pool.put(dataBaseName, simpleExecutor);
		}
		return simpleExecutor;
	}

	public DataBase getDataBase() {
		return dataBase;
	}

	public SqlExecutor setShowSql(boolean showSql) {
		this.showSql = showSql;
		return this;
	}

	@Override
	public boolean tableExist(String tableName) {
		Connection conn = dataBase.getConnection();
		ResultSet rs = null;
		try {
			DatabaseMetaData dbmt = conn.getMetaData();
			rs = dbmt.getTables(null, null, tableName.toUpperCase(), new String[] { "TABLE" });
			return rs.next();
		} catch (Exception e) {
			throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
		} finally {
			dataBase.close(rs, null, conn);
		}
	}

	@Override
	public int insert(String sql, Object... parameter) {
		return executeUpdate(sql, parameter);
	}

	@Override
	public int insert(String tableName, Map<String, Object> columns) {
		checkNull(columns);
		StringBuilder sql = new StringBuilder();
		sql.append("insert into ").append(tableName);
		StringBuilder columnName = new StringBuilder();
		columnName.append("(");
		StringBuilder valueName = new StringBuilder();
		valueName.append("(");
		int i = 0;
		for (String cName : columns.keySet()) {
			if (i++ > 0) {
				columnName.append(",");
				valueName.append(",");
			}
			columnName.append(cName);
			valueName.append("?");
		}
		columnName.append(")");
		valueName.append(")");
		sql.append(columnName).append(" values ").append(valueName);
		return insert(sql.toString(), columns.values());
	}

	@Override
	public <T> int insert(Class<T> clazz, Map<String, Object> columns) {
		return insert(DBInfoContainer.getTableName(clazz), columns);
	}

	@Override
	public int save(Object obj) {
		SQL insertSql = SqlBuilder.buildInsertSQL(obj, dataBase);
		String sql = insertSql.getSql();
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = dataBase.getConnection();
		int count = 0;
		List<Object> param = null;
		boolean auto = insertSql.isAuto();
		boolean[] sqlRunErr = new boolean[] { false };
		try {
			pst = auto ? conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS) : conn.prepareStatement(sql);
			param = dialect.fillStatement(pst, insertSql.getParamsValue());
			long elapsed = System.currentTimeMillis();
			count = executeUpdate(pst, sqlRunErr);
			sqlCommandReport(sql, elapsed, param);
			if (auto) {
				rs = pst.getGeneratedKeys();
				if (rs.next()) {
					Object pk = ConverterUtil.bigDecimal2Other(new BigDecimal(rs.getInt(1)), insertSql.getPrimaryKeyPro().getJavaType());
					insertSql.getPrimaryKeyPro().invokeSet(obj, pk);
				}
			}
		} catch (Exception e) {
			throw handlerExecption(sqlRunErr, sql, e, param);
		} finally {
			dataBase.close(rs, pst, conn);
		}
		return count;
	}

	@Override
	public int save(Object obj, Map<String, Object> columns) {
		return insert(DBInfoContainer.getTableName(obj), columns);
	}

	@Override
	public int delete(String sql, Object... parameter) {
		return executeUpdate(sql, parameter);
	}

	@Override
	public <T> void deleteInById(Class<T> clazz, List<?> ids) {
		deleteInByColumn(clazz, DBInfoContainer.getPkName(clazz), ids);
	}

	@Override
	public <T> void deleteInByColumn(Class<T> clazz, String column, List<?> ids) {
		deleteInByTable(DBInfoContainer.getTableName(clazz), column, ids);
	}

	@Override
	public void deleteInByTable(String tableName, String column, List<?> ids) {
		if (ids == null || ids.size() == 0) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ").append(tableName);
		sb.append(" where ").append(column);
		if (ids.size() == 1) {
			sb.append(" = ? ");
			delete(sb.toString(), ids.get(0));
			return;
		}
		sb.append(" in ");
		int size = ids.size();
		int count = 0;
		int max = 100;
		if (size > max) {
			if (size % max == 0) {
				count = size / max;
			} else {
				count = size / max + 1;
			}
			for (int i = 0; i < count; i++) {
				StringBuilder temp = new StringBuilder();
				temp.append(" ( ");
				int end = max;
				int endIdx = (i + 1) * max;
				if (i + 1 == count) {
					end = size - (max * i);
					endIdx = size;
				}
				for (int j = 0; j < end;) {
					if (j++ > 0) {
						temp.append(",");
					}
					temp.append("?");
				}
				temp.append(" )");
				delete(sb.toString() + temp, ids.subList(i * max, endIdx));
			}
		} else {
			sb.append(" ( ");
			for (int i = 0; i < size;) {
				if (i++ > 0) {
					sb.append(",");
				}
				sb.append("?");
			}
			sb.append(" )");
			delete(sb.toString(), ids);
		}

	}

	public int deleteByPK(Class<?> clazz, Serializable pk) {
		return executeUpdate(SqlBuilder.buildDeleteSQL(clazz), pk);
	}

	@Override
	public int delete(String tableName, Map<String, Object> columns) {
		checkNull(columns);
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ").append(tableName).append(" where ");
		int i = 0;
		for (String cName : columns.keySet()) {
			if (i++ > 0) {
				sql.append(" and ");
			}
			sql.append(cName).append(" = ").append("?");
		}
		return executeUpdate(sql.toString(), columns.values());
	}

	@Override
	public <T> int delete(Class<T> clazz, Map<String, Object> columns) {
		return delete(DBInfoContainer.getTableName(clazz), columns);
	}

	@Override
	public int updateBySql(String sql, Object... parameter) {
		return executeUpdate(sql, parameter);
	}

	@Override
	public int update(String tableName, Map<String, Object> columns) {
		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(tableName).append(" set");
		int i = 0;
		for (String cName : columns.keySet()) {
			if (i++ > 0) {
				sql.append(" , ");
			}
			sql.append(cName).append(" = ").append("?");
		}
		return updateBySql(sql.toString(), columns.values());
	}

	@Override
	public <T> int update(Class<T> clazz, Map<String, Object> columns) {
		return update(DBInfoContainer.getTableName(clazz), columns);
	}

	@Override
	public int update(Object obj) {
		SQL sql = SqlBuilder.buildUpdateSQL(obj);
		return executeUpdate(sql.getSql(), sql.getParamsValue());
	}

	@Override
	public int updateWithoutNull(Object obj) {
		SQL sql = SqlBuilder.buildUpdateWithoutNullSQL(obj);
		return executeUpdate(sql.getSql(), sql.getParamsValue());
	}

	@Override
	public boolean execute(String sql, Object... parameter) {
		PreparedStatement pst = null;
		Connection conn = dataBase.getConnection();
		List<Object> param = null;
		boolean[] sqlRunErr = new boolean[] { false };
		boolean b = true;
		try {
			pst = conn.prepareStatement(sql);
			param = dialect.fillStatement(pst, parameter);
			long elapsed = System.currentTimeMillis();
			b = execute(pst, sqlRunErr);
			sqlCommandReport(sql, elapsed, param);
		} catch (Exception e) {
			throw handlerExecption(sqlRunErr, sql, e, param);
		} finally {
			dataBase.close(pst, conn);
		}
		return b;
	}

	private boolean execute(PreparedStatement pst, boolean[] sqlRunErr) throws SQLException {
		try {
			return pst.execute();
		} catch (SQLException e) {
			sqlRunErr[0] = true;
			throw e;
		}
	}

	private int executeUpdate(String sql, Object... parameter) {
		PreparedStatement pst = null;
		Connection conn = dataBase.getConnection();
		int count = 0;
		List<Object> param = null;
		boolean[] sqlRunErr = new boolean[] { false };
		try {
			pst = conn.prepareStatement(sql);
			param = dialect.fillStatement(pst, parameter);
			long elapsed = System.currentTimeMillis();
			count = executeUpdate(pst, sqlRunErr);
			sqlCommandReport(sql, elapsed, param);
		} catch (Exception e) {
			throw handlerExecption(sqlRunErr, sql, e, param);
		} finally {
			dataBase.close(pst, conn);
		}
		return count;
	}

	private int executeUpdate(PreparedStatement pst, boolean[] sqlRunErr) throws SQLException {
		try {
			return pst.executeUpdate();
		} catch (SQLException e) {
			sqlRunErr[0] = true;
			throw e;
		}
	}

	@Override
	public void transaction(Atom atom) {
		Connection conn = DataBase.getThreadLocalConnection();
		int transactionLevel = dataBase.getTransactionLevel();
		if (conn != null) {
			try {
				if (conn.getTransactionIsolation() < transactionLevel)
					conn.setTransactionIsolation(transactionLevel);
				boolean result = atom.execute();
				if (result)
					return;
				throw new JdbcException("Notice the outer transaction that the nested transaction return false");
			} catch (Exception e) {
				throw new JdbcException(e);
			}
		}

		Boolean autoCommit = null;
		try {
			conn = dataBase.getConnection();
			autoCommit = conn.getAutoCommit();
			conn.setTransactionIsolation(transactionLevel);
			conn.setAutoCommit(false);
			DataBase.setThreadLocalConnection(conn);
			if (atom.execute()) {
				conn.commit();
			} else {
				conn.rollback();
			}
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			throw e instanceof RuntimeException ? (RuntimeException) e : new JdbcException(e);
		} finally {
			try {
				if (conn != null) {
					if (autoCommit != null) {
						conn.setAutoCommit(autoCommit);
					}
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			DataBase.removeThreadLocalConnection();
		}
	}

	@Override
	public Map<String, Object> call(Procedure procedure) {
		String name = procedure.getName();
		Connection conn = dataBase.getConnection();
		CallableStatement cs = null;
		Map<String, Object> map = new HashMap<String, Object>();
		boolean[] sqlRunErr = new boolean[] { false };
		try {
			cs = conn.prepareCall(name);
			List<Object> in = procedure.getIn();
			int idx = 1;
			int startIdx = 1;
			if (in != null && in.size() > 0) {
				startIdx += in.size();
				for (int size = in.size(); idx <= size; idx++) {
					cs.setObject(idx, in.get(idx - 1));
				}
			}
			List<ProcedureOut> out = procedure.getOut();
			if (out != null && out.size() > 0) {
				for (int i = 0, size = out.size(); i < size; i++) {
					cs.registerOutParameter(idx++, out.get(i).getJdbcType().getType());
				}
			}
			long elapsed = System.currentTimeMillis();
			execute(cs, sqlRunErr);
			if (out != null && out.size() > 0) {
				for (int i = 0, size = out.size(); i < size; i++) {
					map.put(out.get(i).getName(), cs.getObject(startIdx++));
				}
			}
			sqlProcedureReport(name, elapsed, procedure.getIn());
		} catch (Exception e) {
			throw callExecption(sqlRunErr, name, e, procedure.getIn());
		} finally {
			dataBase.close(cs, conn);
		}
		return map;
	}

	@Override
	public int[] batchHandle(String sql, List<Object[]> params, int batchSize) {
		PreparedStatement pst = null;
		Connection conn = null;
		int i = 0;
		int start = 0;
		int size = params.size();
		int[] effect = new int[size];
		if (size == 0) {
			return effect;
		}
		if (batchSize <= 0) {
			batchSize = size;
		}
		boolean[] sqlRunErr = new boolean[] { false };
		boolean autoCommit = true;
		try {
			conn = dataBase.getConnection();
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			while (i < size) {
				Object[] obj = params.get(i);
				dialect.fillStatement(pst, obj);
				pst.addBatch();
				if (++i % batchSize == 0) {
					System.arraycopy(executeBatch(pst, sqlRunErr), 0, effect, start, i - start);
					pst.clearBatch();
					start = i;
				}
			}
			if (i % batchSize != 0) {
				System.arraycopy(executeBatch(pst, sqlRunErr), 0, effect, start, i - start);
			}
			conn.commit();
		} catch (Exception e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
				}
			}
			JdbcException je = handlerExecption(sqlRunErr, sql, e, null);
			throw je;
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
			} catch (SQLException e) {
			}
			dataBase.close(pst, conn);
		}
		return effect;
	}

	@Override
	public <T> int[] batchInsert(List<T> list, int batchSize) {
		PreparedStatement pst = null;
		Connection conn = null;
		int i = 0;
		int start = 0;
		int size = list.size();
		int[] effect = new int[size];
		if (size == 0)
			return effect;
		if (batchSize <= 0)
			batchSize = size;
		SQL sql = null;
		boolean autoCommit = true;
		boolean[] sqlRunErr = new boolean[] { false };
		try {
			conn = dataBase.getConnection();
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			sql = SqlBuilder.buildInsertSQL(list.get(0), dataBase);
			pst = conn.prepareStatement(sql.getSql());
			while (i < size) {
				sql = SqlBuilder.buildInsertSQL(list.get(i), dataBase);
				dialect.fillStatement(pst, sql.getParamsValue());
				pst.addBatch();
				if (++i % batchSize == 0) {
					System.arraycopy(executeBatch(pst, sqlRunErr), 0, effect, start, i - start);
					pst.clearBatch();
					start = i;
				}
			}
			if (i % batchSize != 0)
				System.arraycopy(executeBatch(pst, sqlRunErr), 0, effect, start, i - start);
			conn.commit();
		} catch (Exception e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
				}
			}
			JdbcException je = handlerExecption(sqlRunErr, sql.getSql(), e, null);
			throw je;
		} finally {
			try {
				conn.setAutoCommit(autoCommit);
			} catch (Exception e) {
			}
			dataBase.close(pst, conn);
		}
		return effect;
	}

	private Object executeBatch(PreparedStatement pst, boolean[] sqlRunErr) throws SQLException {
		try {
			return pst.executeBatch();
		} catch (SQLException e) {
			sqlRunErr[0] = true;
			throw e;
		}
	}

	@Override
	public long count(String sql, Object... parameter) {
		if (sql.indexOf("count(") == -1) {
			sql = dialect.buildCountSql(sql);
		}
		return queryLong(sql, parameter);
	}

	private ResultSet executeQuery(PreparedStatement pst, boolean[] sqlRunErr) throws SQLException {
		try {
			return pst.executeQuery();
		} catch (SQLException e) {
			sqlRunErr[0] = true;
			throw e;
		}
	}

	@Override
	public long count(String tableName) {
		return queryLong("select count(*) cnt from " + tableName, NULL_PARAM);
	}

	@Override
	public long count(String tableName, Map<String, Object> columns) {
		checkNull(columns);
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) cnt from ").append(tableName).append(" where ");
		int i = 0;
		for (String cName : columns.keySet()) {
			if (i++ > 0) {
				sql.append(" and ");
			}
			sql.append(cName).append(" = ").append("?");
		}
		return queryLong(sql.toString(), columns.values());
	}

	@Override
	public <T> long count(Class<T> clazz) {
		return count(DBInfoContainer.getTableName(clazz));
	}

	@Override
	public <T> long count(Class<T> clazz, Map<String, Object> columns) {
		return count(DBInfoContainer.getTableName(clazz), columns);
	}

	public <T> T queryByPk(Class<T> clazz, Serializable pk) {
		return query(clazz, SqlBuilder.buildQueryByIdSQL(clazz), pk);
	}

	@Override
	public <T> T query(Class<T> clazz, String sql, Object... parameter) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		T t = null;
		List<Object> param = null;
		boolean[] sqlRunErr = new boolean[] { false };
		try {
			conn = dataBase.getConnection();
			pst = conn.prepareStatement(sql);
			param = dialect.fillStatement(pst, parameter);
			long elapsed = System.currentTimeMillis();
			rs = executeQuery(pst, sqlRunErr);
			sqlQueryReport(sql, elapsed, param);
			if (rs.next()) {
				BeanResultSetHandler<T> handler = new BeanResultSetHandler<T>(dialect, clazz, rs);
				t = handler.getResult();
			}
		} catch (Exception e) {
			JdbcException je = handlerExecption(sqlRunErr, sql, e, param);
			throw je;
		} finally {
			dataBase.close(rs, pst, conn);
		}
		return t;
	}

	@Override
	public <T> T query(Class<T> clazz, Map<String, Object> columns) {
		checkNull(columns);
		StringBuilder sql = new StringBuilder();
		sql.append(SqlBuilder.buildQuerySQL(clazz)).append(" where ");
		int i = 0;
		for (String cName : columns.keySet()) {
			if (i++ > 0) {
				sql.append(" and ");
			}
			sql.append(cName).append(" = ").append("?");
		}
		return query(clazz, sql.toString(), columns.values());
	}

	@Override
	public <T> T query(String sql, Object... parameter) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = dataBase.getConnection();
		List<Object> param = null;
		boolean[] sqlRunErr = new boolean[] { false };
		try {
			pst = conn.prepareStatement(sql);
			param = dialect.fillStatement(pst, parameter);
			long elapsed = System.currentTimeMillis();
			rs = executeQuery(pst, sqlRunErr);
			sqlQueryReport(sql, elapsed, param);
			if (rs.next()) {
				return (T) rs.getObject(1);
			}
		} catch (Exception e) {
			JdbcException je = handlerExecption(sqlRunErr, sql, e, param);
			throw je;
		} finally {
			dataBase.close(rs, pst, conn);
		}
		return null;
	}

	@Override
	public String queryString(String sql, Object... parameter) {
		return ConverterUtil.obj2String(query(sql, parameter));
	}

	@Override
	public int queryInt(String sql, Object... parameter) {
		Integer result = ConverterUtil.obj2Int(query(sql, parameter));
		if (result == null) {
			return 0;
		}
		return result;
	}

	@Override
	public long queryLong(String sql, Object... parameter) {
		Long result = ConverterUtil.obj2Long(query(sql, parameter));
		if (result == null) {
			return 0;
		}
		return result;
	}

	@Override
	public Date queryDate(String sql, Object... parameter) {
		return ConverterUtil.obj2Date(query(sql, parameter));
	}

	@Override
	public Record queryRecord(String sql, Object... parameter) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = dataBase.getConnection();
		Record record = null;
		List<Object> param = null;
		boolean[] sqlRunErr = new boolean[] { false };
		try {
			pst = conn.prepareStatement(sql);
			pst.setFetchSize(1);
			param = dialect.fillStatement(pst, parameter);
			long elapsed = System.currentTimeMillis();
			rs = executeQuery(pst, sqlRunErr);
			sqlQueryReport(sql, elapsed, param);
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] labelNames = new String[count + 1];
			int[] types = new int[count + 1];
			for (int i = 1; i < labelNames.length; i++) {
				String columnLabel = rsmd.getColumnLabel(i);
				if (StringUtil.empty(columnLabel)) {
					columnLabel = rsmd.getColumnName(i);
				}
				labelNames[i] = columnLabel.toLowerCase();
				types[i] = rsmd.getColumnType(i);
			}
			if (rs.next()) {
				record = new Record();
				for (int i = 1; i <= count; i++) {
					Object value = dialect.getData(rs, types, i);
					record.set(labelNames[i], value);
				}
			}
		} catch (Exception e) {
			JdbcException je = handlerExecption(sqlRunErr, sql, e, param);
			throw je;
		} finally {
			dataBase.close(rs, pst, conn);
		}
		return record;
	}

	@Override
	public Record queryRecord(String tableName, Map<String, Object> columns) {
		String columnInfo = DBInfoContainer.getColumns(tableName);
		if (StringUtil.empty(columnInfo)) {
			Connection conn = dataBase.getConnection();
			ResultSet rs = null;
			try {
				DatabaseMetaData dbmt = conn.getMetaData();
				rs = dbmt.getColumns(null, null, tableName.toUpperCase(), null);
				int i = 0;
				StringBuilder sb = new StringBuilder();
				while (rs.next()) {
					if (i++ > 0) {
						sb.append(",");
					}
					sb.append(rs.getObject(4).toString().toLowerCase());
				}
				columnInfo = sb.toString();
				DBInfoContainer.setColumns(tableName, columnInfo);
			} catch (Exception e) {
				columnInfo = "*";
			} finally {
				dataBase.close(rs, null, conn);
			}
		}
		StringBuilder sql = new StringBuilder(200);
		sql.append("select ").append(columnInfo).append(" from ").append(tableName);
		sql.append(" where ");
		int i = 0;
		for (String cName : columns.keySet()) {
			if (i++ > 0) {
				sql.append(" and ");
			}
			sql.append(cName).append(" = ").append("?");
		}
		return queryRecord(sql.toString(), columns.values());
	}

	@Override
	public <T> Record queryRecord(Class<T> clazz, Map<String, Object> columns) {
		return queryRecord(DBInfoContainer.getTableName(clazz), columns);
	}

	@Override
	public <T> List<T> querySimpleList(String sql, Object... parameter) {
		return querySimpleList(new Cast<T>() {
			@Override
			public T cast(Object obj) {
				return (T) obj;
			}
		}, sql, parameter);
	}

	@Override
	public <T> List<T> querySimpleList(Cast<T> cast, String sql, Object... parameter) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = dataBase.getConnection();
		List<T> list = null;
		List<Object> param = null;
		boolean[] sqlRunErr = new boolean[] { false };
		try {
			pst = conn.prepareStatement(sql);
			param = dialect.fillStatement(pst, parameter);
			long elapsed = System.currentTimeMillis();
			rs = executeQuery(pst, sqlRunErr);
			sqlQueryReport(sql, elapsed, param);
			list = new ArrayList<T>();
			while (rs.next()) {
				list.add(cast.cast(rs.getObject(1)));
			}
			return list;
		} catch (Exception e) {
			JdbcException je = handlerExecption(sqlRunErr, sql, e, param);
			throw je;
		} finally {
			dataBase.close(rs, pst, conn);
		}
	}

	@Override
	public List<String> querySimpleListString(String sql, Object... parameter) {
		return querySimpleList(new Cast<String>() {
			@Override
			public String cast(Object obj) {
				return ConverterUtil.obj2String(obj);
			}
		}, sql, parameter);
	}

	@Override
	public List<Integer> querySimpleListInt(String sql, Object... parameter) {
		return querySimpleList(new Cast<Integer>() {
			@Override
			public Integer cast(Object obj) {
				return ConverterUtil.obj2Int(obj);
			}
		}, sql, parameter);
	}

	@Override
	public List<Long> querySimpleListLong(String sql, Object... parameter) {
		return querySimpleList(new Cast<Long>() {
			@Override
			public Long cast(Object obj) {
				return ConverterUtil.obj2Long(obj);
			}
		}, sql, parameter);
	}

	@Override
	public List<Date> querySimpleListDate(String sql, Object... parameter) {
		return querySimpleList(new Cast<Date>() {
			@Override
			public Date cast(Object obj) {
				return ConverterUtil.obj2Date(obj);
			}
		}, sql, parameter);
	}

	@Override
	public <T> List<T> querySimpleListPage(String sql, int pageNumber, int pageSize, Object... parameter) {
		PageSql pageSql = dialect.buildPageSql(sql, pageNumber, pageSize);
		return queryPageList(new Cast<T>() {
			@Override
			public T cast(Object obj) {
				return (T) obj;
			}
		}, pageSql, parameter);
	}

	@Override
	public List<String> querySimpleListPageString(String sql, int pageNumber, int pageSize, Object... parameter) {
		PageSql pageSql = dialect.buildPageSql(sql, pageNumber, pageSize);
		return queryPageList(new Cast<String>() {
			@Override
			public String cast(Object obj) {
				return ConverterUtil.obj2String(obj);
			}
		}, pageSql, parameter);

	}

	@Override
	public List<Integer> querySimpleListPageInt(String sql, int pageNumber, int pageSize, Object... parameter) {
		PageSql pageSql = dialect.buildPageSql(sql, pageNumber, pageSize);
		return queryPageList(new Cast<Integer>() {
			@Override
			public Integer cast(Object obj) {
				return ConverterUtil.obj2Int(obj);
			}
		}, pageSql, parameter);

	}

	@Override
	public List<Long> querySimpleListPageLong(String sql, int pageNumber, int pageSize, Object... parameter) {
		PageSql pageSql = dialect.buildPageSql(sql, pageNumber, pageSize);
		return queryPageList(new Cast<Long>() {
			@Override
			public Long cast(Object obj) {
				return ConverterUtil.obj2Long(obj);
			}
		}, pageSql, parameter);

	}

	@Override
	public List<Date> querySimpleListPageDate(String sql, int pageNumber, int pageSize, Object... parameter) {
		PageSql pageSql = dialect.buildPageSql(sql, pageNumber, pageSize);
		return queryPageList(new Cast<Date>() {
			@Override
			public Date cast(Object obj) {
				return ConverterUtil.obj2Date(obj);
			}
		}, pageSql, parameter);

	}

	@Override
	public <T> List<T> queryPageList(Cast<T> cast, PageSql pageSql, Object... paramobj) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = dataBase.getConnection();
		List<T> list = null;
		List<Object> param = null;
		String sql = pageSql.getSql();
		boolean[] sqlRunErr = new boolean[] { false };
		try {
			pst = conn.prepareStatement(sql);
			param = dialect.fillPageStatement(pst, pageSql, paramobj);
			long elapsed = System.currentTimeMillis();
			rs = executeQuery(pst, sqlRunErr);
			sqlQueryReport(sql, elapsed, param);
			list = new ArrayList<T>();
			while (rs.next()) {
				list.add((T) rs.getObject(1));
			}
			return list;
		} catch (Exception e) {
			throw handlerExecption(sqlRunErr, sql, e, param);
		} finally {
			dataBase.close(rs, pst, conn);
		}
	}

	@Override
	public <T> List<T> queryList(Class<T> clazz) {
		String sql = SqlBuilder.buildQuerySQL(clazz);
		return queryList(clazz, sql);
	}

	@Override
	public <T> List<T> queryList(Class<T> clazz, String sql, Object... parameter) {
		List<T> dataList;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<Object> param = null;
		boolean[] sqlRunErr = new boolean[] { false };
		try {
			conn = dataBase.getConnection();
			pst = conn.prepareStatement(sql);
			param = dialect.fillStatement(pst, parameter);
			long elapsed = System.currentTimeMillis();
			rs = executeQuery(pst, sqlRunErr);
			sqlQueryReport(sql, elapsed, param);
			BeanResultSetHandler<T> handler = new BeanResultSetHandler<T>(dialect, clazz, rs);
			dataList = new ArrayList<T>();
			while (rs.next()) {
				T t = handler.getResult();
				dataList.add(t);
			}
		} catch (Exception e) {
			JdbcException je = handlerExecption(sqlRunErr, sql, e, param);
			throw je;
		} finally {
			dataBase.close(rs, pst, conn);
		}
		return dataList;
	}

	@Override
	public <T> List<T> queryList(Class<T> clazz, Map<String, Object> columns) {
		checkNull(columns);
		StringBuilder sql = new StringBuilder(300);
		sql.append(SqlBuilder.buildQuerySQL(clazz));
		sql.append(" where ");
		int i = 0;
		for (String cName : columns.keySet()) {
			if (i++ > 0) {
				sql.append(" and ");
			}
			sql.append(cName).append(" = ").append("?");
		}
		return queryList(clazz, sql.toString(), columns.values());
	}

	@Override
	public <T> List<T> queryListPage(Class<T> clazz, String sql, int pageNumber, int pageSize, Object... parameter) {
		PageSql pageSql = dialect.buildPageSql(sql, pageNumber, pageSize);
		return queryPageList(clazz, dataBase.getConnection(), pageSql, parameter);
	}

	private <T> List<T> queryPageList(Class<T> clazz, Connection conn, PageSql pageSql, Object... paramobj) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<T> dataList;
		List<Object> param = null;
		String sql = pageSql.getSql();
		boolean[] sqlRunErr = new boolean[] { false };
		try {
			pst = conn.prepareStatement(sql);
			param = dialect.fillPageStatement(pst, pageSql, paramobj);
			dataList = new ArrayList<T>();
			long elapsed = System.currentTimeMillis();
			rs = executeQuery(pst, sqlRunErr);
			sqlQueryReport(sql, elapsed, param);
			BeanResultSetHandler<T> handler = new BeanResultSetHandler<T>(dialect, clazz, rs);
			while (rs.next()) {
				T t = handler.getResult();
				dataList.add(t);
			}
			return dataList;
		} catch (Exception e) {
			throw handlerExecption(sqlRunErr, sql, e, param);
		} finally {
			dataBase.close(rs, pst, conn);
		}
	}

	@Override
	public <T> List<T> queryListPage(Class<T> clazz, Map<String, Object> columns, int pageNumber, int pageSize) {
		checkNull(columns);
		StringBuilder sql = new StringBuilder(300);
		sql.append(SqlBuilder.buildQuerySQL(clazz));
		sql.append(" where ");
		int i = 0;
		for (String cName : columns.keySet()) {
			if (i++ > 0) {
				sql.append(" and ");
			}
			sql.append(cName).append(" = ").append("?");
		}
		return queryListPage(clazz, sql.toString(), pageNumber, pageSize, columns.values());
	}

	@Override
	public List<Record> queryListRecord(String sql, Object... parameter) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = dataBase.getConnection();
		List<Record> list;
		List<Object> param = null;
		boolean[] sqlRunErr = new boolean[] { false };
		try {
			pst = conn.prepareStatement(sql);
			param = dialect.fillStatement(pst, parameter);
			long elapsed = System.currentTimeMillis();
			rs = executeQuery(pst, sqlRunErr);
			sqlQueryReport(sql, elapsed, param);
			list = new ArrayList<Record>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] labelNames = new String[count + 1];
			int[] types = new int[count + 1];
			for (int i = 1; i < labelNames.length; i++) {
				String columnLabel = StringUtil.empty(rsmd.getColumnLabel(i), rsmd.getColumnName(i)).trim();
				labelNames[i] = columnLabel.toLowerCase();
				types[i] = rsmd.getColumnType(i);
			}
			while (rs.next()) {
				Record record = new Record();
				for (int i = 1; i <= count; i++) {
					Object value = dialect.getData(rs, types, i);
					record.set(labelNames[i], value);
				}
				list.add(record);
			}
			return list;
		} catch (Exception e) {
			JdbcException je = handlerExecption(sqlRunErr, sql, e, param);
			throw je;
		} finally {
			dataBase.close(rs, pst, conn);
		}
	}

	@Override
	public List<Record> queryListRecord(String tableName, Map<String, Object> columns) {
		String columnInfo = DBInfoContainer.getColumns(tableName);
		if (StringUtil.empty(columnInfo)) {
			Connection conn = dataBase.getConnection();
			ResultSet rs = null;
			try {
				DatabaseMetaData dbmt = conn.getMetaData();
				rs = dbmt.getColumns(null, null, tableName.toUpperCase(), null);
				int i = 0;
				StringBuilder sb = new StringBuilder();
				while (rs.next()) {
					if (i++ > 0) {
						sb.append(",");
					}
					sb.append(rs.getObject(4).toString().toLowerCase());
				}
				columnInfo = sb.toString();
			} catch (Exception e) {
				columnInfo = "*";
			} finally {
				dataBase.close(rs, null, conn);
			}
			DBInfoContainer.setColumns(tableName, columnInfo);
		}
		StringBuilder sql = new StringBuilder(200);
		sql.append("select ").append(columnInfo).append(" from ").append(tableName);
		sql.append(" where ");
		int i = 0;
		for (String cName : columns.keySet()) {
			if (i++ > 0) {
				sql.append(" and ");
			}
			sql.append(cName).append(" = ").append("?");
		}
		return queryListRecord(sql.toString(), columns.values());
	}

	@Override
	public <T> List<Record> queryListRecord(Class<T> clazz, Map<String, Object> columns) {
		return queryListRecord(DBInfoContainer.getTableName(clazz), columns);
	}

	@Override
	public <T> List<Record> queryListRecord(Class<T> clazz) {
		return queryListRecord(SqlBuilder.buildQuerySQL(clazz), NULL_PARAM);
	}

	@Override
	public List<Record> queryListRecordByTableName(String tableName) {
		String columnInfo = DBInfoContainer.getColumns(tableName);
		if (StringUtil.empty(columnInfo)) {
			Connection conn = dataBase.getConnection();
			ResultSet rs = null;
			try {
				DatabaseMetaData dbmt = conn.getMetaData();
				rs = dbmt.getColumns(null, null, tableName.toUpperCase(), null);
				int i = 0;
				StringBuilder sb = new StringBuilder();
				while (rs.next()) {
					if (i++ > 0) {
						sb.append(",");
					}
					sb.append(rs.getObject(4).toString().toLowerCase());
				}
				columnInfo = sb.toString();
			} catch (Exception e) {
				columnInfo = "*";
			} finally {
				dataBase.close(rs, null, conn);
			}
			DBInfoContainer.setColumns(tableName, columnInfo);
		}
		StringBuilder sql = new StringBuilder(200);
		sql.append("select ").append(columnInfo).append(" from ").append(tableName);
		return queryListRecord(sql.toString(), NULL_PARAM);
	}

	@Override
	public List<Record> queryListRecordPage(String sql, int pageNumber, int pageSize, Object... parameter) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = dataBase.getConnection();
		List<Record> list;
		PageSql pageSql = dialect.buildPageSql(sql, pageNumber, pageSize);
		String pSql = pageSql.getSql();
		List<Object> param = null;
		boolean[] sqlRunErr = new boolean[] { false };
		try {
			pst = conn.prepareStatement(pSql);
			param = dialect.fillPageStatement(pst, pageSql, parameter);
			long elapsed = System.currentTimeMillis();
			rs = executeQuery(pst, sqlRunErr);
			sqlQueryReport(pSql, elapsed, param);
			list = new ArrayList<Record>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] labelNames = new String[count + 1];
			int[] types = new int[count + 1];
			for (int i = 1; i < labelNames.length; i++) {
				String columnLabel = StringUtil.empty(rsmd.getColumnLabel(i), rsmd.getColumnName(i)).trim();
				labelNames[i] = columnLabel.toLowerCase();
				types[i] = rsmd.getColumnType(i);
			}
			while (rs.next()) {
				Record record = new Record();
				for (int i = 1; i <= count; i++) {
					Object value = dialect.getData(rs, types, i);
					record.set(labelNames[i], value);
				}
				list.add(record);
			}
			return list;
		} catch (Exception e) {
			JdbcException je = handlerExecption(sqlRunErr, pSql, e, param);
			throw je;
		} finally {
			dataBase.close(rs, pst, conn);
		}
	}

	@Override
	public List<Record> queryListRecordPage(String tableName, Map<String, Object> columns, int pageNumber, int pageSize) {
		String columnInfo = DBInfoContainer.getColumns(tableName);
		if (StringUtil.empty(columnInfo)) {
			Connection conn = dataBase.getConnection();
			ResultSet rs = null;
			try {
				DatabaseMetaData dbmt = conn.getMetaData();
				rs = dbmt.getColumns(null, null, tableName.toUpperCase(), null);
				int i = 0;
				StringBuilder sb = new StringBuilder();
				while (rs.next()) {
					if (i++ > 0) {
						sb.append(",");
					}
					sb.append(rs.getObject(4).toString().toLowerCase());
				}
				columnInfo = sb.toString();
			} catch (Exception e) {
				columnInfo = "*";
			} finally {
				dataBase.close(rs, null, conn);
			}
			DBInfoContainer.setColumns(tableName, columnInfo);
		}
		StringBuilder sql = new StringBuilder(200);
		sql.append("select ").append(columnInfo).append(" from ").append(tableName);
		sql.append(" where ");
		int i = 0;
		for (String cName : columns.keySet()) {
			if (i++ > 0) {
				sql.append(" and ");
			}
			sql.append(cName).append(" = ").append("?");
		}
		return queryListRecordPage(sql.toString(), pageNumber, pageSize, columns.values());
	}

	@Override
	public <T> List<Record> queryListRecordPage(Class<T> clazz, Map<String, Object> columns, int pageNumber, int pageSize) {
		return queryListRecordPage(DBInfoContainer.getTableName(clazz), columns, pageNumber, pageSize);
	}

	@Override
	public <T> PageRecord<T> queryPage(Class<T> clazz, int pageNumber, int pageSize) {
		return queryPage(clazz, SqlBuilder.buildQuerySQL(clazz), pageNumber, pageSize);
	}

	@Override
	public <T> PageRecord<T> queryPage(Class<T> clazz, Map<String, Object> columns, int pageNumber, int pageSize) {
		checkNull(columns);
		StringBuilder sql = new StringBuilder(300);
		sql.append(SqlBuilder.buildQuerySQL(clazz));
		sql.append(" where ");
		int i = 0;
		for (String cName : columns.keySet()) {
			if (i++ > 0) {
				sql.append(" and ");
			}
			sql.append(cName).append(" = ").append("?");
		}
		return queryPage(clazz, sql.toString(), pageNumber, pageSize, columns.values());
	}

	@Override
	public <T> PageRecord<T> queryPage(Class<T> clazz, String sql, int pageNumber, int pageSize, Object... parameter) {
		PageRecord<T> pageRecord;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = dataBase.getConnection();
		int totalCount = 0;
		String countSql = "";
		List<Object> param = null;
		boolean[] sqlRunErr = new boolean[] { false };
		try {
			countSql = dialect.buildCountSql(sql);
			pst = conn.prepareStatement(countSql);
			param = dialect.fillStatement(pst, parameter);
			long elapsed = System.currentTimeMillis();
			rs = executeQuery(pst, sqlRunErr);
			sqlQueryReport(countSql, elapsed, param);
			if (rs.next()) {
				totalCount = rs.getInt(1);
			}
			pageRecord = new PageRecord<T>();
			if (totalCount > 0) {
				pageRecord.setPageSize(pageSize);
				pageRecord.setPage(pageNumber);
				pageRecord.setTotalCount(totalCount);
				PageSql pageSql = dialect.buildPageSql(sql, pageRecord.getPage(), pageSize);
				pageRecord.setDataList(queryPageList(clazz, conn, pageSql, parameter));
			}
			return pageRecord;
		} catch (Exception e) {
			throw handlerExecption(sqlRunErr, countSql, e, param);
		} finally {
			dataBase.close(rs, pst, conn);
		}
	}

	@Override
	public <K, V> Map<K, V> queryMap(String sql, Object... parameter) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<Object> param = null;
		Map<K, V> map = new HashMap<K, V>();
		boolean[] sqlRunErr = new boolean[] { false };
		try {
			conn = dataBase.getConnection();
			pst = conn.prepareStatement(sql);
			param = dialect.fillStatement(pst, parameter);
			long elapsed = System.currentTimeMillis();
			rs = executeQuery(pst, sqlRunErr);
			sqlQueryReport(sql, elapsed, param);
			while (rs.next()) {
				map.put((K) rs.getObject(1), (V) rs.getObject(2));
			}
		} catch (Exception e) {
			JdbcException je = handlerExecption(sqlRunErr, sql, e, param);
			throw je;
		} finally {
			dataBase.close(rs, pst, conn);
		}
		return map;
	}

	@Override
	public <K, V> Map<K, V> queryMapPage(String sql, int pageNumber, int pageSize, Object... parameter) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<Object> param = null;
		Map<K, V> map = new HashMap<K, V>();
		boolean[] sqlRunErr = new boolean[] { false };
		try {
			conn = dataBase.getConnection();
			PageSql ps = dialect.buildPageSql(sql, pageNumber, pageSize);
			pst = conn.prepareStatement(ps.getSql());
			param = dialect.fillPageStatement(pst, ps, parameter);
			long elapsed = System.currentTimeMillis();
			rs = executeQuery(pst, sqlRunErr);
			sqlQueryReport(sql, elapsed, param);
			while (rs.next()) {
				map.put((K) rs.getObject(1), (V) rs.getObject(2));
			}
		} catch (Exception e) {
			JdbcException je = handlerExecption(sqlRunErr, sql, e, param);
			throw je;
		} finally {
			dataBase.close(rs, pst, conn);
		}
		return map;
	}

	private void checkNull(Map<String, Object> columns) {
		if (columns == null || columns.isEmpty()) {
			throw new IllegalArgumentException("The param columns can not be null......");
		}
	}

	private JdbcException handlerExecption(boolean[] sqlRunErr, String sql, Exception e, List<Object> param) {
		JdbcException je = null;
		if (sqlRunErr[0]) {
			SQLReport.executeFail(sql, e, param);
			if (param == null || param.size() == 0) {
				je = new JdbcException("Excute SQL fail," + e.getMessage() + "\nSQL: " + sql + "");
			} else {
				je = new JdbcException("Excute SQL fail," + e.getMessage() + "\nSQL: " + sql + "\nParameter:" + SQLReport.handleParameter(param));
			}
			je.setStackTrace(e.getStackTrace());
		} else {
			je = new JdbcException(e);
		}
		return je;
	}

	private JdbcException callExecption(boolean[] sqlRunErr, String name, Exception e, List<Object> param) {
		JdbcException je = null;
		if (sqlRunErr[0]) {
			SQLReport.callProcedureFail(name, e, param);
			if (param == null || param.size() == 0) {
				je = new JdbcException("Call procedure fail," + e.getMessage() + "name: " + name + "");
			} else {
				je = new JdbcException("Call procedure fail," + e.getMessage() + "name: " + name + "\nParameter:" + SQLReport.handleParameter(param));
			}
			je.setStackTrace(e.getStackTrace());
		} else {
			je = new JdbcException(e);
		}
		return je;
	}

	private void sqlQueryReport(String sql, long elapsed, List<Object> param) {
		if (showSql) {
			SQLReport.report(SQLReport.SQL_QUERY_ELAPSED, new Object[] { sql, new Long(System.currentTimeMillis() - elapsed) });
			if (param != null && param.size() > 0) {
				SQLReport.parameter(SQLReport.SQL_PARAMETER, param);
			}
		}
	}

	private void sqlCommandReport(String sql, long elapsed, List<Object> param) {
		if (showSql) {
			SQLReport.report(SQLReport.SQL_COMMAND_ELAPSED, new Object[] { sql, new Long(System.currentTimeMillis() - elapsed) });
			if (param != null && param.size() > 0) {
				SQLReport.parameter(SQLReport.SQL_PARAMETER, param);
			}
		}
	}

	private void sqlProcedureReport(String sql, long elapsed, List<Object> param) {
		if (showSql) {
			SQLReport.report(SQLReport.SQL_PROCEDURE_ELAPSED, new Object[] { sql, new Long(System.currentTimeMillis() - elapsed) });
			if (param != null && param.size() > 0) {
				SQLReport.parameter(SQLReport.SQL_PARAMETER, param);
			}
		}
	}

}
