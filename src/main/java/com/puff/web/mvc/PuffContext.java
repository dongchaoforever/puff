package com.puff.web.mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.puff.core.ClassProperty;
import com.puff.core.Puff;
import com.puff.framework.container.FormBeanContainer;
import com.puff.framework.converter.urlparam.ConverterFactory;
import com.puff.framework.utils.IOUtil;
import com.puff.framework.utils.NetUtil;
import com.puff.framework.utils.Security;
import com.puff.framework.utils.StringUtil;
import com.puff.jdbc.core.Record;
import com.puff.web.fileupload.FileUpload;
import com.puff.web.fileupload.UploadOverFlowException;
import com.puff.web.fileupload.MultipartRequest;
import com.puff.web.view.AuthCodeView;

public class PuffContext {

	private static final ThreadLocal<PuffContext> PuffContextThreadLocal = new ThreadLocal<PuffContext>();
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String executorKey;
	private Executor executor;
	private String urlParam;

	private PuffContext() {

	}

	/**
	 * init PuffContext
	 * 
	 * @param request
	 * @param response
	 */
	protected static PuffContext init(HttpServletRequest request, HttpServletResponse response) {
		PuffContext context = PuffContextThreadLocal.get();
		if (context == null) {
			context = new PuffContext();
		}
		context.request = request;
		context.response = response;
		PuffContextThreadLocal.set(context);
		return context;
	}

	/**
	 * remove PuffContext
	 */
	protected void remove() {
		request = null;
		response = null;
		executorKey = null;
		executor = null;
		urlParam = null;
		PuffContextThreadLocal.remove();
	}

	/**
	 */
	protected void setExecutor(String executorKey, Executor executor) {
		PuffContext context = PuffContextThreadLocal.get();
		context.executorKey = executorKey;
		context.executor = executor;
	}

	public static HttpServletRequest getRequest() {
		return PuffContextThreadLocal.get().request;
	}

	public static HttpServletResponse getResponse() {
		return PuffContextThreadLocal.get().response;
	}

	public static HttpSession getSession() {
		return PuffContextThreadLocal.get().request.getSession();
	}

	public static HttpSession getSession(boolean create) {
		return PuffContextThreadLocal.get().request.getSession(create);
	}

	public static String getExecutorKey() {
		return PuffContextThreadLocal.get().executorKey;
	}

	public static Executor getExecutor() {
		return PuffContextThreadLocal.get().executor;
	}

	public static void setUrlParam(String urlParam) {
		PuffContextThreadLocal.get().urlParam = urlParam;
	}

	public static String getUrlParam() {
		return PuffContextThreadLocal.get().urlParam;
	}

	public static String getUrlParam(int idx) {
		String[] array = getUrlParamArray();
		return (array != null && array.length > idx) ? array[idx] : "";
	}

	public static String getUrlParam(int idx, String defaultVal) {
		return StringUtil.empty(getUrlParam(idx), defaultVal);
	}

	public static List<String> getUrlParamList() {
		return Arrays.asList(getUrlParamArray());
	}

	public static List<String> getUrlParamList(String urlParaSeparator) {
		return Arrays.asList(getUrlParamArray(urlParaSeparator));
	}

	public static String[] getUrlParamArray() {
		return PuffContextThreadLocal.get().urlParam.split("\\" + Puff.getUrlParamSeparator());
	}

	public static String[] getUrlParamArray(String urlParaSeparator) {
		return PuffContextThreadLocal.get().urlParam.split("\\" + urlParaSeparator);
	}

	public static PuffContext setAttribute(String name, Object value) {
		PuffContext context = PuffContextThreadLocal.get();
		context.request.setAttribute(name, value);
		return context;
	}

	public static PuffContext setSessionAttribute(String key, Object value) {
		PuffContext context = PuffContextThreadLocal.get();
		context.request.getSession().setAttribute(key, value);
		return context;
	}

	public static PuffContext removeAttribute(String name) {
		PuffContext context = PuffContextThreadLocal.get();
		context.request.removeAttribute(name);
		return context;
	}

	public static PuffContext removeSessionAttribute(String key) {
		PuffContext context = PuffContextThreadLocal.get();
		HttpSession session = context.request.getSession(false);
		if (session != null) {
			session.removeAttribute(key);
		}
		return context;
	}

	@SuppressWarnings("unchecked")
	public static <T> T removeSessionAttr(String key) {
		Object attribute = null;
		HttpSession session = PuffContextThreadLocal.get().request.getSession(false);
		if (session != null) {
			attribute = session.getAttribute(key);
			session.removeAttribute(key);
		}
		return (T) attribute;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getPara(String name) {
		return (T) PuffContextThreadLocal.get().request.getParameter(name);
	}

	public static String getParameter(String name) {
		return PuffContextThreadLocal.get().request.getParameter(name);
	}

	public static String getParameter(String name, String defaultVal) {
		return StringUtil.empty(getParameter(name), defaultVal);
	}

	public static int getIntParam(String name) {
		return Integer.parseInt(getParameter(name));
	}

	public static int getIntParam(String name, int defVal) {
		try {
			return Integer.parseInt(getParameter(name));
		} catch (Exception e) {
			return defVal;
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String[]> getParameterMap() {
		return PuffContextThreadLocal.get().request.getParameterMap();
	}

	public static String[] getParameterValues(String name) {
		String[] parameterValues = PuffContextThreadLocal.get().request.getParameterValues(name);
		return parameterValues == null ? new String[0] : parameterValues;
	}

	public static List<String> getParameterList(String name) {
		return Arrays.asList(getParameterValues(name));
	}

	public static List<String> getParameterList(String name, String split) {
		String value = getParameter(name);
		if (StringUtil.notEmpty(value)) {
			String[] arr = value.split(split);
			return Arrays.asList(arr);
		}
		return Collections.emptyList();
	}

	public static String[] getParameterArr(String name, String split) {
		String value = getParameter(name);
		if (StringUtil.notEmpty(value)) {
			return value.split(split);
		}
		return new String[] {};
	}

	public static String readRequestData() {
		BufferedReader br = null;
		try {
			StringBuilder result = new StringBuilder();
			br = PuffContextThreadLocal.get().request.getReader();
			for (String line = null; (line = br.readLine()) != null;) {
				result.append(line).append("\n");
			}
			return result.toString();
		} catch (Exception cause) {
			throw cause instanceof RuntimeException ? (RuntimeException) cause : new RuntimeException(cause);
		} finally {
			IOUtil.close(br);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getAttribute(String name) {
		return (T) PuffContextThreadLocal.get().request.getAttribute(name);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getAttribute(String name, T defaultVal) {
		Object obj = getAttribute(name);
		return obj == null ? defaultVal : (T) obj;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getSessionAttribute(String key) {
		HttpSession session = PuffContextThreadLocal.get().request.getSession();
		return session == null ? null : (T) session.getAttribute(key);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getSessionAttribute(String key, boolean create) {
		HttpSession session = PuffContextThreadLocal.get().request.getSession(create);
		return session == null ? null : (T) session.getAttribute(key);
	}

	@SuppressWarnings("unchecked")
	public static void paramToAttr() {
		HttpServletRequest request = PuffContextThreadLocal.get().request;
		for (Enumeration<String> paramNames = request.getParameterNames(); paramNames.hasMoreElements();) {
			String paramName = paramNames.nextElement();
			String[] values = request.getParameterValues(paramName);
			if (values != null) {
				request.setAttribute(paramName, values.length == 1 ? values[0] : values);
			} else {
				request.setAttribute(paramName, values);
			}
		}
	}

	public static void paramToAttr(String... paramNames) {
		if (paramNames != null) {
			HttpServletRequest request = PuffContextThreadLocal.get().request;
			for (String paramName : paramNames) {
				String[] values = request.getParameterValues(paramName);
				if (values != null) {
					request.setAttribute(paramName, values.length == 1 ? values[0] : values);
				} else {
					request.setAttribute(paramName, values);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Record getRecord() {
		Record record = new Record();
		HttpServletRequest request = PuffContextThreadLocal.get().request;
		for (Enumeration<String> paramNames = request.getParameterNames(); paramNames.hasMoreElements();) {
			String parameterName = paramNames.nextElement();
			String[] values = request.getParameterValues(parameterName);
			if (values != null) {
				record.set(parameterName, values.length == 1 ? values[0] : values);
			} else {
				record.set(parameterName, values);
			}
		}
		return record;
	}

	public static <T> T getModel(Class<T> t) {
		return getModel(t, null);
	}

	public static <T> T getModel(Class<T> t, String prefix) {
		T obj = null;
		try {
			obj = t.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		List<ClassProperty> list = FormBeanContainer.getClassProperty(t);
		if (list != null) {
			Map<String, String[]> map = getParameterMap();
			for (ClassProperty cp : list) {
				String fieldName = cp.getFieldName();
				String name = StringUtil.empty(prefix) ? fieldName : prefix + "." + fieldName;
				if (!map.containsKey(name)) {
					continue;
				}
				String[] values = map.get(name);
				Class<?> type = cp.getJavaType();
				Object result = values;
				if (values.length == 1) {
					if (type.equals(String.class)) {
						result = values[0];
					} else {
						try {
							result = ConverterFactory.convert(type, values[0]);
						} catch (Exception e) {
							String err = " '{0}' in class:{1} convert fail, the field '{2}' is '{3}' , can not convert value '{4}' to '{5}' ";
							String errMsg = StringUtil.replaceArgs(err, fieldName, t.getName(), fieldName, type.getName(), values[0], t.getName());
							throw new IllegalArgumentException(errMsg, e);
						}
					}
				}
				try {
					cp.invokeSet(obj, result);
				} catch (Exception e) {
					String err = "set '{0}' in class:{1} fail, the field '{2}' is '{3}', the value is '{4}' ";
					String errMsg = StringUtil.replaceArgs(err, fieldName, t.getName(), fieldName, type.getName(), result.getClass().getName());
					throw new IllegalArgumentException(errMsg, e);
				}
			}
		}
		return obj;
	}

	public static FileUpload getFile(String name) throws UploadOverFlowException {
		MultipartRequest request = null;
		try {
			request = MultipartRequest.getMultipartRequest();
		} catch (IOException e) {
			throw new RuntimeException(" upload files fail", e);
		}
		return request.getFile(name);
	}

	public static MultipartRequest getMultipartRequest() throws UploadOverFlowException {
		try {
			return MultipartRequest.getMultipartRequest();
		} catch (IOException e) {
			throw new RuntimeException(" upload files fail", e);
		}
	}

	public static List<FileUpload> getFiles() throws UploadOverFlowException {
		MultipartRequest request;
		try {
			request = MultipartRequest.getMultipartRequest();
		} catch (IOException e) {
			throw new RuntimeException(" upload files fail", e);
		}
		return request.getFiles();
	}

	public static boolean isRepeatSubmit() {
		String server_token = removeSessionAttr("server_token");
		String client_token = getParameter("client_token");
		if (StringUtil.hasEmpty(server_token, client_token)) {
			return true;
		}
		return server_token.equals(client_token);
	}

	public static boolean validateAuthCode(String userInputCaptcha) {
		if (StringUtil.blank(userInputCaptcha)) {
			return false;
		}
		userInputCaptcha = Security.md5(userInputCaptcha.toUpperCase());
		boolean result = userInputCaptcha.equals(findCookieValue(AuthCodeView.defCookieName));
		if (result) {
			failureCookie(AuthCodeView.defCookieName);
		}
		return result;
	}

	public static String getIpAddr() {
		String ips = PuffContextThreadLocal.get().request.getHeader("x-forwarded-for");
		if (ips == null || ips.length() == 0 || "unknown".equalsIgnoreCase(ips)) {
			ips = PuffContextThreadLocal.get().request.getHeader("Proxy-Client-IP");
		}
		if (ips == null || ips.length() == 0 || "unknown".equalsIgnoreCase(ips)) {
			ips = PuffContextThreadLocal.get().request.getHeader("WL-Proxy-Client-IP");
		}
		if (ips == null || ips.length() == 0 || "unknown".equalsIgnoreCase(ips)) {
			ips = PuffContextThreadLocal.get().request.getRemoteAddr();
		}

		String[] ipArray = ips.split(",");
		String clientIP = "";
		for (String ip : ipArray) {
			if (!("unknown".equalsIgnoreCase(ip))) {
				clientIP = ip;
				break;
			}
		}
		return clientIP;
	}

	public static String getUserAgentStr() {
		String userAgent = PuffContextThreadLocal.get().request.getHeader("user-agent");
		return userAgent == null ? "" : userAgent;
	}

	/**
	 * 查找指定请求中的指定名称的Cookie。
	 * 
	 * @param request
	 *            请求。
	 * @param name
	 *            cookie名称。
	 * @return 如果有相应名称的Cookie，则返回相应Cookie实例。没有返回null。
	 */
	public static Cookie findCookie(String name) {
		return HttpCookie.findCookie(PuffContextThreadLocal.get().request, name);
	}

	/**
	 * 查找指定请求中的指定名称Cookie的值，如果不存在将返回null。
	 * 
	 * @param request
	 *            请求。
	 * @param name
	 *            Cookie名称。
	 * @return cookie的值。
	 */
	public static String findCookieValue(String name) {
		return HttpCookie.findCookieValue(PuffContextThreadLocal.get().request, name);
	}

	/**
	 * 查找指定请求中的指定名称Cookie的值，如果不存在将返回默认值。
	 * 
	 * @param request
	 *            请求。
	 * @param name
	 *            Cookie名称。
	 * @return cookie的值。
	 */
	public static String findCookieValue(String name, String defVal) {
		return HttpCookie.findCookieValue(PuffContextThreadLocal.get().request, name, defVal);
	}

	/**
	 * 增加一个Cookie,使用默认域名。
	 * 
	 * @param request
	 *            请求。
	 * @param response
	 *            响应。
	 * @param name
	 *            Cookie名称 。
	 * @param value
	 *            Cookie的值。
	 * @param maxAge
	 *            生命周期。
	 */
	public static void addCookie(String name, String value, int maxAge) {
		HttpCookie.addCookie(PuffContextThreadLocal.get().request, PuffContextThreadLocal.get().response, name, value, null, maxAge);
	}

	/**
	 * 增加一个Cookie,使用指定域名。
	 * 
	 * @param request
	 *            请求。
	 * @param response
	 *            响应。
	 * @param name
	 *            Cookie名称 。
	 * @param value
	 *            Cookie的值。
	 * @param maxAge
	 *            生命周期。
	 */
	public static void addCookie(String name, String value, String domain, int maxAge) {
		String contextPath = StringUtil.empty(Puff.getContextPath(), "/");
		HttpCookie.addCookie(PuffContextThreadLocal.get().request, PuffContextThreadLocal.get().response, name, value, domain, contextPath, maxAge);
	}

	/**
	 * 增加一个Cookie.ContextPath如果为空或者长度为0，将使用"/".
	 * 
	 * @param request
	 *            当前请求。
	 * @param response
	 *            当前响应。
	 * @param name
	 *            cookie名称
	 * @param value
	 *            cookie值
	 * @param domain
	 *            cookie域名
	 * @param contextPath
	 *            cookie路径。
	 * @param maxAge
	 *            有效时间。
	 */
	public static void addCookie(String name, String value, String domain, String contextPath, int maxAge) {
		HttpCookie.addCookie(PuffContextThreadLocal.get().request, PuffContextThreadLocal.get().response, name, value, domain, contextPath, maxAge);
	}

	/**
	 * 失效一个Cookie.
	 * 
	 * @param request
	 *            当前请求。
	 * @param response
	 *            当前响应。
	 * @param name
	 *            Cookie名称。
	 * @param domain
	 *            Cookie域名。
	 * @param contextPath
	 *            有效路径。
	 */
	public static void failureCookie(String name, String domain, String contextPath) {
		addCookie(name, null, domain, contextPath, 0);
	}

	/**
	 * 将指定的Cookie失效掉。
	 * 
	 * @param request
	 *            请求
	 * @param response
	 *            响应。
	 * @param name
	 *            cookie名称。
	 * @param domain
	 *            cookie的域名。
	 */
	public static void failureCookie(String name, String domain) {
		String contextPath = StringUtil.empty(Puff.getContextPath(), "/");
		failureCookie(name, domain, contextPath);
	}

	/**
	 * 将指定的Cookie失效掉。
	 * 
	 * @param request
	 *            请求
	 * @param response
	 *            响应。
	 * @param name
	 *            cookie名称。
	 */
	public static void failureCookie(String name) {
		failureCookie(name, null);
	}

	/**
	 * 获取请求的完整地址。
	 * 
	 * @param request
	 *            请求。
	 * @return 完整地址。
	 */
	public static String completeTheRequestAddress() {
		StringBuilder buff = new StringBuilder(PuffContextThreadLocal.get().request.getRequestURL().toString());
		String queryString = PuffContextThreadLocal.get().request.getQueryString();
		if (queryString != null) {
			buff.append("?").append(queryString);
		}
		return buff.toString();
	}

	/**
	 * 将换行符替换成html页面使用的换行元素。
	 * 
	 * @param source
	 *            原始字符串。
	 * @return 替换后的字符串。
	 */
	public static String enterToHtmlWrap(String source) {
		if (StringUtil.empty(source)) {
			return source;
		} else {
			return source.replaceAll("\r\n", "<br/>");
		}
	}

	public static boolean ajax() {
		return "XMLHttpRequest".equalsIgnoreCase(PuffContextThreadLocal.get().request.getHeader("x-requested-with"));
	}

	public static boolean isInnerIP() {
		return NetUtil.isInnerIP(getIpAddr());
	}

}
