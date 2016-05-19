package com.puff.web.mvc;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.puff.core.Puff;
import com.puff.framework.container.ExecutorContainer;
import com.puff.framework.utils.ListUtil;
import com.puff.log.Log;
import com.puff.log.LogFactory;

public class PuffExecutorProvider implements ExecutorProvider {
	private static final Log log = LogFactory.get(PuffExecutorProvider.class);
	private static final String encoding = Puff.getEncoding();

	@Override
	public Executor getExecutor(HttpServletRequest request, String requestURI) {
		List<Executor> executorList = ExecutorContainer.get(requestURI);
		String requestMethod = request.getMethod();
		if (ListUtil.notEmpty(executorList)) {
			for (Executor ex : executorList) {
				if (canInvoke(ex.requestMethod, requestMethod)) {
					return ex;
				}
			}
			return null;
		}
		int i = requestURI.lastIndexOf("/");
		if (i != -1) {
			executorList = (i == 0) ? ExecutorContainer.get("/") : ExecutorContainer.get(requestURI.substring(0, i));
			if (ListUtil.notEmpty(executorList)) {
				for (Executor ex : executorList) {
					if (canInvoke(ex.requestMethod, requestMethod)) {
						String urlParam;
						try {
							urlParam = new String(requestURI.substring(i + 1).getBytes("ISO-8859-1"), encoding);
						} catch (UnsupportedEncodingException e) {
							urlParam = "";
							log.error(" '" + requestURI.substring(i + 1) + " ' urldecoder fail, use default value \"\"", e);
						}
						PuffContext.setUrlParam(urlParam);
						return ex;
					}
				}
				return null;
			}
		}
		return null;
	}

	private boolean canInvoke(String executorMethod, String requestMethod) {
		return "ALL".equals(executorMethod) || requestMethod.equals(executorMethod);
	}

}
