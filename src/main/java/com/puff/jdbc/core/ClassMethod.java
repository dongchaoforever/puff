package com.puff.jdbc.core;

import java.util.HashMap;
import java.util.Map;

import com.puff.include.asm.reflect.MethodAccess;

public final class ClassMethod {

	private final static Map<String, MethodAccess> map = new HashMap<String, MethodAccess>();

	public static MethodAccess regMethodAccess(Class<?> clazz) {
		MethodAccess access = map.get(clazz.getName());
		if (access == null) {
			access = MethodAccess.get(clazz);
			map.put(clazz.getName(), access);
		}
		return access;
	}

	public static Object invokeMethod(Object object, int methodIdx, Object... args) {
		return invokeMethod(object, object.getClass().getName(), methodIdx, args);
	}

	public static Object invokeMethod(Object object, String className, int methodIdx, Object... args) {
		MethodAccess access = map.get(className);
		return access == null ? null : access.invoke(object, methodIdx, args);
	}

}
