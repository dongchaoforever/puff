package com.puff.web.view;

import java.io.IOException;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;

import com.puff.exception.ViewException;

public class BeetlView extends View {

	public static GroupTemplate groupTemplate = null;
	private static BeetlRender beetlRender;
	static {
		try {
			Configuration cfg = Configuration.defaultConfiguration();
			groupTemplate = new GroupTemplate(cfg);
			beetlRender = new BeetlRender(groupTemplate);
		} catch (IOException e) {
			throw new RuntimeException("can not init beetl template ", e);
		}
	}

	/**
	 * 
	 */
	private static final String contentType = ContentType.HTML.getType();

	private String view;

	public BeetlView(String view) {
		super();
		this.view = view;
	}

	@Override
	public void view() throws ViewException {
		response.setContentType(contentType);
		beetlRender.render(view, request, response);
	}
}
