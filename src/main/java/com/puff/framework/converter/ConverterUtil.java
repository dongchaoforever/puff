package com.puff.framework.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.puff.exception.ConverterExecption;
import com.puff.exception.ExceptionUtil;
import com.puff.framework.utils.IOUtil;

public class ConverterUtil {
	public static Object timestamp2Other(Timestamp timestamp, Type type) {
		if (String.class == type) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.format(timestamp);
		}
		if (long.class == type || Long.class == type) {
			return timestamp.getTime();
		}
		if (java.util.Date.class == type) {
			return new java.util.Date(timestamp.getTime());
		}
		return timestamp;
	}

	public static Object time2Other(Time time, Type type) {
		if (String.class == type) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			return sdf.format(time);
		}
		if (long.class == type || Long.class == type) {
			return time.getTime();
		}
		return time;
	}

	public static Object date2Other(Date date, Type type) {
		if (long.class == type || Long.class == type) {
			return date.getTime();
		}
		if (java.util.Date.class == type) {
			return new java.util.Date(date.getTime());
		}
		if (Timestamp.class == type) {
			return new Timestamp(date.getTime());
		}
		if (String.class == type) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.format(date);
		}
		return date;
	}

	public static Object bytes2Other(byte[] bytes, Type type) {
		if (bytes == null) {
			return null;
		}
		if (String.class == type) {
			return new String(bytes);
		}
		if (Byte[].class == type) {
			return type;
		}
		return bytes;
	}

	public static Object bigDecimal2Other(BigDecimal bd, Type type) throws ConverterExecption {
		if (String.class == type) {
			return bd.toEngineeringString();
		}
		if (Integer.class == type || int.class == type) {
			return bd.intValue();
		}
		if (Long.class == type || long.class == type) {
			return bd.longValue();
		}
		if (Double.class == type || double.class == type) {
			return bd.doubleValue();
		}
		if (Float.class == type || float.class == type) {
			return bd.floatValue();
		}
		if (Short.class == type || short.class == type) {
			return bd.shortValue();
		}
		if (Boolean.class == type || boolean.class == type) {
			return bd.intValue() != 0;
		}
		return bd;
	}

	public static Object double2Other(Double value, Type type) throws ConverterExecption {
		try {
			if (String.class == type) {
				return value.toString();
			}
			if (Double.class == type || double.class == type) {
				return value;
			}
			if (Float.class == type || float.class == type) {
				return new BigDecimal(value).floatValue();
			}
			if (Short.class == type || short.class == type) {
				return new BigDecimal(value).shortValue();
			}
		} catch (Exception e) {
			throw new ConverterExecption("the double value:" + value + " can not converter to " + type, e);
		}
		return value;
	}

	public static Object string2Other(String value, Type type) throws ConverterExecption {
		try {
			if (Integer.class == type || int.class == type) {
				return Integer.parseInt(value);
			}
			if (Long.class == type || long.class == type) {
				return Long.parseLong(value);
			}
			if (Double.class == type || double.class == type) {
				return Double.parseDouble(value);
			}
			if (Float.class == type || float.class == type) {
				return Float.valueOf(value);
			}
			if (Short.class == type || short.class == type) {
				return Short.parseShort(value);
			}
			if (Boolean.class == type || boolean.class == type) {
				return "yes".equals(value) || "y".equals(value) || "true".equals(value) || "1".equals(value);
			}
		} catch (Exception e) {
			throw new ConverterExecption("the string value:" + value + " can not converter to " + type, e);
		}
		return value;
	}

	public static String handleClob(Clob clob) throws IOException, SQLException {
		if (clob == null) {
			return null;
		}
		Reader reader = null;
		try {
			reader = clob.getCharacterStream();
			char[] buffer = new char[(int) clob.length()];
			reader.read(buffer);
			return new String(buffer);
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static byte[] handleBlob(Blob blob) throws IOException, SQLException {
		if (blob == null) {
			return null;
		}
		InputStream is = null;
		try {
			is = blob.getBinaryStream();
			byte[] data = new byte[(int) blob.length()];
			is.read(data);
			return data;
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			IOUtil.close(is);
		}
	}

	public static String obj2String(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof String)
			return (String) obj;
		if (obj instanceof Clob) {
			try {
				return handleClob((Clob) obj);
			} catch (Exception e) {
				ExceptionUtil.throwRuntime(e);
			}
		}
		return obj.toString();
	}

	public static Integer obj2Int(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof Integer)
			return (Integer) obj;
		if (obj instanceof Long)
			return ((Long) obj).intValue();
		if (obj instanceof BigDecimal)
			return ((BigDecimal) obj).intValue();
		if (obj instanceof BigInteger)
			return ((BigInteger) obj).intValue();
		if (obj instanceof String) {
			String strValue = obj2String(obj).trim();
			if (strValue.equals(""))
				return 0;
			return Integer.parseInt(strValue);
		}
		throw new RuntimeException(String.format("Can't convert %s-->>Integer!", obj.getClass().getName()));
	}

	public static Long obj2Long(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof Long)
			return (Long) obj;
		if (obj instanceof Integer)
			return Long.parseLong(String.valueOf(obj));
		if (obj instanceof BigDecimal)
			return ((BigDecimal) obj).longValue();
		if (obj instanceof BigInteger)
			return ((BigInteger) obj).longValue();
		if (obj instanceof String) {
			String strValue = ((String) obj).trim();
			if (strValue.equals(""))
				return null;
			return Long.parseLong(strValue);
		}
		throw new RuntimeException(String.format("Can't convert %s-->>Long!", obj.getClass().getName()));
	}

	public static java.util.Date obj2Date(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof Date)
			return (Date) obj;

		if (obj instanceof Timestamp)
			return (Date) obj;

		if (obj instanceof Time)
			return (Date) obj;
		throw new RuntimeException(String.format("Can't convert %s-->>Date!", obj.getClass().getName()));
	}

}
