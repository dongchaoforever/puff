package com.puff.web.interceptor;

import com.puff.web.mvc.DispatcherExecutor;

/**
 * Interceptor
 */
public interface Interceptor {
	public void intercept(DispatcherExecutor executor);
}
