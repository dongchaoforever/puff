package com.puff.exception;

import com.puff.web.mvc.Executor;
import com.puff.web.view.View;

public interface ErrorHandler {

	public View handle404();

	public View handleExecption(Executor executor, Throwable t);

}
