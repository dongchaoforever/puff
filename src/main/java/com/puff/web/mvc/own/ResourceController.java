package com.puff.web.mvc.own;

import java.util.HashMap;
import java.util.Map;

import com.puff.core.Puff;
import com.puff.framework.annotation.BeanScope;
import com.puff.framework.annotation.Controller;
import com.puff.framework.annotation.Request;
import com.puff.framework.utils.FileUtil;
import com.puff.framework.utils.PathUtil;
import com.puff.framework.utils.Security;
import com.puff.framework.utils.StringUtil;
import com.puff.web.mvc.PuffContext;
import com.puff.web.view.View;
import com.puff.web.view.ViewFactory;

@Controller(value = "/puff/owm/resource", scope = BeanScope.SINGLETON, report = false)
public class ResourceController {
	private static Map<String, String> tarMap = new HashMap<String, String>();

	static {
		tarMap.put("url-mapping", "/puff/owm/url-mapping");
		tarMap.put("server-status", "/puff/owm/monitor/server/status");
		tarMap.put("druid-monitor", "/puff/owm/monitor/druid");
	}

	@Request.POST
	public View login() {
		String username = PuffContext.getParameter("monitor.username");
		if (StringUtil.empty(username)) {
			return ViewFactory.json("msg", "username can not be null ! ");
		}
		String password = PuffContext.getParameter("monitor.password");
		if (StringUtil.empty(password)) {
			return ViewFactory.json("msg", "password can not be null ! ");
		}
		String authcode = PuffContext.getParameter("monitor.authcode");
		if (StringUtil.empty(authcode)) {
			return ViewFactory.json("msg", "authcode can not be null ! ");
		}
		if (!PuffContext.validateAuthCode(authcode)) {
			return ViewFactory.json("msg", "authcode is not correct ! ");
		}
		String target = PuffContext.getParameter("target");
		String url = tarMap.get(target);
		if (StringUtil.empty(url)) {
			return ViewFactory.json("msg", "Can't accept your request ! ");
		}
		if (MonitorInfo.getUserName().equals(username) && MonitorInfo.getPassword().equals(password)) {
			PuffContext.addCookie("_Monitor_User", Security.blowFish(username + password), -1);
			return ViewFactory.json("target", Puff.getContextPath() + url);
		} else {
			return ViewFactory.json("msg", "The user name or password is not correct ! ");
		}
	}

	@Request.GET
	public View authcode() {
		return ViewFactory.authCode();
	}

	@Request.GET
	public View index() {
		String res = PuffContext.getParameter("res");
		String[] arr = res.split("\\.");
		String type = arr[arr.length - 1];
		String content = DruidStatViewController.resMap.get(res);
		if (StringUtil.empty(content)) {
			content = FileUtil.toString("UTF-8", PathUtil.fromJar("resource/druid/" + type + "/" + res));
			DruidStatViewController.resMap.put(res, content);
		}
		return ViewFactory.text(content, "js".equals(type) ? "text/javascript;charset=UTF-8" : "text/css;charset=UTF-8");
	}

}
