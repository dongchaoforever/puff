package com.puff.web.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Dispatcher {

	protected Dispatcher chain;

	/**
	 * 
	 * @param target
	 * @param request
	 * @param response
	 * @param provider
	 */
	public abstract void dispatching(String target, HttpServletRequest request, HttpServletResponse response, ExecutorProvider provider);
}
