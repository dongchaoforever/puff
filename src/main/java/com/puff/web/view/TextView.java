package com.puff.web.view;

import java.io.IOException;
import java.io.PrintWriter;

import com.puff.exception.ViewException;

public class TextView extends View {

	/**
	 * 
	 */
	private String contentType = ContentType.TEXT.getType();
	private String text;

	public TextView(String text) {
		this.text = text;
	}

	public TextView(String text, String contentType) {
		this.text = text;
		this.contentType = contentType;
	}

	public TextView(String text, ContentType contentType) {
		this.text = text;
		this.contentType = contentType.getType();
	}

	public TextView setContentType(String contentType) {
		this.contentType = contentType;
		return this;
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
