package com.puff.web.mvc;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.puff.framework.utils.StringUtil;

public class GetEncoderHttpServletRequestWrapper extends HttpServletRequestWrapper {
	private HttpServletRequest request;

	public GetEncoderHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		this.request = request;
	}

	@Override
	public String getParameter(String name) {
		String value = request.getParameter(name);
		if (value == null) {
			return "";
		}
		try {
			return new String(value.getBytes("ISO-8859-1"), request.getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			return value;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> map = new HashMap<String, String[]>();
		for (Enumeration<String> attrs = request.getParameterNames(); attrs.hasMoreElements();) {
			String name = attrs.nextElement();
			map.put(name, this.getParameterValues(name));
		}
		return map;
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] values = request.getParameterValues(name);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				String tmp = values[i];
				if (StringUtil.notEmpty(tmp)) {
					try {
						values[i] = new String(tmp.getBytes("ISO-8859-1"), request.getCharacterEncoding());
					} catch (UnsupportedEncodingException e) {

					}
				}
			}
		}
		return values;
	}

}