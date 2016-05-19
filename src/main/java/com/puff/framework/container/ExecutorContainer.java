package com.puff.framework.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.web.mvc.Executor;

/**
 * ExecutorContainer
 */
public class ExecutorContainer {
	private static final Log log = LogFactory.get(ExecutorContainer.class);
	private static final Map<String, List<Executor>> executorMap = new HashMap<String, List<Executor>>();

	public static void put(String key, Executor executor) {
		putSysConf(key, executor);
		log.info("Mapped URL path [" + key + "] onto executor-->" + executor.beanId + "  invoke method '" + executor.methodName + "'");
	}

	public static boolean exist(String key) {
		return executorMap.containsKey(key);
	}

	public static List<Executor> remove(String key) {
		return executorMap.remove(key);
	}

	public static List<Executor> get(String key) {
		return executorMap.get(key);
	}

	public static void putSysConf(String key, Executor executor) {
		List<Executor> list = null;
		if (executorMap.containsKey(key)) {
			list = executorMap.get(key);
			for (Executor ex : list) {
				if (ex.requestMethod.equals("ALL")) {
					throw new IllegalArgumentException("the url '" + key + "' already mapping for class (" + ex.beanId + "-->" + ex.methodName + ") ,please check you controller: "
							+ executor.beanId + ", method:" + executor.methodName);
				}
				if (executor.requestMethod.equals("ALL")) {
					throw new IllegalArgumentException("the url '" + key + "' already mapping for class (" + ex.beanId + "-->" + ex.methodName + ") ,please check you controller: "
							+ executor.beanId + ", method:" + executor.methodName);
				}
				if (ex.executorKey.equals(executor.executorKey) && ex.requestMethod.equals(executor.requestMethod)) {
					throw new IllegalArgumentException("the url '" + key + "' already mapping for class (" + ex.beanId + "-->" + ex.methodName + ") ,please check you controller: "
							+ executor.beanId + ", method:" + executor.methodName);
				}
			}
			list.add(executor);
		} else {
			list = new ArrayList<Executor>();
			list.add(executor);
		}
		executorMap.put(key, list);
	}

	public static List<String> getExecutorKeys() {
		List<String> allActionKeys = new ArrayList<String>(executorMap.keySet());
		Collections.sort(allActionKeys);
		return allActionKeys;
	}

	public static Map<String, List<Executor>> getExecutorMap() {
		return executorMap;
	}

}