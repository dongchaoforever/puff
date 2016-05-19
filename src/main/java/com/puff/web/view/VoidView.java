package com.puff.web.view;

import com.puff.core.Puff;
import com.puff.exception.ViewException;
import com.puff.framework.utils.StringUtil;
import com.puff.web.mvc.Executor;

public class VoidView extends View {

	/**
	 * 
	 */

	private static final String filePath = Puff.getFileViewPath();
	private String view;

	public VoidView(Executor executor) {
		this.view = filePath + defaultView(executor) + ".jsp";
	}

	private String defaultView(Executor executor) {
		if (StringUtil.notEmptyAndNotEqOther(executor.controllerKey, "/")) {
			return executor.controllerKey + "/" + executor.methodName;
		} else {
			return executor.methodName;
		}
	}

	@Override
	public void view() {
		try {
			request.getRequestDispatcher(view).forward(request, response);
		} catch (Exception e) {
			throw new ViewException(e);
		}
	}

}
