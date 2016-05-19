package com.puff.web.mvc;

import javax.servlet.http.HttpServletRequest;

public interface ExecutorProvider {

	public Executor getExecutor(HttpServletRequest request, String target);

}