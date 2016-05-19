package com.puff.jdbc.executor;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.puff.jdbc.core.PageRecord;
import com.puff.jdbc.core.Procedure;
import com.puff.jdbc.core.Record;
import com.puff.jdbc.dialect.PageSql;

public interface SqlExecutor {

	public boolean tableExist(String tableName);

	public int insert(String sql, Object... parameter);

	public int insert(String tableName, Map<String, Object> columns);

	public <T> int insert(Class<T> clazz, Map<String, Object> columns);

	public int save(Object obj);

	public int save(Object obj, Map<String, Object> columns);

	public int delete(String sql, Object... parameter);

	public <T> void deleteInById(Class<T> clazz, List<?> ids);

	public <T> void deleteInByColumn(Class<T> clazz, String column, List<?> ids);

	public void deleteInByTable(String tableName, String column, List<?> ids);

	public int delete(String tableName, Map<String, Object> columns);

	public <T> int delete(Class<T> clazz, Map<String, Object> columns);

	public int updateBySql(String sql, Object... parameter);

	public int update(String tableName, Map<String, Object> columns);

	public <T> int update(Class<T> clazz, Map<String, Object> columns);

	public int update(Object obj);

	public int updateWithoutNull(Object obj);

	public boolean execute(String sql, Object... parameter);

	public void transaction(Atom atom);

	public Map<String, Object> call(Procedure procedure);

	public int[] batchHandle(String sql, List<Object[]> params, int batchSize);

	public <T> int[] batchInsert(List<T> list, int batchSize);

	public long count(String sql, Object... parameter);

	public long count(String tableName);

	public long count(String tableName, Map<String, Object> columns);

	public <T> long count(Class<T> clazz);

	public <T> long count(Class<T> clazz, Map<String, Object> columns);

	public <T> T query(Class<T> clazz, String sql, Object... parameter);

	public <T> T query(Class<T> clazz, Map<String, Object> columns);

	public <T> T query(String sql, Object... parameter);

	public String queryString(String sql, Object... parameter);

	public int queryInt(String sql, Object... parameter);

	public long queryLong(String sql, Object... parameter);

	public Date queryDate(String sql, Object... parameter);

	public Record queryRecord(String sql, Object... parameter);

	public Record queryRecord(String tableName, Map<String, Object> columns);

	public <T> Record queryRecord(Class<T> clazz, Map<String, Object> columns);

	public <T> List<T> querySimpleList(String sql, Object... parameter);

	public <T> List<T> querySimpleList(Cast<T> cast, String sql, Object... parameter);

	public List<String> querySimpleListString(String sql, Object... parameter);

	public List<Integer> querySimpleListInt(String sql, Object... parameter);

	public List<Long> querySimpleListLong(String sql, Object... parameter);

	public List<Date> querySimpleListDate(String sql, Object... parameter);

	public <T> List<T> querySimpleListPage(String sql, int pageNumber, int pageSize, Object... parameter);

	public <T> List<T> queryPageList(Cast<T> cast, PageSql pageSql, Object... parameter);

	public List<String> querySimpleListPageString(String sql, int pageNumber, int pageSize, Object... parameter);

	public List<Integer> querySimpleListPageInt(String sql, int pageNumber, int pageSize, Object... parameter);

	public List<Long> querySimpleListPageLong(String sql, int pageNumber, int pageSize, Object... parameter);

	public List<Date> querySimpleListPageDate(String sql, int pageNumber, int pageSize, Object... parameter);

	public <T> List<T> queryList(Class<T> clazz);

	public <T> List<T> queryList(Class<T> clazz, String sql, Object... parameter);

	public <T> List<T> queryList(Class<T> clazz, Map<String, Object> columns);

	public <T> List<T> queryListPage(Class<T> clazz, String sql, int pageNumber, int pageSize, Object... parameter);

	public <T> List<T> queryListPage(Class<T> clazz, Map<String, Object> columns, int pageNumber, int pageSize);

	public List<Record> queryListRecord(String sql, Object... parameter);

	public List<Record> queryListRecord(String tableName, Map<String, Object> columns);

	public <T> List<Record> queryListRecord(Class<T> clazz, Map<String, Object> columns);

	public <T> List<Record> queryListRecord(Class<T> clazz);

	public List<Record> queryListRecordByTableName(String tableName);

	public List<Record> queryListRecordPage(String sql, int pageNumber, int pageSize, Object... parameter);

	public List<Record> queryListRecordPage(String tableName, Map<String, Object> columns, int pageNumber, int pageSize);

	public <T> List<Record> queryListRecordPage(Class<T> clazz, Map<String, Object> columns, int pageNumber, int pageSize);

	public <T> PageRecord<T> queryPage(Class<T> clazz, int pageNumber, int pageSize);

	public <T> PageRecord<T> queryPage(Class<T> clazz, Map<String, Object> columns, int pageNumber, int pageSize);

	public <T> PageRecord<T> queryPage(Class<T> clazz, String sql, int pageNumber, int pageSize, Object... parameter);

	public <K, V> Map<K, V> queryMap(String sql, Object... parameter);

	public <K, V> Map<K, V> queryMapPage(String sql, int pageNumber, int pageSize, Object... parameter);

	public interface Cast<T> {
		public T cast(Object obj);
	}

}
