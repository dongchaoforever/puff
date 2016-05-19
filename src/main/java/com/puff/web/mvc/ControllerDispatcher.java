package com.puff.web.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.puff.core.Puff;
import com.puff.exception.ErrorHandler;
import com.puff.framework.converter.urlparam.ConverterFactory;
import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.web.view.NullView;
import com.puff.web.view.TextView;
import com.puff.web.view.View;

public class ControllerDispatcher extends Dispatcher {

	private static final Log log = LogFactory.get(ControllerDispatcher.class);
	private static final ErrorHandler errorHandler = Puff.getErrorHandler();

	@Override
	public void dispatching(String target, HttpServletRequest request, HttpServletResponse response, ExecutorProvider provider) {
		PuffContext puffContext = null;
		Executor executor = null;
		View view = null;
		try {
			puffContext = PuffContext.init(request, response);
			executor = provider.getExecutor(request, target);
			if (executor == null) {
				view = errorHandler.handle404();
			} else {
				puffContext.setExecutor(executor.executorKey, executor);
				DispatcherExecutor dispaterExecutor = new DispatcherExecutor(executor);
				long start = System.currentTimeMillis();
				dispaterExecutor.execute();
				Object result = dispaterExecutor.getResult();
				if (result instanceof View) {
					view = (View) result;
				} else if (ConverterFactory.basicType(executor.returnType)) {
					view = new TextView(String.valueOf(result));
				} else {
					view = NullView.getInstance();
				}
				if (Puff.getDevMode()) {
					dispaterExecutor.executorReport(start);
				}
			}
		} catch (Throwable e) {
			log.error("Puff handle the controller '{0}' hanpend error", e, executor.executorKey);
			view = errorHandler.handleExecption(executor, e);
		} finally {
			render(view, executor);
			if (puffContext != null) {
				puffContext.remove();
			}
		}
	}

	public void render(View view, Executor executor) {
		if (view != null) {
			try {
				view.view();
			} catch (Exception e) {
				log.error("Puff View '{0}' hanpend error", e, executor.executorKey);
				errorHandler.handleExecption(executor, e).view();
			}
		}
	}
}