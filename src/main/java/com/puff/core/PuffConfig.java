package com.puff.core;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.puff.exception.ErrorHandler;
import com.puff.framework.annotation.Controller;
import com.puff.framework.parse.XNode;
import com.puff.framework.parse.XPathParser;
import com.puff.framework.utils.PackageSearch;
import com.puff.framework.utils.StringUtil;
import com.puff.ioc.BeanFactory;
import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.plugin.Plugin;
import com.puff.web.mvc.ControllerDispatcher;
import com.puff.web.mvc.Dispatcher;
import com.puff.web.mvc.ExecutorProvider;
import com.puff.web.mvc.PuffExecutorProvider;
import com.puff.web.mvc.own.MonitorInfo;

public class PuffConfig extends Config {
	private static final Log log = LogFactory.get(PuffConfig.class);
	public static final String SPLIT = ",";

	public PuffConfig(InputStream inputStream) {
		XPathParser parser = new XPathParser(inputStream);
		node = parser.evalNode("/Puff");
	}

	public void initPuff() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Map<String, String> constantMap = getConstantsInfo("constant");
		StringBuilder sb = new StringBuilder();
		String value = constantMap.get("encoding");
		if (StringUtil.notEmpty(value)) {
			Puff.setEncoding(value.trim());
		}
		sb.append("encoding: ").append(Puff.getEncoding()).append(", ");
		value = constantMap.get("urlSuffix");
		if (StringUtil.notEmpty(value)) {
			Puff.setUrlSuffix(value.trim().split(SPLIT));
			sb.append("urlSuffix: [").append(value).append("], ");
		}
		value = constantMap.get("devMode");
		if (StringUtil.notEmpty(value)) {
			boolean b = value.trim().equals("true");
			Puff.setDevMode(b);
		}
		sb.append("devMode: ").append(Puff.getDevMode()).append(", ");
		value = constantMap.get("fileViewPath");
		if (StringUtil.notEmpty(value)) {
			Puff.setFileViewPath(value);
			sb.append("fileViewPath: ").append(value).append(", ");
		}
		value = constantMap.get("defMaxUploadSize");
		if (StringUtil.notEmpty(value)) {
			try {
				long defMaxUploadSize = Long.valueOf(value);
				Puff.setDefMaxUploadSize(defMaxUploadSize * 1024 * 1024);
			} catch (NumberFormatException e) {
			}
		}
		sb.append("maxUploadSize: ").append(Puff.getDefMaxUploadSize()).append(", ");
		value = StringUtil.empty(constantMap.get("urlParamSeparator"), Puff.getUrlParamSeparator());
		if (value.contains(Puff.SLASH)) {
			throw new IllegalArgumentException("urlParaSepartor can not be null and can not contains \"/\"");
		}
		Puff.setUrlParamSeparator(value);
		sb.append("urlParamSeparator: ").append(Puff.getUrlParamSeparator()).append(", ");
		value = StringUtil.empty(constantMap.get("errorHandler"), "com.puff.exception.PuffErrorHandler");
		Class<?> clazz = Class.forName(value);
		if (!ErrorHandler.class.isAssignableFrom(clazz)) {
			throw new IllegalArgumentException("The class: '" + value + "' must implements com.puff.exception.ErrorHandler ");
		}
		ErrorHandler errorHandler = (ErrorHandler) clazz.newInstance();
		Puff.setErrorHandler(errorHandler);
		sb.append("errorHandler: ").append(value);

		constantMap = getInfo("scan");
		value = constantMap.get("controller");
		if (StringUtil.empty(value)) {
			sb.append(", ").append("controllerPackage: ").append(" ALL ");
		} else {
			Puff.setControllerPackage(value.split(SPLIT));
			sb.append(", ").append("controllerPackage: ").append(value);
		}

		value = constantMap.get("bean");
		if (StringUtil.empty(value)) {
			sb.append(", ").append("beanPackage: ").append(" ALL ");
		} else {
			Puff.setBeanPackage(value.split(SPLIT));
			sb.append(", ").append("beanPackage: ").append(value);
		}

		log.info("Puff running constant-->" + sb);
	}

	public void initDispatcher() {
		List<XNode> evalNodes = node.evalNodes("dispatcher");
		LinkedList<Dispatcher> dispatchers = new LinkedList<Dispatcher>();
		if (evalNodes != null && evalNodes.size() > 0) {
			for (XNode parnet : evalNodes) {
				String className = parnet.getStringAttribute("class");
				if (StringUtil.empty(className)) {
					throw new IllegalArgumentException(" please set the property 'class' in dispatcher element at Puff.xml ");
				}
				try {
					Class<?> clazz = Class.forName(className);
					if (!Dispatcher.class.isAssignableFrom(clazz)) {
						throw new IllegalArgumentException("The class: '" + className + "' must extends com.puff.mvc.Dispatcher ");
					}
					dispatchers.add((Dispatcher) clazz.newInstance());
				} catch (Throwable e) {
					log.error("Init dispatcher happend error...", e);
				}
			}
		}
		Puff.setDispatcherList(dispatchers);
		Puff.setDispatcher(new ControllerDispatcher());
	}

	public void initExecutorProvider() {
		List<XNode> evalNodes = node.evalNodes("executorProvider");
		if (evalNodes != null && evalNodes.size() > 0) {
			for (XNode parnet : evalNodes) {
				String className = parnet.getStringAttribute("class");
				if (StringUtil.empty(className)) {
					throw new IllegalArgumentException(" please set the property 'class' in executorProvider element at Puff.xml ");
				}
				try {
					Class<?> clazz = Class.forName(className);
					if (!ExecutorProvider.class.isAssignableFrom(clazz)) {
						throw new IllegalArgumentException("The class: '" + className + "' must extends com.puff.web.mvc.ExecutorProvider ");
					}
					Puff.setExecutorProvider((ExecutorProvider) clazz.newInstance());
				} catch (Throwable e) {
					log.error("Init executorContainer happend error...", e);
				}
			}
		} else {
			Puff.setExecutorProvider(new PuffExecutorProvider());
		}
	}

	public void initMapping() throws InstantiationException, IllegalAccessException {
		String[] packages = Puff.getControllerPackage();
		if (packages == null || packages.length == 0) {
			log.warn("Do not specify a scanning controller package, please check your puff.xml ...");
			return;
		}
		Collection<Class<Controller>> classes = PackageSearch.findClassByClazz(Controller.class, Arrays.asList(packages), getClass());
		if (classes == null || classes.size() == 0) {
			return;
		}
		log.info("start config url mapping");
		for (Class<?> clazz : classes) {
			Puff.addMapping(clazz);
		}
	}

	public void initPlugin() {
		List<XNode> evalNodes = node.evalNodes("plugin");
		Plugin plugin = null;
		if (evalNodes != null && evalNodes.size() > 0) {
			log.info("start install application plugin...");
			for (XNode parnet : evalNodes) {
				Properties prop = new Properties();
				String pluginName = parnet.getStringAttribute("name");
				String className = parnet.getStringAttribute("class");
				if (StringUtil.empty(className)) {
					throw new IllegalArgumentException(" please set the property 'class' in plugin element at Puff.xml ");
				}
				try {
					Class<?> clazz = Class.forName(className);
					if (!Plugin.class.isAssignableFrom(clazz)) {
						throw new IllegalArgumentException("The class: '" + className + "' must implements com.puff.plugin.Plugin ");
					}
					for (XNode child : parnet.getChildren()) {
						String name = child.getStringAttribute("name");
						String value = child.getStringAttribute("value");
						if (StringUtil.hasEmpty(name, value))
							continue;
						prop.put(name, value);
					}
					prop.put("pluginName", StringUtil.empty(pluginName, className));
					plugin = (Plugin) clazz.newInstance();
					Puff.startPlugin(plugin, prop);
				} catch (Throwable e) {
					log.error("The plugin '" + prop.getProperty("pluginName") + "' start fail", e);
				}
			}
		}
	}

	public void initBean() {
		String[] arr = Puff.getBeanPackage();
		if (arr == null) {
			log.warn("Do not specify a scanning bean package, please check your puff.xml ...");
			return;
		} else {
			log.info("start install bean ");
			for (String pg : arr) {
				BeanFactory.loadPackage(pg.trim());
			}
			log.info("bean install success  ");
		}
	}

	public void initMonitorInfo() {
		Map<String, String> constantMap = getConstantsInfo("monitor");
		String value = constantMap.get("username");
		if (StringUtil.notEmpty(value)) {
			MonitorInfo.setUserName(value.trim());
		}
		value = constantMap.get("password");
		if (StringUtil.notEmpty(value)) {
			MonitorInfo.setPassword(value);
		}
	}

}
