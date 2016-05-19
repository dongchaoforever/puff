package com.puff.web.dispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.puff.web.mvc.Dispatcher;
import com.puff.web.mvc.ExecutorProvider;
import com.puff.web.mvc.GetEncoderHttpServletRequestWrapper;

public class Zh_CN_GetDispatcher extends Dispatcher {

	@Override
	public void dispatching(String target, HttpServletRequest request, HttpServletResponse response, ExecutorProvider provider) {
		if (request.getMethod().equalsIgnoreCase("GET")) {
			request = new GetEncoderHttpServletRequestWrapper(request);
		}
		chain.dispatching(target, request, response, provider);
	}
}
