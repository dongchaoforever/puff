package com.puff.web.view;

import java.io.IOException;
import java.io.PrintWriter;

import com.puff.exception.ViewException;

public class HtmlView extends View {

	/**
	 * 
	 */
	private static final String contentType = View.ContentType.HTML.getType();
	private String text;

	public HtmlView(String html) {
		this.text = html;
	}

	@Override
	public void view() {
		PrintWriter writer = null;
		try {
			response.setContentType(contentType);
			writer = response.getWriter();
			writer.write(text);
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
