package com.puff.jdbc.core;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Record implements Serializable, Iterable<Entry<String, Object>> {

	private static final long serialVersionUID = 7308189090731685018L;

	private Map<String, Object> record = new HashMap<String, Object>();

	public void setRecord(Record record) {
		this.record.putAll(record.getRecord());
	}

	public void setRecord(Map<String, Object> record) {
		this.record = record;
	}

	public Map<String, Object> getRecord() {
		return record;
	}

	public void set(String key, Object value) {
		record.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String columnName) {
		return (T) record.get(columnName);
	}

	public Object getObj(String columnName) {
		return record.get(columnName);
	}

	public String getString(String columnName) {
		return (String) record.get(columnName);
	}

	public Integer getInt(String columnName) {
		return (Integer) record.get(columnName);
	}

	public Long getLong(String columnName) {
		return (Long) record.get(columnName);
	}

	public java.sql.Date getSqlDate(String columnName) {
		return (java.sql.Date) record.get(columnName);
	}

	public java.util.Date getDate(String columnName) {
		java.sql.Date sqlDate = getSqlDate(columnName);
		return new Date(sqlDate.getTime());
	}

	public Time getTime(String columnName) {
		return (Time) record.get(columnName);
	}

	public Timestamp getTimestamp(String columnName) {
		return (Timestamp) record.get(columnName);
	}

	public Double getDouble(String columnName) {
		return (Double) record.get(columnName);
	}

	public Float getFloat(String columnName) {
		return (Float) record.get(columnName);
	}

	public Boolean getBoolean(String columnName) {
		return (Boolean) record.get(columnName);
	}

	public BigDecimal getBigDecimal(String columnName) {
		return (BigDecimal) record.get(columnName);
	}

	public byte[] getBytes(String columnName) {
		return (byte[]) record.get(columnName);
	}

	public Number getNumber(String columnName) {
		return (Number) record.get(columnName);
	}

	public Set<Entry<String, Object>> getEntrySet() {
		return record.entrySet();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((record == null) ? 0 : record.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Record other = (Record) obj;
		if (record == null) {
			if (other.record != null) {
				return false;
			}
		} else if (!record.equals(other.record))
			return false;
		return true;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(" {");
		boolean first = true;
		for (Entry<String, Object> e : record.entrySet()) {
			if (first)
				first = false;
			else
				sb.append(", ");
			Object value = e.getValue();
			if (value != null)
				value = value.toString();
			sb.append(e.getKey()).append(":").append(value);
		}
		sb.append("}");
		return sb.toString();
	}

	public String[] getColumnNames() {
		Set<String> columnNames = record.keySet();
		return columnNames.toArray(new String[columnNames.size()]);
	}

	public Object[] getValues() {
		Collection<Object> attrValueCollection = record.values();
		return attrValueCollection.toArray(new Object[attrValueCollection.size()]);
	}

	public boolean empty(String key) {
		Object object = record.get(key);
		return object == null || "".equals(object.toString());
	}

	public boolean notEmpty(String key) {
		return !empty(key);
	}

	public boolean isEmpty() {
		return record.isEmpty();
	}

	@Override
	public Iterator<Entry<String, Object>> iterator() {
		final Iterator<Entry<String, Object>> iterator = getEntrySet().iterator();
		return new Iterator<Map.Entry<String, Object>>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public Entry<String, Object> next() {
				return iterator.next();
			}

			@Override
			public void remove() {
				iterator.remove();
			}
		};
	}

}
