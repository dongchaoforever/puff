package com.puff.web.mvc.own;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.stat.DruidStatService;
import com.puff.framework.annotation.BeanScope;
import com.puff.framework.annotation.Controller;
import com.puff.framework.annotation.Request;
import com.puff.framework.utils.FileUtil;
import com.puff.framework.utils.PathUtil;
import com.puff.framework.utils.StringUtil;
import com.puff.web.mvc.PuffContext;
import com.puff.web.view.View;
import com.puff.web.view.ViewFactory;

@Controller(value = "/puff/owm/monitor/druid", scope = BeanScope.SINGLETON, report = false)
public class DruidStatViewController {

	private DruidStatService statService = DruidStatService.getInstance();
	protected static final Map<String, String> resMap = new HashMap<String, String>();

	@Request.GET
	public View index() {
		if (!MonitorInfo.isLogin()) {
			return MonitorInfo.login("druid-monitor");
		}
		String target = PuffContext.getParameter("target", "index.html");
		String content = resMap.get(target);
		if (StringUtil.empty(content)) {
			content = FileUtil.toString("UTF-8", PathUtil.fromJar("resource/druid/" + target));
			resMap.put(target, content);
		}
		return ViewFactory.html(content);
	}

	public View json() {
		if (!MonitorInfo.isLogin()) {
			return MonitorInfo.login("druid-monitor");
		}
		String target = "/" + PuffContext.getParameter("target");
		return ViewFactory.html(statService.service(target));
	}

	@Request.GET
	public View res() {
		String res = PuffContext.getParameter("res");
		String[] arr = res.split("\\.");
		String type = arr[arr.length - 1];
		String content = resMap.get(res);
		if (StringUtil.empty(content)) {
			content = FileUtil.toString("UTF-8", PathUtil.fromJar("resource/druid/" + type + "/" + res));
			resMap.put(res, content);
		}
		return ViewFactory.text(content, "js".equals(type) ? "text/javascript;charset=UTF-8" : "text/css;charset=UTF-8");
	}
}
