package com.puff.web.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.puff.core.Puff;
import com.puff.exception.ViewException;
import com.puff.web.mvc.PuffContext;

/**
 * View.
 */
public abstract class View {

	protected HttpServletRequest request;
	protected HttpServletResponse response;

	protected static final String encoding = Puff.getEncoding();

	public View() {
		this.request = PuffContext.getRequest();
		this.response = PuffContext.getResponse();
	}

	public void init(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public final View put(String name, Object value) {
		request.setAttribute(name, value);
		return this;
	}

	public abstract void view() throws ViewException;

	public static enum ContentType {
		JSON("application/json; charset=" + encoding + ""), TEXT("text/plan; charset=" + encoding + ""), HTML("text/html; charset=" + encoding + ""), XML(
				"text/xml; charset=" + encoding + "");
		private String type;

		public String getType() {
			return this.type;
		}

		private ContentType(String type) {
			this.type = type;
		}
	}
}
