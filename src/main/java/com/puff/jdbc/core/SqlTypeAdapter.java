package com.puff.jdbc.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("unchecked")
public class SqlTypeAdapter {

	/**
	 * Convert the SQL type value - 'sqlTypeValue' to java type - 'javaType'
	 *
	 * @param javaType
	 *            the target converted type
	 * @param sqlTypeValue
	 *            the SQL value unknown type
	 * @param <T>
	 *            template type
	 * @return the converted SQL value
	 */

	public static <T> T toJavaType(Class<T> javaType, Object sqlTypeValue) {
		if (sqlTypeValue == null) {
			throw new RuntimeException(String.format("Can't convert null-->>%s!", javaType.getSimpleName()));
		}
		if (sqlTypeValue.getClass().equals(javaType)) {
			return (T) sqlTypeValue;
		}
		if (String.class.equals(javaType)) {
			return (T) toJavaString(sqlTypeValue);
		}
		if (byte.class.equals(javaType) || Byte.class.equals(javaType))
			return (T) toJavaByte(sqlTypeValue);
		if (char.class.equals(javaType) || Character.class.equals(javaType))
			return (T) toJavaChar(sqlTypeValue);

		if (short.class.equals(javaType) || Short.class.equals(javaType))
			return (T) toJavaShort(sqlTypeValue);

		if (int.class.equals(javaType) || Integer.class.equals(javaType))
			return (T) toJavaInteger(sqlTypeValue);

		if (long.class.equals(javaType) || Long.class.equals(javaType))
			return (T) toJavaLong(sqlTypeValue);

		if (float.class.equals(javaType) || Float.class.equals(javaType))
			return (T) toJavaFloat(sqlTypeValue);

		if (double.class.equals(javaType) || Double.class.equals(javaType))
			return (T) toJavaDouble(sqlTypeValue);

		if (boolean.class.equals(javaType) || Boolean.class.equals(javaType))
			return (T) toJavaBoolean(sqlTypeValue);

		if (BigInteger.class.equals(javaType))
			return (T) toJavaBigInteger(sqlTypeValue);

		if (BigDecimal.class.equals(javaType))
			return (T) toJavaBigDecimal(sqlTypeValue);

		if (java.util.Date.class.equals(javaType))
			return (T) toJavaDate(sqlTypeValue);

		if (java.util.Calendar.class.equals(javaType))
			return (T) toJavaCalendar(sqlTypeValue);

		throw new RuntimeException(String.format("Can't convert %s-->>%s!", sqlTypeValue.getClass().getSimpleName(), javaType.getSimpleName()));
	}

	public static String toJavaString(Object sqlTypeValue) {
		if (sqlTypeValue == null)
			return null;
		return sqlTypeValue.toString();
	}

	public static Byte toJavaByte(Object sqlTypeValue) {

		if (sqlTypeValue == null)
			return 0;

		Class<?> sqlType = sqlTypeValue.getClass();

		if (String.class.equals(sqlType))
			return Byte.parseByte((String) sqlTypeValue);

		if (int.class.equals(sqlType) || Integer.class.equals(sqlType))
			return ((Integer) sqlTypeValue).byteValue();

		if (long.class.equals(sqlType) || Long.class.equals(sqlType))
			return ((Long) sqlTypeValue).byteValue();

		if (float.class.equals(sqlType) || Float.class.equals(sqlType))
			return ((Float) sqlTypeValue).byteValue();

		if (double.class.equals(sqlType) || Double.class.equals(sqlType))
			return ((Double) sqlTypeValue).byteValue();

		if (boolean.class.equals(sqlType) || Boolean.class.equals(sqlType))
			return (byte) (((Boolean) sqlTypeValue) ? 1 : 0);

		if (BigDecimal.class.equals(sqlType))
			return ((BigDecimal) sqlTypeValue).byteValue();

		throw new RuntimeException(String.format("Can't convert %s-->>Byte!", sqlType.getSimpleName()));
	}

	public static Character toJavaChar(Object sqlTypeValue) {
		byte byteValue = (Byte) toJavaByte(sqlTypeValue);
		return (char) byteValue;
	}

	public static Short toJavaShort(Object sqlTypeValue) {

		if (sqlTypeValue == null)
			return 0;

		Class<?> sqlType = sqlTypeValue.getClass();

		if (String.class.equals(sqlType))
			return Short.parseShort((String) sqlTypeValue);

		if (int.class.equals(sqlType) || Integer.class.equals(sqlType))
			return ((Integer) sqlTypeValue).shortValue();

		if (long.class.equals(sqlType) || Long.class.equals(sqlType))
			return ((Long) sqlTypeValue).shortValue();

		if (float.class.equals(sqlType) || Float.class.equals(sqlType))
			return ((Float) sqlTypeValue).shortValue();

		if (double.class.equals(sqlType) || Double.class.equals(sqlType))
			return ((Double) sqlTypeValue).shortValue();

		if (boolean.class.equals(sqlType) || Boolean.class.equals(sqlType))
			return (short) (((Boolean) sqlTypeValue) ? 1 : 0);

		if (BigDecimal.class.equals(sqlType))
			return ((BigDecimal) sqlTypeValue).shortValue();

		throw new RuntimeException(String.format("Can't convert %s-->>Short!", sqlType.getSimpleName()));
	}

	public static Integer toJavaInteger(Object sqlTypeValue) {

		if (sqlTypeValue == null)
			return 0;

		Class<?> sqlType = sqlTypeValue.getClass();

		if (String.class.equals(sqlType))
			return Integer.parseInt((String) sqlTypeValue);

		if (int.class.equals(sqlType) || Integer.class.equals(sqlType))
			return (Integer) sqlTypeValue;

		if (long.class.equals(sqlType) || Long.class.equals(sqlType))
			return ((Long) sqlTypeValue).intValue();

		if (float.class.equals(sqlType) || Float.class.equals(sqlType))
			return ((Float) sqlTypeValue).intValue();

		if (double.class.equals(sqlType) || Double.class.equals(sqlType))
			return ((Double) sqlTypeValue).intValue();

		if (boolean.class.equals(sqlType) || Boolean.class.equals(sqlType))
			return ((Boolean) sqlTypeValue) ? 1 : 0;

		if (BigDecimal.class.equals(sqlType))
			return ((BigDecimal) sqlTypeValue).intValue();

		throw new RuntimeException(String.format("Can't convert %s-->>Integer!", sqlType.getSimpleName()));
	}

	public static Long toJavaLong(Object sqlTypeValue) {

		if (sqlTypeValue == null)
			return 0L;

		Class<?> sqlType = sqlTypeValue.getClass();

		if (String.class.equals(sqlType))
			return Long.parseLong((String) sqlTypeValue);

		if (int.class.equals(sqlType) || Integer.class.equals(sqlType))
			return (long) (Integer) sqlTypeValue;

		if (long.class.equals(sqlType) || Long.class.equals(sqlType))
			return (Long) sqlTypeValue;

		if (float.class.equals(sqlType) || Float.class.equals(sqlType))
			return ((Float) sqlTypeValue).longValue();

		if (double.class.equals(sqlType) || Double.class.equals(sqlType))
			return ((Double) sqlTypeValue).longValue();

		if (boolean.class.equals(sqlType) || Boolean.class.equals(sqlType))
			return (long) (((Boolean) sqlTypeValue) ? 1 : 0);

		if (BigDecimal.class.equals(sqlType))
			return ((BigDecimal) sqlTypeValue).longValue();

		throw new RuntimeException(String.format("Can't convert %s-->>Long!", sqlType.getSimpleName()));
	}

	public static Float toJavaFloat(Object sqlTypeValue) {

		if (sqlTypeValue == null)
			return 0F;

		Class<?> sqlType = sqlTypeValue.getClass();

		if (String.class.equals(sqlType))
			return Float.parseFloat((String) sqlTypeValue);

		if (int.class.equals(sqlType) || Integer.class.equals(sqlType))
			return (float) (Integer) sqlTypeValue;

		if (long.class.equals(sqlType) || Long.class.equals(sqlType))
			return (float) (Long) sqlTypeValue;

		if (float.class.equals(sqlType) || Float.class.equals(sqlType))
			return (Float) sqlTypeValue;

		if (double.class.equals(sqlType) || Double.class.equals(sqlType))
			return ((Double) sqlTypeValue).floatValue();

		if (BigDecimal.class.equals(sqlType))
			return ((BigDecimal) sqlTypeValue).floatValue();

		throw new RuntimeException(String.format("Can't convert %s-->>Float!", sqlType.getSimpleName()));
	}

	public static Double toJavaDouble(Object sqlTypeValue) {

		if (sqlTypeValue == null)
			return 0D;

		Class<?> sqlType = sqlTypeValue.getClass();

		if (String.class.equals(sqlType))
			return Double.parseDouble((String) sqlTypeValue);

		if (int.class.equals(sqlType) || Integer.class.equals(sqlType))
			return (double) (Integer) sqlTypeValue;

		if (long.class.equals(sqlType) || Long.class.equals(sqlType))
			return (double) (Long) sqlTypeValue;

		if (float.class.equals(sqlType) || Float.class.equals(sqlType))
			return (double) (Float) sqlTypeValue;

		if (double.class.equals(sqlType) || Double.class.equals(sqlType))
			return (Double) sqlTypeValue;

		if (BigDecimal.class.equals(sqlType))
			return ((BigDecimal) sqlTypeValue).doubleValue();

		throw new RuntimeException(String.format("Can't convert %s-->>Double!", sqlType.getSimpleName()));
	}

	public static Boolean toJavaBoolean(Object sqlTypeValue) {

		if (sqlTypeValue == null)
			return false;

		Class<?> sqlType = sqlTypeValue.getClass();

		if (String.class.equals(sqlType))
			return Boolean.parseBoolean((String) sqlTypeValue);

		if (int.class.equals(sqlType) || Integer.class.equals(sqlType))
			return ((Integer) sqlTypeValue) == 1;

		if (long.class.equals(sqlType) || Long.class.equals(sqlType))
			return ((Long) sqlTypeValue) == 1;

		if (float.class.equals(sqlType) || Float.class.equals(sqlType))
			throw new RuntimeException("Can't convert Float-->>Boolean!");

		if (double.class.equals(sqlType) || Double.class.equals(sqlType))
			throw new RuntimeException("Can't convert Double-->>Boolean!");

		if (boolean.class.equals(sqlType) || Boolean.class.equals(sqlType))
			return (Boolean) sqlTypeValue;

		if (BigDecimal.class.equals(sqlType))
			return ((BigDecimal) sqlTypeValue).intValue() == 1;

		throw new RuntimeException(String.format("Can't convert %s-->>Boolean!", sqlType.getSimpleName()));
	}

	public static BigInteger toJavaBigInteger(Object sqlTypeValue) {

		if (sqlTypeValue == null)
			return null;

		Class<?> sqlType = sqlTypeValue.getClass();

		if (String.class.equals(sqlType))
			return new BigInteger((String) sqlTypeValue);

		if (int.class.equals(sqlType) || Integer.class.equals(sqlType))
			return new BigInteger(sqlTypeValue.toString());

		if (long.class.equals(sqlType) || Long.class.equals(sqlType))
			return new BigInteger(sqlTypeValue.toString());

		if (float.class.equals(sqlType) || Float.class.equals(sqlType)) {
			long longValue = ((Float) sqlTypeValue).longValue();
			return new BigInteger(((Long) longValue).toString());
		}

		if (double.class.equals(sqlType) || Double.class.equals(sqlType)) {
			long longValue = ((Double) sqlTypeValue).longValue();
			return new BigInteger(((Long) longValue).toString());
		}

		if (boolean.class.equals(sqlType) || Boolean.class.equals(sqlType))
			throw new RuntimeException("Can't convert Boolean-->>BigInteger!");

		if (BigInteger.class.equals(sqlType))
			return (BigInteger) sqlTypeValue;

		if (BigDecimal.class.equals(sqlType)) {
			long longValue = ((BigDecimal) sqlTypeValue).longValue();
			return new BigInteger(((Long) longValue).toString());
		}

		throw new RuntimeException(String.format("Can't convert %s-->>BigInteger!", sqlType.getSimpleName()));
	}

	public static BigDecimal toJavaBigDecimal(Object sqlTypeValue) {

		if (sqlTypeValue == null)
			return null;

		Class<?> sqlType = sqlTypeValue.getClass();

		if (String.class.equals(sqlType))
			return new BigDecimal((String) sqlTypeValue);

		if (int.class.equals(sqlType) || Integer.class.equals(sqlType))
			return new BigDecimal((Integer) sqlTypeValue);

		if (long.class.equals(sqlType) || Long.class.equals(sqlType))
			return new BigDecimal((Long) sqlTypeValue);

		if (float.class.equals(sqlType) || Float.class.equals(sqlType))
			return new BigDecimal((Float) sqlTypeValue);

		if (double.class.equals(sqlType) || Double.class.equals(sqlType))
			return new BigDecimal((Double) sqlTypeValue);

		if (boolean.class.equals(sqlType) || Boolean.class.equals(sqlType))
			throw new RuntimeException("Can't convert Boolean-->>BigDecimal!");

		if (BigDecimal.class.equals(sqlType))
			return (BigDecimal) sqlTypeValue;

		throw new RuntimeException(String.format("Can't convert %s-->>BigDecimal!", sqlType.getSimpleName()));
	}

	public static Date toJavaDate(Object sqlTypeValue) {

		if (sqlTypeValue == null)
			return null;

		Class<?> sqlType = sqlTypeValue.getClass();

		if (String.class.equals(sqlType)) {
			SimpleDateFormat defaultDateFormat = new SimpleDateFormat();
			try {
				return defaultDateFormat.parse((String) sqlTypeValue);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}

		if (java.sql.Date.class.equals(sqlType))
			return (Date) sqlTypeValue;

		if (java.sql.Timestamp.class.equals(sqlType))
			return (Date) sqlTypeValue;

		if (java.sql.Time.class.equals(sqlType))
			return (Date) sqlTypeValue;

		throw new RuntimeException(String.format("Can't convert %s-->>Date!", sqlType.getSimpleName()));
	}

	public static Calendar toJavaCalendar(Object sqlTypeValue) {
		Object date = toJavaDate(sqlTypeValue);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime((Date) date);
		return calendar;
	}
}
