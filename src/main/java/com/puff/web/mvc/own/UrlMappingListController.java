package com.puff.web.mvc.own;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.puff.framework.annotation.Controller;
import com.puff.framework.annotation.Request;
import com.puff.framework.container.ExecutorContainer;
import com.puff.framework.utils.FileUtil;
import com.puff.framework.utils.PathUtil;
import com.puff.framework.utils.StringUtil;
import com.puff.web.mvc.Executor;
import com.puff.web.mvc.PuffContext;
import com.puff.web.view.View;
import com.puff.web.view.ViewFactory;

@Controller(value = "/puff/owm/url-mapping", report = false)
public class UrlMappingListController {

	private static List<String> excludeUrl = new ArrayList<String>();
	private int count = 0;
	private static String htmlContent;

	static {
		excludeUrl.add("/puff/owm/resource");
		excludeUrl.add("/puff/owm/resource/login");
		excludeUrl.add("/puff/owm/resource/authcode");
		excludeUrl.add("/puff/owm/url-mapping");
		excludeUrl.add("/puff/owm/monitor/druid");
		excludeUrl.add("/puff/owm/monitor/druid/json");
		excludeUrl.add("/puff/owm/monitor/druid/res");
		excludeUrl.add("/puff/owm/monitor/server/status");

		htmlContent = FileUtil.toString("UTF-8", PathUtil.fromJar("resource/owm/url-mapping"));
	}

	@Request.GET
	public View index() {
		if (!MonitorInfo.isLogin()) {
			return MonitorInfo.login("url-mapping");
		} else {
			String html = bulidHtml();
			String content = htmlContent.replace("<tbody></tbody>", "<tbody>" + html + "</tbody>");
			String url = StringUtil.empty(PuffContext.getParameter("url"), "");
			content = content.replace("${url}", url);
			String className = StringUtil.empty(PuffContext.getParameter("class"), "");
			content = content.replace("${class}", className);
			String intecetpor = StringUtil.empty(PuffContext.getParameter("intecetpor"), "");
			content = content.replace("${intecetpor}", intecetpor);
			content = content.replace("${count}", count + "");
			return ViewFactory.html(content);
		}
	}

	private String bulidHtml() {
		StringBuilder sb = new StringBuilder();
		String url = PuffContext.getParameter("url");
		String className = PuffContext.getParameter("class");
		String intecetpor = PuffContext.getParameter("intecetpor");
		boolean emptyUrl = StringUtil.empty(url);
		boolean emptyClassName = StringUtil.empty(className);
		boolean emptyIntecetpor = StringUtil.empty(intecetpor);
		Map<String, List<Executor>> map = new TreeMap<String, List<Executor>>(ExecutorContainer.getExecutorMap());

		for (Entry<String, List<Executor>> entry : map.entrySet()) {
			String key = entry.getKey();
			if (excludeUrl.contains(key)) {
				continue;
			}
			if (!emptyUrl) {
				key = key.replace(url, "<em>" + url + "</em>");
				if (key.indexOf(url) == -1) {
					continue;
				}
			}
			List<Executor> executors = entry.getValue();
			for (Executor executor : executors) {
				count++;
				String name = executor.beanId;
				if (!emptyClassName) {
					name = name.replace(className, "<em>" + className + "</em>");
					if (name.indexOf(className) == -1) {
						continue;
					}
				}
				String[] array = executor.interceptors;
				String interceptors = "";
				if (array != null) {
					for (String inter : array) {
						if (StringUtil.notEmpty(inter)) {
							int lastIndexOf = inter.lastIndexOf(".");
							inter = inter.substring(lastIndexOf + 1);
							interceptors += inter + " ";
						}
					}
				}
				if (!emptyIntecetpor) {
					interceptors = interceptors.replace(intecetpor, "<em>" + intecetpor + "</em>");
					if (interceptors.indexOf(intecetpor) == -1) {
						continue;
					}
				}
				sb.append("<tr>").append("\n");
				sb.append("<td>" + count + "</td>").append("\n");
				sb.append("<td>" + key + "</td>").append("\n");
				sb.append("<td align=\"center\">" + executor.requestMethod + "</td>").append("\n");
				sb.append("<td>" + name + "</td>").append("\n");
				sb.append("<td align=\"center\">" + executor.methodName + "</td>").append("\n");
				String temp = "0";
				if (StringUtil.notEmpty(interceptors)) {
					int size = interceptors.split(" ").length;
					if (!emptyIntecetpor) {
						temp = "<a class=\"tips\"><em>" + size + "</em><span>" + interceptors + "</span></a>";
					} else {
						temp = "<a class=\"tips\">" + size + "<span>" + interceptors + "</span></a>";
					}
				}
				sb.append("<td align=\"center\">").append(temp).append("</td>").append("\n");
				sb.append("</tr>");
			}
		}
		if (count == 0) {
			sb.append("<tr>").append("\n");
			sb.append("<td colspan =\"6\"align=\"center\" style=\"background-color: #EEE\">");
			sb.append("Sorry, can not find the results you want where");
			if (!emptyUrl) {
				sb.append(" the Url like '<em>" + url + "</em>'");
			}
			if (!emptyClassName) {
				sb.append(" the Class like '<em>" + className + "</em>'");
			}
			if (!emptyIntecetpor) {
				sb.append(" the Intecetpor like '<em>" + intecetpor + "</em>'");
			}
			sb.append("</td>\n");
			sb.append("</tr>");
		}
		return sb.toString();
	}
}