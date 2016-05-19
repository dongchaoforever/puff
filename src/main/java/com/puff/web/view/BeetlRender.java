package com.puff.web.view;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.exception.BeetlException;
import org.beetl.ext.web.SessionWrapper;
import org.beetl.ext.web.WebRender;
import org.beetl.ext.web.WebVariable;

public class BeetlRender extends WebRender {

	private GroupTemplate gt;
	private boolean byteOutput = true;

	public BeetlRender(GroupTemplate gt) {
		super(gt);
		this.gt = gt;
		this.byteOutput = gt.getConf().isDirectByteOutput();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void render(String key, HttpServletRequest request, HttpServletResponse response, Object... args) {
		Writer writer = null;
		OutputStream os = null;
		try {
			// response.setContentType(contentType);
			Template template = gt.getTemplate(key);
			Enumeration<String> attrs = request.getAttributeNames();
			while (attrs.hasMoreElements()) {
				String attrName = attrs.nextElement();
				template.binding(attrName, request.getAttribute(attrName));
			}
			WebVariable webVariable = new WebVariable();
			webVariable.setRequest(request);
			webVariable.setResponse(response);
			HttpSession session = request.getSession();
			webVariable.setSession(session);
			template.binding("session", new SessionWrapper(request, session));
			template.binding("servlet", webVariable);
			template.binding("request", request);
			template.binding("ctxPath", request.getContextPath());

			modifyTemplate(template, key, request, response, args);

			if (byteOutput) {
				os = response.getOutputStream();
				template.renderTo(os);
			} else {
				writer = response.getWriter();
				template.renderTo(writer);
			}

		} catch (IOException e) {
			handleClientError(e);
		} catch (BeetlException e) {
			handleBeetlException(e);
		}

		finally {
			try {
				if (writer != null)
					writer.close();
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				handleClientError(e);
			}
		}
	}

}
