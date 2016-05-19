package com.puff.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.puff.framework.utils.StringUtil;
import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.web.mvc.Executor;
import com.puff.web.mvc.PuffContext;
import com.puff.web.view.HtmlView;
import com.puff.web.view.View;

public class PuffErrorHandler implements ErrorHandler {
	private static final Log log = LogFactory.get(PuffErrorHandler.class);

	@Override
	public View handle404() {
		HttpServletRequest request = PuffContext.getRequest();
		StringBuffer requestURL = request.getRequestURL();
		String queryString = request.getQueryString();
		if (StringUtil.notEmpty(queryString)) {
			requestURL.append("?").append(queryString);
		}
		StringBuilder sb = new StringBuilder(200);
		sb.append("This ip[").append(PuffContext.getIpAddr()).append("] request (").append(requestURL).append(") was not found on this server...");
		log.error(sb.toString());
		PuffContext.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
		return new HtmlView(create(sb, HttpServletResponse.SC_NOT_FOUND));
	}

	@Override
	public View handleExecption(Executor executor, Throwable t) {
		PuffContext.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return new HtmlView(create(getExceptionString(t), HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
	}

	public boolean ajax() {
		return "XMLHttpRequest".equals(PuffContext.getRequest().getHeader("x-requested-with"));
	}

	public static String create(Object error, int errCode) {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html>\n");
		sb.append("<html>\n");
		sb.append("<head>\n");
		sb.append("<meta http-equiv=\"content-type\" content=\"text/html;charset=UTF-8\">\n");
		sb.append("<title>Puff Error report</title>\n");
		sb.append("<style type=\"text/css\">\n");
		sb.append("\thtml,body{font-family: \"Microsoft Yahei\", Verdana, Arial, Helvetica, sans-serif; font-size: 14px;}\n");
		sb.append("\th1{font-size:100%;}\n");
		sb.append("\tstrong{font-weight:bold;}\n");
		sb.append("\t.type{padding:10px 30px 0 30px;color:#5b4f5b;}\n");
		sb.append("\t.code{font-size:48px;}\n");
		sb.append("\t.title{font-size:32px;}\n");
		sb.append("\t.exceptions{padding:10px 50px 0 5px;color:#000;}\n");
		sb.append("\t.exception{line-height:120%;}\n");
		sb.append("</style>\n");
		sb.append("</head>\n");
		sb.append("<body>\n\t");
		if (404 == errCode) {
			sb.append("<h1 class=\"type\"><strong class=\"code\">404</strong><strong class=\"title\">   抱歉，您访问的页面没有找到​</strong></h1>\n");
		} else if (500 == errCode) {
			sb.append("<h1 class=\"type\"><strong class=\"code\">500</strong><strong class=\"title\">   服务器内部错误</strong></h1>\n");
		}
		sb.append("\t<div class=\"exceptions\">\n");
		sb.append("\t\t<pre class=\"exception\">\n");
		sb.append(error).append("\n");
		sb.append("\t\t</pre>\n");
		sb.append("\t</div>\n");
		sb.append("</body>\n");
		sb.append("</html>");
		return sb.toString();
	}

	public static String getExceptionString(Throwable t) {
		if (t == null) {
			return "";
		}
		StringWriter strWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(strWriter, true);
		t.printStackTrace(writer);
		StringBuffer sb = strWriter.getBuffer();
		return sb.toString();
	}

}
