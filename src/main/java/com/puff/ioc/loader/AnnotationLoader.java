package com.puff.ioc.loader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.puff.exception.ExceptionUtil;
import com.puff.framework.annotation.Bean;
import com.puff.framework.annotation.BeanScope;
import com.puff.framework.annotation.Inject;
import com.puff.framework.annotation.Transaction;
import com.puff.framework.utils.ClassUtil;
import com.puff.framework.utils.PackageSearch;
import com.puff.framework.utils.StringUtil;
import com.puff.ioc.BeanFactory;
import com.puff.ioc.info.BeanInfo;
import com.puff.ioc.info.Property;
import com.puff.log.Log;
import com.puff.log.LogFactory;

public class AnnotationLoader implements Loader {
	private static final Log LOG = LogFactory.get();

	public void load(Class<?> clazz, BeanScope scope) {
		BeanInfo info = new BeanInfo();
		String id = clazz.getName();
		info.setId(id);
		info.setClazz(clazz);
		info.setClassName(clazz.getName());
		info.setScope(scope);
		Transaction annotation = clazz.getAnnotation(Transaction.class);
		if (annotation != null) {
			info.setTransaction(true);
		} else {
			info.setTransaction(hasTransaction(clazz));
		}
		process(clazz, info);
	}

	private boolean hasTransaction(Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		for (Method m : methods) {
			Transaction annotation = m.getAnnotation(Transaction.class);
			if (annotation != null) {
				return true;
			}
		}
		return false;
	}

	private void process(Class<?> clazz, BeanInfo info) {
		List<Field> fields = ClassUtil.getField(clazz, new ArrayList<Field>());
		for (Field field : fields) {
			Inject inject = field.getAnnotation(Inject.class);
			if (inject != null) {
				String name = StringUtil.firstCharToLowerCase(field.getName());
				String value = inject.value();
				if (StringUtil.empty(value)) {
					value = name;
				}
				Property propInfo = new Property();
				propInfo.setName(name);
				propInfo.setValue(value);
				field.setAccessible(true);
				propInfo.setField(field);
				Class<?> type = field.getType();
				String fieldName = field.getName();
				fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				String setMethodName = "set" + fieldName;
				Method method = null;
				try {
					method = clazz.getMethod(setMethodName, type);
					if (method != null) {
						method.setAccessible(true);
						propInfo.setMethod(method);
					}
				} catch (Exception e) {
				}
				info.addProperties(propInfo);
			}
		}
		LOG.debug(info);
		BeanFactory.putBean(info);
	}

	public void load(String param) {
		Collection<Class<Bean>> calsses = PackageSearch.findClassByClazz(Bean.class, param, getClass());
		for (Class<Bean> clazz : calsses) {
			try {
				String simpleName = clazz.getSimpleName();
				Bean bean = clazz.getAnnotation(Bean.class);
				if (bean != null) {
					// 基本信息
					BeanInfo info = new BeanInfo();
					String id = StringUtil.empty(bean.id(), StringUtil.firstCharToLowerCase(simpleName));
					info.setId(id);
					info.setClazz(clazz);
					info.setClassName(clazz.getName());
					info.setScope(bean.scope());
					Transaction annotation = clazz.getAnnotation(Transaction.class);
					if (annotation != null) {
						info.setTransaction(true);
					} else {
						info.setTransaction(hasTransaction(clazz));
					}
					process(clazz, info);
				}
			} catch (Exception e) {
				LOG.error("load class <{0}> fail ", e, clazz.getName());
				ExceptionUtil.throwRuntime(e);
			}
		}
	}

}
