package com.puff.web.view;

import java.io.IOException;
import java.io.PrintWriter;

import com.puff.exception.ViewException;

public class XmlView extends View {

	/**
	 * 
	 */
	private String contentType = ContentType.XML.getType();
	private String xmlStr;

	public XmlView(String xmlStr) {
		this.xmlStr = xmlStr;
	}

	@Override
	public void view() throws ViewException {
		PrintWriter writer = null;
		try {
			response.setContentType(contentType);
			writer = response.getWriter();
			writer.write(xmlStr);
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
