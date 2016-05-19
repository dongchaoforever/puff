package com.puff.web.mvc.own;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.puff.core.Puff;
import com.puff.framework.annotation.BeanScope;
import com.puff.framework.annotation.Controller;
import com.puff.framework.utils.DateTime;
import com.puff.framework.utils.FileUtil;
import com.puff.framework.utils.PathUtil;
import com.puff.framework.utils.StringUtil;
import com.puff.web.mvc.PuffContext;
import com.puff.web.view.View;
import com.puff.web.view.ViewFactory;

@Controller(value = "/puff/owm/monitor/server/status", scope = BeanScope.SINGLETON, report = false)
public class ServerStatusController {
	private static String content;
	static {
		content = FileUtil.toString("UTF-8", PathUtil.fromJar("resource/owm/server-status-info"));
	}

	public View index() {
		if (!MonitorInfo.isLogin()) {
			return MonitorInfo.login("server-status");
		} else {
			Properties properties = System.getProperties();
			Map<String, Object> map = new HashMap<String, Object>();
			for (Object key : properties.keySet()) {
				map.put(key + "", properties.getProperty(key + ""));
			}
			map.put("server.availableProcessors", Runtime.getRuntime().availableProcessors());
			map.put("server.totalMemory", convertFileSize(Runtime.getRuntime().totalMemory()));
			map.put("server.freeMemory", convertFileSize(Runtime.getRuntime().freeMemory()));
			map.put("server.maxMemory", convertFileSize(Runtime.getRuntime().maxMemory()));
			String serverStartTime = Puff.serverStartTime();
			map.put("server.starttime", serverStartTime);
			map.put("server.nowtime", DateTime.currentTimeStamp());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long time = 0;
			try {
				Date date = sdf.parse(serverStartTime);
				time = System.currentTimeMillis() - date.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			map.put("server.runtime", DateTime.timeSpan(time));
			map.put("puff.version", Puff.version);
			map.put("server.serverInfo", Puff.getServletContext().getServerInfo());
			map.put("server.serverPort", PuffContext.getRequest().getServerPort());

			String classPath = (String) map.get("java.class.path");
			if (StringUtil.notEmpty(classPath)) {
				String[] arr = classPath.split(":");
				StringBuilder sb = new StringBuilder();
				for (String string : arr) {
					sb.append(string).append("<br>");
				}
				map.put("java.class.path", sb.toString());
			}
			try {
				// 获取计算机名
				String name = InetAddress.getLocalHost().getHostName();
				map.put("server.hostname", name);
				// 获取IP地址
				String ip = InetAddress.getLocalHost().getHostAddress();
				map.put("server.ip", ip);
			} catch (UnknownHostException e) {
			}
			String htmlContent = StringUtil.replace(content, map);
			return ViewFactory.html(htmlContent);
		}
	}

	public String convertFileSize(long size) {
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;
		if (size >= gb) {
			return String.format("%.1f GB", (float) size / gb);
		} else if (size >= mb) {
			float f = (float) size / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (size >= kb) {
			float f = (float) size / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else
			return String.format("%d B", size);
	}
}
