package com.puff.framework.container;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.puff.exception.ExceptionUtil;
import com.puff.framework.annotation.Column;
import com.puff.framework.annotation.PKType;
import com.puff.framework.annotation.PrimaryKey;
import com.puff.framework.annotation.Table;
import com.puff.framework.utils.ClassUtil;
import com.puff.framework.utils.StringUtil;
import com.puff.include.asm.reflect.MethodAccess;
import com.puff.jdbc.core.ColumnProperty;
import com.puff.jdbc.core.ClassMethod;
import com.puff.jdbc.core.FieldProcessor;

/**
 * DBInfoContainer
 */
public final class DBInfoContainer {

	private final static Map<String, List<ColumnProperty>> map = new HashMap<String, List<ColumnProperty>>();
	private final static Map<String, String> tableNameMap = new HashMap<String, String>();
	private final static Map<String, String> pkMap = new HashMap<String, String>();
	private final static Map<String, String> colunmInfoMap = new HashMap<String, String>();
	private final static Map<String, FieldProcessor> fieldProcessorMap = new HashMap<String, FieldProcessor>();
	private final static Map<String, List<String>> colunmMap = new HashMap<String, List<String>>();

	public static void addColumnProperty(Class<?> clazz, List<ColumnProperty> list) {
		map.put(clazz.getName(), list);
	}

	public static List<ColumnProperty> getColumnProperty(Class<?> clazz) {
		String className = clazz.getName();
		List<ColumnProperty> list = map.get(className);
		if (list == null || list.size() == 0) {
			List<Field> fields = ClassUtil.getField(clazz, new ArrayList<Field>());
			if (fields.size() == 0) {
				throw new IllegalArgumentException("The class " + clazz + " has no fields");
			}
			MethodAccess access = ClassMethod.regMethodAccess(clazz);
			list = new ArrayList<ColumnProperty>(fields.size());
			for (Field field : fields) {
				Column column = field.getAnnotation(Column.class);
				if (column == null) {
					continue;
				}
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				String fieldName = field.getName();
				if ("serialVersionUID".equals(fieldName)) {
					continue;
				}

				String columnName = StringUtil.empty(column.value(), fieldName).trim().toLowerCase();
				ColumnProperty cp = new ColumnProperty();
				cp.setFieldName(fieldName);
				cp.setColumnName(columnName);
				field.setAccessible(true);
				Class<?> type = field.getType();
				cp.setJavaType(type);
				fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				String setMethodName = "set" + fieldName;
				try {
					Class<? extends FieldProcessor> klass = column.processor();
					if (klass != null && klass != FieldProcessor.class) {
						cp.setFieldProcessor(getProcessor(klass));
					}
					int setMethodIdx = access.getIndex(setMethodName, type);
					String getMethodName = type.equals(boolean.class) ? "is" + fieldName : "get" + fieldName;
					int getMethodIdx = access.getIndex(getMethodName);
					cp.setSetMethodIdx(setMethodIdx);
					cp.setGetMethodIdx(getMethodIdx);
					PrimaryKey.AUTO primaryKey_auto = field.getAnnotation(PrimaryKey.AUTO.class);
					if (primaryKey_auto != null) {
						cp.setPkType(PKType.AUTO);
					} else {
						PrimaryKey.AUTO_SEQUENCE primaryKey_auto_sequence = field.getAnnotation(PrimaryKey.AUTO_SEQUENCE.class);
						if (primaryKey_auto_sequence != null) {
							cp.setPkType(PKType.AUTO_SEQUENCE);
							cp.setSeq_name(primaryKey_auto_sequence.name());
						} else {
							PrimaryKey.CUSTOM primaryKey_custom = field.getAnnotation(PrimaryKey.CUSTOM.class);
							if (primaryKey_custom != null) {
								cp.setPkType(PKType.CUSTOM);
							} else {
								PrimaryKey.IDWORKER primaryKey_idworker = field.getAnnotation(PrimaryKey.IDWORKER.class);
								if (primaryKey_idworker != null) {
									cp.setPkType(PKType.IDWORKER);
								} else {
									PrimaryKey.SEQUENCE primaryKey_sequence = field.getAnnotation(PrimaryKey.SEQUENCE.class);
									if (primaryKey_sequence != null) {
										cp.setPkType(PKType.SEQUENCE);
										cp.setSeq_name(primaryKey_sequence.name());
									} else {
										PrimaryKey.UUID primaryKey_uuid = field.getAnnotation(PrimaryKey.UUID.class);
										if (primaryKey_uuid != null) {
											cp.setPkType(PKType.UUID);
										}
									}
								}
							}
						}
					}
					cp.setAlias(column.alias());
					list.add(cp);
				} catch (Exception e) {
					ExceptionUtil.throwRuntime(e);
				}
			}
			map.put(className, list);
		}
		return list;
	}

	private static FieldProcessor getProcessor(Class<? extends FieldProcessor> klass) throws Exception {
		FieldProcessor fieldProcessor = fieldProcessorMap.get(klass.getName());
		if (fieldProcessor == null) {
			fieldProcessor = klass.newInstance();
			fieldProcessorMap.put(klass.getName(), fieldProcessor);
		}
		return fieldProcessor;
	}

	public static List<String> getColumns(Class<?> clazz) {
		String className = clazz.getName();
		List<String> list = colunmMap.get(className);
		if (list == null || list.size() == 0) {
			List<ColumnProperty> columnProperties = getColumnProperty(clazz);
			list = new ArrayList<String>();
			for (ColumnProperty pm : columnProperties) {
				try {
					if (pm.isAlias()) {
						continue;
					}
					String function = pm.getFunction();
					if (StringUtil.empty(function)) {
						list.add(pm.getColumnName());
					} else {
						list.add(function + " as " + pm.getColumnName());
					}
				} catch (Exception e) {
					ExceptionUtil.throwRuntime(e);
				}
			}
			colunmMap.put(className, list);
		}
		return list;
	}

	public static String getPkName(Class<?> clazz) {
		String className = clazz.getName();
		String pkName = pkMap.get(className);
		if (pkName == null) {
			List<ColumnProperty> list = getColumnProperty(clazz);
			for (ColumnProperty pm : list) {
				if (pm.getPkType() != null) {
					pkName = pm.getColumnName();
					pkMap.put(className, pkName);
					break;
				}
			}
		}
		return pkName;
	}

	public static String getTableName(Object obj) {
		Class<?> clazz = (obj instanceof Class<?>) ? (Class<?>) obj : obj.getClass();
		String className = clazz.getName();
		String tableName = tableNameMap.get(className);
		if (tableName == null) {
			Table table = clazz.getAnnotation(Table.class);
			if (table == null || StringUtil.empty(table.value())) {
				tableName = clazz.getSimpleName().toLowerCase();
			} else {
				tableName = table.value().toLowerCase();
			}
			tableNameMap.put(className, tableName);
		}
		return tableName;
	}

	public static String setColumns(String tableName, String columnInfo) {
		return colunmInfoMap.put(tableName, columnInfo);
	}

	public static String getColumns(String tableName) {
		return colunmInfoMap.get(tableName);
	}
}