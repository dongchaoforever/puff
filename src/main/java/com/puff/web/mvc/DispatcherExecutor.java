package com.puff.web.mvc;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.puff.core.Puff;
import com.puff.exception.ExceptionUtil;
import com.puff.framework.container.InterceptorContainer;
import com.puff.framework.converter.urlparam.ConverterFactory;
import com.puff.framework.utils.DateUtil;
import com.puff.framework.utils.StringUtil;
import com.puff.ioc.BeanFactory;
import com.puff.log.Log;
import com.puff.log.LogFactory;

/**
 * DispaterExcutor execute the target
 */
public class DispatcherExecutor {

	private static final Log LOG = LogFactory.get(DispatcherExecutor.class);

	private static final String URLPARAMSEPARATOR = "\\" + Puff.getUrlParamSeparator();
	private final Object target;
	private final String[] interceptors;
	private Executor executor;
	private Object result;
	private int index = 0;
	private final Object[] args;

	public DispatcherExecutor(Executor executor) {
		this.executor = executor;
		args = handleArgs();
		try {
			target = BeanFactory.getBean(executor.beanId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		interceptors = executor.interceptors;
	}

	private Object[] handleArgs() {
		Class<?>[] argTypes = executor.argTypes;
		if (argTypes.length > 0) {
			Object[] args = new Object[argTypes.length];
			String urlParam = PuffContext.getUrlParam();
			if (StringUtil.empty(urlParam)) {
				for (int i = 0; i < argTypes.length; i++) {
					Class<?> type = argTypes[i];
					args[i] = type.equals(String.class) ? "" : ConverterFactory.defaultVal(type);
				}
			} else {
				String[] arr = urlParam.split(URLPARAMSEPARATOR);
				if (arr.length < argTypes.length) {
					for (int i = 0; i < argTypes.length; i++) {
						Class<?> type = argTypes[i];
						if (i < arr.length) {
							args[i] = type.equals(String.class) ? arr[i] : ConverterFactory.convert(type, arr[i]);
						} else {
							args[i] = type.equals(String.class) ? "" : ConverterFactory.defaultVal(type);
						}
					}
				} else {
					for (int i = 0; i < argTypes.length; i++) {
						Class<?> type = argTypes[i];
						args[i] = type.equals(String.class) ? arr[i] : ConverterFactory.convert(type, arr[i]);
					}
				}
			}
			return args;
		}
		return null;
	}

	public Object getTarget() {
		return target;
	}

	public Executor getExecutor() {
		return executor;
	}

	public String getExecutorKey() {
		return executor.executorKey;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	/**
	 * execute
	 * 
	 */
	public void execute() {
		if (index == interceptors.length) {
			try {
				result = executor.execute(target, args);
			} catch (Exception e) {
				ExceptionUtil.throwRuntime(e);
			}
		} else {
			InterceptorContainer.get(interceptors[index++]).intercept(this);
		}
	}

	@SuppressWarnings("unchecked")
	public void executorReport(long startTime) {
		if (!executor.report) {
			return;
		}
		long end = System.currentTimeMillis();
		HttpServletRequest request = PuffContext.getRequest();
		StringBuilder sb = new StringBuilder("Puff Controller report---> ").append(DateUtil.long2Time(end)).append("\n");
		sb.append("--------------------------------------------------------------------------------------------");
		sb.append("\nUrl           : ").append(executor.executorKey);
		sb.append("\nController    : ").append(target.getClass().getName());
		sb.append("\nMethod        : ").append(executor.methodName);
		sb.append("\nMethodType    : ").append(request.getMethod());
		if (executor.argTypes != null && executor.argTypes.length > 0) {
			sb.append("\nUrlParameter  : ");
			for (int i = 0, size = args.length; i < size; i++) {
				if (i > 0)
					sb.append("\n                ");
				sb.append(args[i] + "");
			}
		}
		if (interceptors != null && interceptors.length > 0) {
			sb.append("\nInterceptor   : ");
			for (int i = 0, size = interceptors.length; i < size; i++) {
				if (i > 0)
					sb.append("\n                ");
				sb.append(interceptors[i]);
			}
		}
		Enumeration<String> e = request.getParameterNames();
		if (e.hasMoreElements()) {
			sb.append("\nParameter     : ");
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				String[] values = request.getParameterValues(name);
				if (values.length == 1) {
					sb.append(name).append("=").append(values[0]);
				} else {
					sb.append(name).append("[]={");
					for (int i = 0; i < values.length; i++) {
						if (i > 0)
							sb.append(",");
						sb.append(values[i]);
					}
					sb.append("}");
				}
				sb.append("  ");
			}
		}
		sb.append("\nStartTime     : ").append(DateUtil.long2Time(startTime)).append("    EndTime:  ").append(DateUtil.long2Time(end)).append("    Elapsed:  ")
				.append(end - startTime).append("millis");
		sb.append("\n--------------------------------------------------------------------------------------------\n");
		LOG.info(sb.toString());
	}
}
