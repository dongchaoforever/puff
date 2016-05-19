package com.puff.plugin.event;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.puff.exception.ExceptionUtil;
import com.puff.framework.utils.ClassUtil;
import com.puff.framework.utils.PackageSearch;
import com.puff.framework.utils.StringUtil;
import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.plugin.Plugin;

/**
 * 模拟spring的消息机制插件
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class EventPlugin implements Plugin {

	private final Log logger = LogFactory.get(EventPlugin.class);

	// 事件处理器
	private EventHandler handler = null;
	private ExecutorService pool = null;

	// 默认扫描所有的包
	private String[] scanPackage = {};

	@Override
	public void init(Properties prop) {
		String maxThreads = prop.getProperty("maxThreads", String.valueOf(Runtime.getRuntime().availableProcessors()));
		this.pool = Executors.newFixedThreadPool(Integer.valueOf(maxThreads), new ThreadFactory() {
			int i = 1;
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "Puff-TP-Event-" + (i++));
			}
		});
		String packageName = prop.getProperty("scanPackage");
		if (StringUtil.notEmpty(packageName)) {
			this.scanPackage = packageName.split("\\,");
		}
	}

	@Override
	public boolean start() {
		// 扫描注解 {@code Listener}
		Collection<Class<Listener>> calsses = new ArrayList<Class<Listener>>();
		for (String pk : scanPackage) {
			if (StringUtil.notEmpty(pk)) {
				Collection<Class<Listener>> klasses = PackageSearch.findClassByClazz(Listener.class, pk, getClass());
				calsses.addAll(klasses);
			}
		}

		if (calsses == null || calsses.isEmpty()) {
			logger.warn("Listener is empty! Please check it!");
			return false;
		}
		// 装载所有 {@code ApplicationListener} 的子类
		Class superClass = ApplicationListener.class;
		ApplicationListener listener;
		ArrayListMultimap<String, ApplicationListener<ApplicationEvent>> map = new ArrayListMultimap<String, ApplicationListener<ApplicationEvent>>();
		Type type;
		for (Class<?> clazz : calsses) {
			if (superClass.isAssignableFrom(clazz) && !superClass.equals(clazz)) {
				type = ((ParameterizedType) clazz.getGenericInterfaces()[0]).getActualTypeArguments()[0];
				if (type == null) {
					throw new IllegalArgumentException("the class implement ApplicationListener must assign genericity class ");
				}
				ApplicationEvent e = null;
				try {
					e = (ApplicationEvent) ((Class<ApplicationEvent>) type).newInstance();
				} catch (Exception ex) {
					ExceptionUtil.throwRuntime(ex);
				}
				listener = (ApplicationListener) ClassUtil.newInstance(clazz.getName());
				map.put(e.eventKey(), listener);
			}
		}
		handler = new EventHandler(map, pool);
		EventUtil.init(handler);
		return true;
	}

	@Override
	public boolean stop() {
		if (null != pool) {
			pool.shutdown();
		}
		pool = null;
		handler = null;
		return true;
	}

}
