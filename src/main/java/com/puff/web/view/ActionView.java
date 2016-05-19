package com.puff.web.view;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.puff.core.Puff;
import com.puff.exception.ViewException;

public class ActionView extends View {

	/**
	 * 
	 */
	private String actionUrl;
	private static final String contextPath = getContxtPath();
	private static final String contentType = View.ContentType.HTML.getType();
	private Map<String, String> param;

	private static String getContxtPath() {
		String cp = Puff.getContextPath();
		return ("".equals(cp) || "/".equals(cp)) ? null : cp;
	}

	public Map<String, String> getCarryParam() {
		return param;
	}

	public void setCarryParam(Map<String, String> param) {
		this.param = param;
	}

	public ActionView(String actionUrl) {
		if (contextPath != null && actionUrl.indexOf("://") == -1) {
			actionUrl = contextPath + actionUrl;
		}
		this.actionUrl = actionUrl;
	}

	public ActionView(String actionUrl, Map<String, String> param) {
		this(actionUrl);
		this.param = param;
	}

	public final void put(String name, String value) {
		if (param == null) {
			param = new HashMap<String, String>();
		}
		param.put(name, value);
	}

	public String getActionUrl() {
		return actionUrl;
	}

	@Override
	public void view() {
		StringBuilder sb = new StringBuilder(200);
		if (param != null && param.size() > 0) {
			for (Entry<String, String> entry : param.entrySet()) {
				sb.append(String.format("<input name='%s' value='%s' type='hidden'/>", entry.getKey(), entry.getValue()));
			}
		}
		String form = "<form id='__PUFF_ACTION_REDIRECT_FORM' action='%s' method='POST' >%s</form>";
		form = String.format(form, actionUrl, sb.toString());
		String js = "<script>document.getElementById('__PUFF_ACTION_REDIRECT_FORM').submit();</script>";
		StringBuilder html = new StringBuilder(400);
		html.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head>").append("<body>").append(form).append(js).append("</body></html>");
		PrintWriter writer = null;
		try {
			response.setContentType(contentType);
			writer = response.getWriter();
			writer.print(html);
		} catch (IOException e) {
			throw new ViewException(e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
