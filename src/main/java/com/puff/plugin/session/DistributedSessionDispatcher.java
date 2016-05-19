package com.puff.plugin.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.puff.web.mvc.Dispatcher;
import com.puff.web.mvc.ExecutorProvider;

public class DistributedSessionDispatcher extends Dispatcher {

	@Override
	public void dispatching(String target, HttpServletRequest request, HttpServletResponse response, ExecutorProvider provider) {
		chain.dispatching(target, new DistributedSessionReqeust(request, response), response, provider);
	}
}
