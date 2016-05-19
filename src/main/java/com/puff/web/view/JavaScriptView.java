package com.puff.web.view;

import java.io.IOException;
import java.io.PrintWriter;

import com.puff.exception.ViewException;

public class JavaScriptView extends View {

	/**
	 * 
	 */
	private static final String contentType = "text/javascript;charset=" + encoding;
	private String content;

	public JavaScriptView(String content) {
		this.content = content;
	}

	@Override
	public void view() {
		PrintWriter writer = null;
		try {
			response.setContentType(contentType);
			writer = response.getWriter();
			writer.write(content);
			writer.flush();
		} catch (IOException e) {
			throw new ViewException(e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

}
