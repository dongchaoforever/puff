package com.puff.plugin.jar;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import com.puff.framework.utils.ClassUtil;
import com.puff.framework.utils.IOUtil;
import com.puff.framework.utils.StringUtil;

public class StartInJar {

	public static void before() throws Exception {
		start("before");
	}

	public static void after() throws Exception {
		start("after");
	}

	private static void start(String type) throws Exception {
		ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
		if (ctxClassLoader != null) {
			Map<Integer, Properties> map = new TreeMap<Integer, Properties>(new Comparator<Integer>() {
				public int compare(Integer obj1, Integer obj2) {
					return obj1.compareTo(obj2);
				}
			});
			for (Enumeration<URL> e = ctxClassLoader.getResources("META-INF/puff-jar-start.properties"); e.hasMoreElements();) {
				InputStream is = null;
				try {
					URL url = e.nextElement();
					Properties property = new Properties();
					is = url.openStream();
					property.load(is);
					String loadModel = property.getProperty("load-model");
					if (StringUtil.notEmptyAndEqOther(loadModel, type)) {
						String loadOnStartup = property.getProperty("load-on-startup", "1");
						int idx;
						try {
							idx = Integer.parseInt(loadOnStartup);
						} catch (Exception ex) {
							idx = 1;
						}
						if (map.containsKey(idx)) {
							Set<Integer> set = map.keySet();
							Integer[] array = set.toArray(new Integer[set.size()]);
							Integer i = array[array.length - 1];
							idx = i + 1;
						}
						map.put(idx, property);
					}
				} catch (Exception ex) {
					throw ex;
				} finally {
					IOUtil.close(is);
				}
			}

			for (Entry<Integer, Properties> entry : map.entrySet()) {
				Properties properties = entry.getValue();
				String className = properties.getProperty("start-class");
				Object instance = ClassUtil.newInstance(className);
				String methodName = properties.getProperty("start-method", "run");
				Method method = instance.getClass().getMethod(methodName);
				ClassUtil.invokeMethod(instance, method);
			}
		}
	}
}
