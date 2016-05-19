package com.puff.web.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SpecialContent {

	public static PuffContext init(HttpServletRequest request, HttpServletResponse response) {
		return PuffContext.init(request, response);
	}

	public static void removeContext(PuffContext context) {
		context.remove();
	}

}
