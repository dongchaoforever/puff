package com.puff.web.mvc;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.puff.core.Puff;
import com.puff.framework.utils.StringUtil;

public class HttpCookie {
	/**
	 * 查找指定请求中的指定名称的Cookie。
	 * 
	 * @param request
	 *            请求。
	 * @param name
	 *            cookie名称。
	 * @return 如果有相应名称的Cookie，则返回相应Cookie实例。没有返回null。
	 */
	public static Cookie findCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return cookie;
				}
			}
		}
		return null;
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
	public static String findCookieValue(HttpServletRequest request, String name) {
		Cookie cookie = findCookie(request, name);
		return cookie != null ? cookie.getValue() : null;
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
	public static String findCookieValue(HttpServletRequest request, String name, String defVal) {
		Cookie cookie = findCookie(request, name);
		return cookie != null ? cookie.getValue() : defVal;
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
	public static void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge) {
		addCookie(request, response, name, value, null, maxAge);
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
	public static void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, String domain, int maxAge) {
		String contextPath = StringUtil.empty(Puff.getContextPath(), "/");
		addCookie(request, response, name, value, domain, contextPath, maxAge);
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
	public static void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, String domain, String contextPath, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAge);
		cookie.setSecure(request.isSecure());
		cookie.setPath(StringUtil.empty(contextPath, "/"));
		if (StringUtil.notEmpty(domain)) {
			cookie.setDomain(domain);
		}
		response.addCookie(cookie);
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
	public static void failureCookie(HttpServletRequest request, HttpServletResponse response, String name, String domain, String contextPath) {
		addCookie(request, response, name, null, domain, contextPath, 0);
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
	public static void failureCookie(HttpServletRequest request, HttpServletResponse response, String name, String domain) {
		String contextPath = StringUtil.empty(Puff.getContextPath(), "/");
		failureCookie(request, response, name, domain, contextPath);
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
	public static void failureCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		failureCookie(request, response, name, null);
	}
}
