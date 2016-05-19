package com.puff.web.view;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.puff.exception.ViewException;
import com.puff.framework.utils.JsonUtil;
import com.puff.framework.utils.StringUtil;

/**
 * JsonView.
 */
public class JsonView extends View {

	/**
	 * 
	 */

	private static final String contentType = ContentType.JSON.getType();
	private static final String IEContentType = ContentType.HTML.getType();
	private String json;

	public JsonView() {
	}

	public JsonView(String key, Object value) {
		if (key == null) {
			throw new IllegalArgumentException("The parameter key can not be null.");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key, value);
		this.json = JsonUtil.toJson(map);
	}

	public JsonView(String jsonText) {
		this.json = jsonText;
	}

	public JsonView(Object object) {
		this.json = JsonUtil.toJson(object);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void view() {
		if (json == null) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (Enumeration<String> attrs = request.getAttributeNames(); attrs.hasMoreElements();) {
				String key = attrs.nextElement();
				if (key.startsWith("org.apache.")) {
					continue;
				}
				Object value = request.getAttribute(key);
				map.put(key, value);
			}
			this.json = JsonUtil.toJson(map);
		}
		PrintWriter writer = null;
		try {
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setContentType(isIE() ? IEContentType : contentType);
			writer = response.getWriter();
			writer.write(json);
			writer.flush();
		} catch (IOException e) {
			throw new ViewException(e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private boolean isIE() {
		String header = request.getHeader("user-agent");
		return StringUtil.notEmpty(header) && header.toLowerCase().indexOf("msie") > -1;
	}
}