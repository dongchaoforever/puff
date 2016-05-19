package com.puff.web.mvc.own;

import com.puff.core.Puff;
import com.puff.framework.utils.FileUtil;
import com.puff.framework.utils.PathUtil;
import com.puff.framework.utils.Security;
import com.puff.framework.utils.StringUtil;
import com.puff.web.mvc.PuffContext;
import com.puff.web.view.View;
import com.puff.web.view.ViewFactory;

public enum MonitorInfo {
	;
	private static String userName = "Puff.Monitor";

	private static String password = "Puff.Monitor";

	public static String getUserName() {
		return userName;
	}

	public static void setUserName(String userName) {
		MonitorInfo.userName = userName;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		MonitorInfo.password = password;
	}

	public static boolean isLogin() {
		String value = PuffContext.findCookieValue("_Monitor_User");
		return StringUtil.notEmptyAndEqOther(value, Security.blowFish(userName + password));
	}

	public static View login(String target) {
		String loginContent = FileUtil.toString("UTF-8", PathUtil.fromJar("resource/owm/login"));
		loginContent = loginContent.replace("${ctx}", Puff.getContextPath());
		loginContent = loginContent.replace("${target}", target);
		return ViewFactory.html(loginContent);
	}

}
