package com.puff.framework.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.puff.framework.utils.ClassUtil;
import com.puff.framework.utils.StringUtil;
import com.puff.web.interceptor.Interceptor;

/**
 * InterceportContainer
 */
public final class InterceptorContainer {

	private final static List<String> singelClass = new ArrayList<String>();
	private final static Map<String, Interceptor> container = new HashMap<String, Interceptor>();

	public static boolean containsKey(String name) {
		return container.containsKey(name);
	}

	public static void add(String name) {
		if (StringUtil.notEmpty(name) && !container.containsKey(name)) {
			singelClass.add(name);
		}
	}

	public static Interceptor get(String name) {
		Interceptor inter = container.get(name);
		if (inter == null) {
			inter = (Interceptor) ClassUtil.newInstance(name);
			if (singelClass.contains(name)) {
				container.put(name, inter);
			}
		}
		return inter;
	}

	public static void clear() {
		container.clear();
	}

}