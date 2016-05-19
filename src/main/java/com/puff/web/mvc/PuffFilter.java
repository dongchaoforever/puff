package com.puff.web.mvc;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.puff.core.Puff;

/**
 * 
 * @author dongchaoforever@gmail.com
 * @date 2012-3-25 上午10:51:17
 * @Description: Puff global dispatcher Filter
 */
public final class PuffFilter implements Filter {

	private String encoding;
	private String[] urlSuffix;
	private Dispatcher dispatcher;
	private ExecutorProvider provider;

	@Override
	public void init(FilterConfig config) throws ServletException {
		Puff.start(config.getServletContext());
		encoding = Puff.getEncoding();
		dispatcher = Puff.getDispatcher();
		provider = Puff.getExecutorProvider();
		urlSuffix = Puff.getUrlSuffix();
		if (urlSuffix != null) {
			Arrays.sort(urlSuffix);
		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		request.setCharacterEncoding(encoding);
		response.setCharacterEncoding(encoding);
		String servletPath = request.getServletPath();
		int index = servletPath.indexOf(".");
		if (index != -1) {
			if (urlSuffix != null) {
				String suffix = servletPath.substring(index + 1);
				if (Arrays.binarySearch(urlSuffix, suffix) < 0) {
					chain.doFilter(request, response);
					return;
				} else {
					servletPath = servletPath.substring(0, index);
				}
			} else {
				chain.doFilter(request, response);
				return;
			}
		}
		dispatcher.dispatching(servletPath, request, response, provider);
	}

	@Override
	public void destroy() {
		Puff.stop();
	}
}
