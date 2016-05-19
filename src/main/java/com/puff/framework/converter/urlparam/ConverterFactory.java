package com.puff.framework.converter.urlparam;

import java.util.HashMap;
import java.util.Map;

import com.puff.jdbc.core.Record;
import com.puff.log.Log;
import com.puff.log.LogFactory;

public class ConverterFactory {

	private static final Log log = LogFactory.get(ConverterFactory.class);

	private static final Map<Class<?>, Converter<?>> map = new HashMap<Class<?>, Converter<?>>();

	private static final Map<Class<?>, Object> defaultVal = new HashMap<Class<?>, Object>();

	private ConverterFactory() {

	}

	static {
		Converter<?> c = null;
		c = new BooleanConverter();
		map.put(boolean.class, c);
		defaultVal.put(boolean.class, c.defaultVal());
		map.put(Boolean.class, c);
		defaultVal.put(Boolean.class, c.defaultVal());

		c = new CharacterConverter();
		map.put(char.class, c);
		defaultVal.put(char.class, c.defaultVal());
		map.put(Character.class, c);
		defaultVal.put(Character.class, c.defaultVal());

		c = new ByteConverter();
		map.put(byte.class, c);
		defaultVal.put(byte.class, c.defaultVal());
		map.put(Byte.class, c);
		defaultVal.put(Byte.class, c.defaultVal());

		c = new ShortConverter();
		map.put(short.class, c);
		defaultVal.put(short.class, c.defaultVal());
		map.put(Short.class, c);
		defaultVal.put(Short.class, c.defaultVal());

		c = new IntegerConverter();
		map.put(int.class, c);
		defaultVal.put(int.class, c.defaultVal());
		map.put(Integer.class, c);
		defaultVal.put(Integer.class, c.defaultVal());

		c = new LongConverter();
		map.put(long.class, c);
		defaultVal.put(Integer.class, c.defaultVal());
		map.put(Long.class, c);
		defaultVal.put(Long.class, c.defaultVal());

		c = new FloatConverter();
		map.put(float.class, c);
		defaultVal.put(float.class, c.defaultVal());
		map.put(Float.class, c);
		defaultVal.put(Float.class, c.defaultVal());

		c = new DoubleConverter();
		map.put(double.class, c);
		defaultVal.put(double.class, c.defaultVal());
		map.put(Double.class, c);
		defaultVal.put(Double.class, c.defaultVal());

		c = new RecordConverter();
		map.put(Record.class, c);
		defaultVal.put(Record.class, c.defaultVal());
	}

	public static void loadExternalConverter(String typeClass, String converterClass) {
		try {
			Class<?> clazz = Class.forName(typeClass);
			Converter<?> converter = (Converter<?>) Class.forName(converterClass).newInstance();
			if (clazz == null) {
				throw new NullPointerException("Class is null.");
			}
			if (converter == null) {
				throw new NullPointerException("Converter is null.");
			}
			if (map.containsKey(clazz)) {
				log.warn("Cannot replace the exist converter for type '" + clazz.getName() + "'.");
				return;
			}
			map.put(clazz, converter);
		} catch (Exception e) {
			log.warn("Cannot load converter '" + converterClass + "' for type '" + typeClass + "'.", e);
		}
	}

	public static boolean canConvert(Class<?> clazz) {
		return clazz.equals(String.class) || map.containsKey(clazz);
	}

	public static boolean basicType(Class<?> clazz) {
		if (clazz.equals(Record.class)) {
			return false;
		}
		return clazz.equals(String.class) || map.containsKey(clazz);
	}

	public static Object convert(Class<?> clazz, String s) {
		if (canConvert(clazz)) {
			Converter<?> c = map.get(clazz);
			return c.getVal(s);
		}
		return null;
	}

	public static Object defaultVal(Class<?> clazz) {
		return defaultVal.get(clazz);
	}
}
