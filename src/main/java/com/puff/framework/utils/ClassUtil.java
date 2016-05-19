package com.puff.framework.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.puff.exception.ExceptionUtil;
import com.puff.ioc.info.Property;
import com.puff.log.Log;
import com.puff.log.LogFactory;

public class ClassUtil {
	private static final Log LOG = LogFactory.get();

	public static Object newInstance(String className) {
		Object obj = null;
		try {
			obj = Class.forName(className).newInstance();
			LOG.debug("new " + className + "");
		} catch (Exception e) {
			ExceptionUtil.throwRuntime(e);
		}
		return obj;
	}

	public static void inject(Object bean, String name, Object value, Property info) throws IllegalArgumentException, IllegalAccessException {
		if (value != null) {
			if (info.getMethod() != null) {
				invokeMethod(bean, info.getMethod(), value);
			} else if (info.getField() != null) {
				info.getField().set(bean, value);
			}
		}
	}

	/** 类型转换 */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object value, Class<T> type) {
		if (value != null && !type.isAssignableFrom(value.getClass())) {
			if (is(type, int.class, Integer.class)) {
				value = Integer.parseInt(String.valueOf(value));
			} else if (is(type, long.class, Long.class)) {
				value = Long.parseLong(String.valueOf(value));
			} else if (is(type, float.class, Float.class)) {
				value = Float.parseFloat(String.valueOf(value));
			} else if (is(type, double.class, Double.class)) {
				value = Double.parseDouble(String.valueOf(value));
			} else if (is(type, boolean.class, Boolean.class)) {
				value = Boolean.parseBoolean(String.valueOf(value));
			} else if (is(type, String.class)) {
				value = String.valueOf(value);
			}
		}
		return (T) value;
	}

	public static Object invokeMethod(Object bean, Method method, Object... args) {
		try {
			method.setAccessible(true);
			Class<?>[] types = method.getParameterTypes();
			int argCount = args == null ? 0 : args.length;
			if (argCount != types.length) {
				throw new RuntimeException(String.format("%s in %s", method.getName(), bean));
			}
			for (int i = 0; i < argCount; i++) {
				args[i] = cast(args[i], types[i]);
			}
			return method.invoke(bean, args);
		} catch (Exception e) {
			ExceptionUtil.throwRuntime(e);
		}
		return null;
	}

	public static Object invoke(Object bean, Method method, Object... args) {
		try {
			method.setAccessible(true);
			return method.invoke(bean, args);
		} catch (Exception e) {
			ExceptionUtil.throwRuntime(e);
		}
		return null;
	}

	public static boolean is(Object obj, Object... mybe) {
		if (obj != null && mybe != null) {
			for (Object mb : mybe)
				if (obj.equals(mb))
					return true;
		}
		return false;
	}

	public static boolean isNot(Object obj, Object... mybe) {
		return !is(obj, mybe);
	}

	public static List<Field> getField(Class<?> clazz, List<Field> list) {
		Field[] fields = clazz.getDeclaredFields();
		list.addAll(Arrays.asList(fields));
		Class<?> superclass = clazz.getSuperclass();
		if (superclass != Object.class) {
			getField(superclass, list);
		}
		return list;
	}

	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = ClassUtil.class.getClassLoader();
		}
		return cl;
	}

}
