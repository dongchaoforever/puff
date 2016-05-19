package com.puff.ioc;

import java.util.HashMap;
import java.util.Map;

import com.puff.exception.ExceptionUtil;
import com.puff.framework.annotation.BeanScope;
import com.puff.framework.utils.ClassUtil;
import com.puff.framework.utils.StringUtil;
import com.puff.ioc.info.BeanInfo;
import com.puff.ioc.info.Property;
import com.puff.ioc.loader.AnnotationLoader;
import com.puff.ioc.loader.Loader;
import com.puff.jdbc.tx.proxy.TXProxy;
import com.puff.plugin.spring.BeanContextHelper;

public class BeanFactory {

	private static final Map<String, BeanInfo> beans = new HashMap<String, BeanInfo>();
	private static boolean springSupport = true;
	static {
		try {
			Class.forName("org.springframework.context.ApplicationContext");
		} catch (Exception e) {
			springSupport = false;
		}
	}

	public static void putBean(BeanInfo info) {
		String id = StringUtil.firstCharToLowerCase(info.getId());
		if (!beans.containsKey(id)) {
			beans.put(id, info);
		}
	}

	public synchronized static void loadPackage(String packageName) {
		Loader loader = new AnnotationLoader();
		loader.load(packageName);
	}

	private static int DEPTH = 15;

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String id) {
		Object object = null;
		try {
			object = get(id, DEPTH);
		} catch (Exception e) {
			ExceptionUtil.throwRuntime(e);
		}
		return (T) object;
	}

	public static <T> T getBean(Class<T> klass) {
		String simpleName = klass.getSimpleName();
		return getBean(StringUtil.firstCharToLowerCase(simpleName));
	}

	public static Object get(String id, int depth) throws Exception {
		Object instance = null;
		BeanInfo info = beans.get(id);
		if (info != null) {
			instance = info.getInstance();
			if (instance == null) {
				if (info.isTransaction()) {
					TXProxy proxy = TXProxyFactory.getProxy(info.getClazz());
					instance = proxy.getProxy();
				} else {
					instance = ClassUtil.newInstance(info.getClassName());
				}
				if (instance == null) {
					throw new RuntimeException(String.format("Can not instantiate id=%s, class=%s", id, info.getClassName()));
				}
				for (Property prop : info.getProperties()) {
					String pname = prop.getName();
					if ((depth--) > 0) {
						Object val = get(prop.getValue(), depth);
						ClassUtil.inject(instance, pname, val, prop);
					}
				}
				if (BeanScope.SINGLETON.equals(info.getScope())) {
					info.setInstance(instance);
				}
			}
		} else if (springSupport) {
			return BeanContextHelper.getBean(id);
		}
		return instance;
	}

}