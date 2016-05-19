package com.puff.framework.container;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.puff.core.ClassProperty;
import com.puff.framework.utils.ClassUtil;
import com.puff.framework.utils.StringUtil;
import com.puff.include.asm.reflect.MethodAccess;
import com.puff.jdbc.core.ClassMethod;
import com.puff.log.Log;

public class FormBeanContainer {
	private final static Map<String, List<ClassProperty>> container = new HashMap<String, List<ClassProperty>>();

	public static List<ClassProperty> getClassProperty(Class<?> clazz) {
		String className = clazz.getName();
		List<ClassProperty> list = container.get(className);
		if (list == null || list.size() == 0) {
			List<Field> fields = ClassUtil.getField(clazz, new ArrayList<Field>());
			if (fields.size() == 0) {
				throw new IllegalArgumentException("The class " + clazz + " there is no fields");
			}
			MethodAccess access = ClassMethod.regMethodAccess(clazz);
			list = new ArrayList<ClassProperty>(fields.size());
			for (Field field : fields) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				String fieldName = field.getName();
				if ("serialVersionUID".equals(fieldName)) {
					continue;
				}
				if (field.getType().equals(Log.class)) {
					continue;
				}

				ClassProperty cp = new ClassProperty();
				cp.setFieldName(fieldName);
				field.setAccessible(true);
				Class<?> type = field.getType();
				cp.setJavaType(type);
				fieldName = StringUtil.firstCharToUpperCase(fieldName);
				String setMethodName = "set" + fieldName;
				int setMethodIdx = access.getIndex(setMethodName, type);
				String getMethodName = type.equals(boolean.class) ? "is" + fieldName : "get" + fieldName;
				int getMethodIdx = access.getIndex(getMethodName);
				cp.setSetMethodIdx(setMethodIdx);
				cp.setGetMethodIdx(getMethodIdx);
				list.add(cp);
			}
			container.put(className, list);
		}
		return list;
	}

}
