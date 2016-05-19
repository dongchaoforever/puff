package com.puff.web.dispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.puff.core.Puff;
import com.puff.web.mvc.Dispatcher;
import com.puff.web.mvc.ExecutorProvider;

public class ContextPathDispatcher extends Dispatcher {

	@Override
	public void dispatching(String target, HttpServletRequest request, HttpServletResponse response, ExecutorProvider provider) {
		request.setAttribute("ctx", Puff.getContextPath());
		chain.dispatching(target, request, response, provider);
	}
}
