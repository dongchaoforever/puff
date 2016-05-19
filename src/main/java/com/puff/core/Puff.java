package com.puff.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletContext;

import com.puff.exception.ErrorHandler;
import com.puff.framework.annotation.Before;
import com.puff.framework.annotation.Controller;
import com.puff.framework.annotation.Disable;
import com.puff.framework.annotation.FilterType;
import com.puff.framework.annotation.InterceptorChain;
import com.puff.framework.annotation.Mapping;
import com.puff.framework.annotation.Request;
import com.puff.framework.annotation.Request.DELETE;
import com.puff.framework.annotation.Request.GET;
import com.puff.framework.annotation.Request.POST;
import com.puff.framework.annotation.Request.PUT;
import com.puff.framework.annotation.Skip;
import com.puff.framework.annotation.Skip.ALL;
import com.puff.framework.annotation.Skip.MORE;
import com.puff.framework.annotation.Skip.ONE;
import com.puff.framework.annotation.Validate;
import com.puff.framework.container.ExecutorContainer;
import com.puff.framework.container.InterceptorContainer;
import com.puff.framework.utils.ClassUtil;
import com.puff.framework.utils.DateTime;
import com.puff.framework.utils.IOUtil;
import com.puff.framework.utils.ListUtil;
import com.puff.framework.utils.PathUtil;
import com.puff.framework.utils.StringUtil;
import com.puff.include.asm.reflect.MethodAccess;
import com.puff.ioc.loader.AnnotationLoader;
import com.puff.jdbc.core.DB;
import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.plugin.Plugin;
import com.puff.plugin.jar.StartInJar;
import com.puff.web.i18n.I18N;
import com.puff.web.interceptor.Interceptor;
import com.puff.web.interceptor.Validator;
import com.puff.web.mvc.Dispatcher;
import com.puff.web.mvc.DispatcherFactory;
import com.puff.web.mvc.Executor;
import com.puff.web.mvc.ExecutorProvider;
import com.puff.web.mvc.ExecutorMethod;
import com.puff.web.mvc.own.ResourceController;
import com.puff.web.mvc.own.ServerStatusController;
import com.puff.web.mvc.own.UrlMappingListController;
import com.puff.web.view.View;

/**
 * Puff framework core
 * 
 * @author dongchao
 *
 */
public enum Puff {
	;
	private static final Log log = LogFactory.get(Puff.class);
	public final static String version = VERSION.getVersionNumber();
	private static ServletContext servletContext;
	private static String contextPath = "";
	public static String SLASH = "/";
	private static String fileViewPath = "";
	private static long defMaxUploadSize = 10 * 1024 * 1024;// MB
	private static String[] beanPackage;
	private static String[] controllerPackage;
	private static String[] urlSuffix;
	private static boolean devMode = false;
	private static String encoding = "UTF-8";
	private static String urlParamSeparator = "-";
	private static final Map<String, List<Plugin>> plugins = new HashMap<String, List<Plugin>>();
	private static LinkedList<Dispatcher> dispatcherList;
	private static Dispatcher dispatcher;
	private static ErrorHandler errorHandler;
	private static ExecutorProvider executorProvider;
	private static Map<String, String> constantsMap = new HashMap<String, String>();
	private static I18N locales;
	private static String serverStartTime;

	public static ServletContext getServletContext() {
		return servletContext;
	}

	public static String getContextPath() {
		return contextPath;
	}

	public static String getFileViewPath() {
		return fileViewPath;
	}

	public static void setFileViewPath(String fileViewPath) {
		if (!fileViewPath.startsWith(SLASH) && !fileViewPath.startsWith(File.separator)) {
			fileViewPath = File.separator + fileViewPath;
		}
		Puff.fileViewPath = fileViewPath;
	}

	public static long getDefMaxUploadSize() {
		return defMaxUploadSize;
	}

	public static void setDefMaxUploadSize(long defMaxUploadSize) {
		Puff.defMaxUploadSize = defMaxUploadSize;
	}

	public static String[] getBeanPackage() {
		return beanPackage;
	}

	public static void setBeanPackage(String[] beanPackage) {
		Puff.beanPackage = beanPackage;
	}

	public static String[] getControllerPackage() {
		return controllerPackage;
	}

	public static void setControllerPackage(String controllerPackage[]) {
		Puff.controllerPackage = controllerPackage;
	}

	public static String[] getUrlSuffix() {
		return urlSuffix;
	}

	public static void setUrlSuffix(String[] urlSuffix) {
		Puff.urlSuffix = urlSuffix;
	}

	public static void setDevMode(boolean devMode) {
		Puff.devMode = devMode;
	}

	public static boolean getDevMode() {
		return devMode;
	}

	public static void setEncoding(String encoding) {
		Puff.encoding = encoding;
	}

	public static String getEncoding() {
		return encoding;
	}

	public static String getUrlParamSeparator() {
		return urlParamSeparator;
	}

	public static void setUrlParamSeparator(String urlParamSeparator) {
		Puff.urlParamSeparator = urlParamSeparator;
	}

	public static Plugin startPlugin(Class<? extends Plugin> klass, Properties prop) {
		if (klass != null) {
			Plugin plugin = (Plugin) ClassUtil.newInstance(klass.getName());
			plugin.init(prop);
			if (plugin.start()) {
				String name = klass.getName();
				log.info("The plugin '{0}' start success", name);
				addPlugin(plugin);
			}
			return plugin;
		}
		return null;
	}

	public static Plugin startPlugin(Plugin plugin, Properties prop) {
		plugin.init(prop);
		if (plugin.start()) {
			String name = plugin.getClass().getName();
			log.info("The plugin '{0}' start success", name);
			addPlugin(plugin);
		}
		return plugin;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getPlugin(Class<? extends Plugin> klass) {
		List<Plugin> list = Puff.plugins.get(klass.getName());
		return (T) (ListUtil.empty(list) ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getPlugins(Class<? extends Plugin> klass) {
		return (List<T>) Puff.plugins.get(klass.getName());
	}

	public static void addPlugin(Plugin plugin) {
		String key = plugin.getClass().getName();
		List<Plugin> list = null;
		if (Puff.plugins.containsKey(key)) {
			list = Puff.plugins.get(key);
		} else {
			list = new ArrayList<Plugin>();
		}
		list.add(plugin);
		Puff.plugins.put(key, list);
	}

	public static Map<String, String> getConstantsMap() {
		return constantsMap;
	}

	public static void setConstantsMap(Map<String, String> constantsMap) {
		Puff.constantsMap = constantsMap;
	}

	public static String getConstant(String key) {
		if (constantsMap == null || constantsMap.isEmpty() || !constantsMap.containsKey(key)) {
			return "";
		}
		return constantsMap.get(key);
	}

	public static String getConstant(String key, String defaultValue) {
		if (constantsMap == null || constantsMap.isEmpty() || !constantsMap.containsKey(key)) {
			return defaultValue;
		}
		return constantsMap.get(key);
	}

	public static ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public static void setErrorHandler(ErrorHandler errorHandler) {
		Puff.errorHandler = errorHandler;
	}

	public static ExecutorProvider getExecutorProvider() {
		return executorProvider;
	}

	public static void setExecutorProvider(ExecutorProvider executorProvider) {
		Puff.executorProvider = executorProvider;
	}

	public static I18N getLocales() {
		return locales;
	}

	public static void setLocales(I18N locales) {
		Puff.locales = locales;
	}

	public static LinkedList<Dispatcher> getDispatcherList() {
		return dispatcherList;
	}

	public static void setDispatcherList(LinkedList<Dispatcher> dispatcherList) {
		Puff.dispatcherList = dispatcherList;
	}

	protected static void setDispatcher(Dispatcher dispatcher) {
		Puff.dispatcher = DispatcherFactory.getDispatcher(dispatcherList, dispatcher);
		dispatcherList.addLast(dispatcher);
	}

	public static void addDispatcher(Dispatcher dispatcher) {
		Puff.dispatcher = DispatcherFactory.addDispatcher(Puff.dispatcher, dispatcher);
	}

	public static Dispatcher getDispatcher() {
		return dispatcher;
	}

	public static String serverStartTime() {
		return serverStartTime;
	}

	/**
	 * strat running Puff
	 * 
	 * @param servletContext
	 * @return
	 */

	public static void start(ServletContext servletContext) {
		serverStartTime = DateTime.currentTimeStamp();
		log.info("Puff " + version + " starting ......");
		Puff.servletContext = servletContext;
		String path = servletContext.getRealPath(SLASH);
		if (path == null) {
			try {
				URL url = servletContext.getResource(SLASH);
				if (url != null && "file".equals(url.getProtocol())) {
					path = URLDecoder.decode(url.getFile(), encoding);
				} else {
					throw new IllegalStateException("Can't get webroot dir, url = " + url);
				}
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
		PathUtil.setWebRootPath(path);
		String tmp_path = servletContext.getContextPath();
		if (!SLASH.equals(tmp_path)) {
			Puff.contextPath = tmp_path;
		}
		run();
	}

	public static void run() {
		InputStream inputStream = null;
		PuffConfig config = null;
		try {
			inputStream = PathUtil.fromClassPath("/Puff.xml");
			if (inputStream == null) {
				log.warn("can not find core config file 'Puff.xml' in classpath, use default...");
				inputStream = PathUtil.fromJar("resource/default.config/Puff.xml");
			}
			config = new PuffConfig(inputStream);
			config.initPuff();
			DB.start();
			config.initBean();
			config.initExecutorProvider();
			config.initDispatcher();
			defaultMapping();
			config.initMonitorInfo();

			try {
				StartInJar.before();
			} catch (Exception e) {
				log.error("Start jar fail ", e);
			}

			config.initMapping();
			log.info("Puff start success!!!  ");
		} catch (Throwable e) {
			log.error("Start Puff fail", e);
			throw new RuntimeException("Start Puff fail ", e);
		} finally {
			IOUtil.close(inputStream);
		}

		config.initPlugin();

		try {
			StartInJar.after();
		} catch (Exception e) {
			log.error("Start jar fail ", e);
		}

	}

	/**
	 * running in main
	 */
	public static void runTestModule() {
		InputStream inputStream = null;
		PuffConfig config = null;
		try {
			inputStream = PathUtil.fromClassPath("/Puff.xml");
			if (inputStream == null) {
				log.warn("can not find core config file 'Puff.xml' in classpath, use default...");
				inputStream = PathUtil.fromJar("com/puff/core/Puff.xml");
			}
			config = new PuffConfig(inputStream);
			config.initPuff();
			DB.start();
			config.initBean();
			log.info("Puff start Test success ......");
		} catch (Throwable e) {
			log.error("Start Puff fail", e);
			throw new RuntimeException("Start Puff fail ", e);
		} finally {
			IOUtil.close(inputStream);
		}
		config.initPlugin();
	}

	private static void defaultMapping() throws SecurityException, NoSuchMethodException {
		addSysMapping(UrlMappingListController.class, "index");
		addSysMapping(ResourceController.class, "index");
		addSysMapping(ResourceController.class, "login");
		addSysMapping(ResourceController.class, "authcode");
		addSysMapping(ServerStatusController.class, "index");
	}

	public static void addSysMapping(Class<?> clazz, String methodName) throws SecurityException, NoSuchMethodException {
		Controller controller = clazz.getAnnotation(Controller.class);
		if (controller == null) {
			throw new IllegalArgumentException("the class: " + clazz.getName() + " must have annotation @Controller .");
		}
		Method method = clazz.getMethod(methodName);
		if (method == null) {
			throw new NoSuchMethodError();
		}
		method.setAccessible(true);
		AnnotationLoader loader = new AnnotationLoader();
		loader.load(clazz, controller.scope());
		String controllerKey = controller.value().trim();
		String executorKey = controllerKey;
		if (!"index".equals(methodName)) {
			executorKey = controllerKey + "/" + methodName;
		}
		MethodAccess access = ExecutorMethod.regMethodAccess(clazz);
		int index = access.getIndex(method.getName(), method.getParameterTypes());
		Executor executor = new Executor(clazz.getName(), controllerKey, executorKey, getRequetMethod(method), method, index, new String[] {}, controller.report());
		ExecutorContainer.putSysConf(executorKey, executor);
	}

	/**
	 * add url mapping the class must have @Controller
	 * 
	 * @param clazz
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws Exception
	 */
	public static void addMapping(Class<?> clazz) throws InstantiationException, IllegalAccessException {
		Disable disable = clazz.getAnnotation(Disable.class);
		if (disable != null) {
			return;
		}
		Controller controller = clazz.getAnnotation(Controller.class);
		if (controller == null) {
			throw new IllegalArgumentException("the class: " + clazz.getName() + " must have annotation @Controller .");
		}
		AnnotationLoader loader = new AnnotationLoader();
		loader.load(clazz, controller.scope());

		String controllerKey = controller.value().trim();
		if (StringUtil.empty(controllerKey)) {
			throw new IllegalArgumentException("The controllerKey can not be null, for example:@Controller(\"/puff\") ,please check you controller: " + clazz + " ");
		}
		if (!controllerKey.startsWith(SLASH)) {
			controllerKey = SLASH + controllerKey;
		}
		if (controllerKey.endsWith(SLASH) && !controllerKey.equals(SLASH)) {
			controllerKey = controllerKey.substring(0, controllerKey.length() - 1);
		}

		LinkedList<Before> interceptorList = new LinkedList<Before>();
		int flag = 0;
		for (Class<?> tempClazz = clazz; tempClazz != Object.class; tempClazz = tempClazz.getSuperclass(), flag++) {
			InterceptorChain interceptorChain = tempClazz.getAnnotation(InterceptorChain.class);
			if (interceptorChain != null) {
				Before[] interceptors = interceptorChain.value();
				if (flag == 0) {
					for (Before before : interceptors) {
						if (interceptorList.contains(before)) {
							interceptorList.remove(before);
						}
						interceptorList.add(before);
					}
				} else {
					for (int i = interceptors.length; i > 0;) {
						Before before = interceptors[--i];
						if (interceptorList.contains(before)) {
							interceptorList.remove(before);
						}
						interceptorList.addFirst(before);
					}
				}
			}
		}
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			disable = clazz.getAnnotation(Disable.class);
			if (disable != null) {
				continue;
			}
			Class<?> retType = method.getReturnType();
			if (Modifier.isPublic(method.getModifiers()) && (View.class.isAssignableFrom(retType) || method.getAnnotation(Mapping.class) != null)) {
				String methodName = method.getName();
				String mapping = methodName;
				Mapping m = method.getAnnotation(Mapping.class);
				if (m != null) {
					String value = m.value().trim();
					if (StringUtil.notEmpty(value)) {
						if (value.startsWith(SLASH)) {
							value = value.substring(1);
						}
						if (value.endsWith(SLASH)) {
							value = value.substring(0, value.length() - 1);
						}
						mapping = value;
					}
				}
				LinkedList<String> tempList = new LinkedList<String>();
				String name = "";
				if (interceptorList != null) {
					for (Before before : interceptorList) {
						FilterType type = before.type();
						String[] filterMethod = before.method();
						switch (type) {
						case TARGET:
							if (filterMethod == null || filterMethod.length == 0 || StringUtil.empty(filterMethod[0])) {
								Class<? extends Interceptor>[] value = before.value();
								if (value != null && value.length > 0) {
									name = value[0].getName();
								} else {
									throw new IllegalArgumentException("The @Before must set value ...");
								}
								if (before.singleton()) {
									InterceptorContainer.add(name);
								}
								tempList.add(name);
							} else {
								Arrays.sort(filterMethod);
								if (Arrays.binarySearch(filterMethod, methodName) >= 0) {
									Class<? extends Interceptor>[] value = before.value();
									if (value != null && value.length > 0) {
										name = value[0].getName();
									} else {
										throw new IllegalArgumentException("The @Before must set value ...");
									}
									if (before.singleton()) {
										InterceptorContainer.add(name);
									}
									tempList.add(name);
								}
							}
							break;
						case EXCLUDE:
							if (filterMethod == null || filterMethod.length == 0 || StringUtil.empty(filterMethod[0]))
								throw new IllegalArgumentException("The interceptor: " + clazz + " filter type is exclude ,so you must assign exclude methods!!! ");
							Arrays.sort(filterMethod);
							if (Arrays.binarySearch(filterMethod, methodName) < 0) {
								Class<? extends Interceptor>[] value = before.value();
								if (value != null && value.length > 0) {
									name = value[0].getName();
								} else {
									throw new IllegalArgumentException("The @Before must set ...");
								}
								if (before.singleton()) {
									InterceptorContainer.add(name);
								}
								tempList.add(name);
							}
							break;
						default:
							break;
						}
					}
				}

				Before before = method.getAnnotation(Before.class);
				if (before != null) {
					Class<? extends Interceptor>[] value = before.value();
					if (value != null && value.length > 0) {
						for (Class<? extends Interceptor> klass : value) {
							name = klass.getName();
							if (tempList.contains(name)) {
								tempList.remove(name);
							}
							tempList.addLast(name);
							if (before.singleton()) {
								InterceptorContainer.add(name);
							}
						}
					} else {
						throw new IllegalArgumentException("The @Before must set value or clazz...");
					}
				}

				Validate validate = method.getAnnotation(Validate.class);
				if (validate != null) {
					Class<? extends Validator> value = validate.value();
					if (value != null && !Validator.class.equals(value)) {
						name = value.getName();
					} else {
						throw new IllegalArgumentException("The @Validate must set value ...");
					}
					if (tempList.contains(name)) {
						tempList.remove(name);
					}
					tempList.addLast(name);
				}
				ALL all = clazz.getAnnotation(Skip.ALL.class);
				if (all != null) {
					tempList.clear();
				}
				all = method.getAnnotation(Skip.ALL.class);
				if (all != null) {
					tempList.clear();
				}
				MORE more = clazz.getAnnotation(Skip.MORE.class);
				if (more != null) {
					Class<? extends Interceptor>[] moreClass = more.value();
					for (Class<? extends Interceptor> mc : moreClass) {
						name = mc.getName();
						if (tempList.contains(name)) {
							tempList.remove(name);
						}
					}
				}
				more = method.getAnnotation(Skip.MORE.class);
				if (more != null) {
					Class<? extends Interceptor>[] moreClass = more.value();
					for (Class<? extends Interceptor> mc : moreClass) {
						name = mc.getName();
						if (tempList.contains(name)) {
							tempList.remove(name);
						}
					}
				}

				ONE one = clazz.getAnnotation(Skip.ONE.class);
				if (one != null) {
					name = one.value().getName();
					if (tempList.contains(name)) {
						tempList.remove(name);
					}
				}
				one = method.getAnnotation(Skip.ONE.class);
				if (one != null) {
					name = one.value().getName();
					if (tempList.contains(name)) {
						tempList.remove(name);
					}
				}
				String executorKey = mapping.equals("index") ? controllerKey : controllerKey.equals(SLASH) ? SLASH + mapping : controllerKey + SLASH + mapping;
				MethodAccess access = ExecutorMethod.regMethodAccess(clazz);
				int index = access.getIndex(method.getName(), method.getParameterTypes());
				Executor executor = new Executor(clazz.getName(), controllerKey, executorKey, Puff.getRequetMethod(method), method, index,
						tempList.toArray(new String[tempList.size()]), controller.report());
				ExecutorContainer.put(executorKey, executor);
			}
		}
	}

	public static String getRequetMethod(Method method) {
		GET get = method.getAnnotation(Request.GET.class);
		if (get != null) {
			return "GET";
		}
		POST post = method.getAnnotation(Request.POST.class);
		if (post != null) {
			return "POST";
		}
		PUT put = method.getAnnotation(Request.PUT.class);
		if (put != null) {
			return "PUT";
		}
		DELETE delete = method.getAnnotation(Request.DELETE.class);
		if (delete != null) {
			return "DELETE";
		}
		return "ALL";
	}

	/**
	 * stop Puff
	 * 
	 * @return
	 */
	public static void stop() {
		if (plugins != null) {
			for (Entry<String, List<Plugin>> entry : plugins.entrySet()) {
				List<Plugin> plugins = entry.getValue();
				if (!ListUtil.empty(plugins)) {
					for (Plugin plugin : plugins) {
						if (plugin != null) {
							plugin.stop();
						}
					}
				}
			}
		}
	}

}