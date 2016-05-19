package com.puff.framework.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class VerifyUtil {

	public static boolean isDigit(String str) {
		if (StringUtil.empty(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher match = pattern.matcher(str);
		return match.matches();
	}

	public static boolean isMoney(String str) {
		if (StringUtil.empty(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
		Matcher match = pattern.matcher(str);
		return match.matches();
	}

	public static boolean isMobile(String str) {
		if (StringUtil.empty(str)) {
			return false;
		}
		Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/**
	 * 电话号码验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isPhone(String str) {
		if (StringUtil.empty(str)) {
			return false;
		}
		Matcher m = null;
		Pattern p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$"); // 验证带区号的
		Pattern p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$"); // 验证没有区号的
		if (str.length() > 9) {
			m = p1.matcher(str);
			return m.matches();
		} else {
			m = p2.matcher(str);
			return m.matches();
		}
	}

	private static final String EMAIL_ADDRESS_PATTERN = "\\b(^['_A-Za-z0-9-]+(\\.['_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";

	public static boolean isEmail(String value) {
		if (StringUtil.empty(value)) {
			return false;
		}
		Pattern pattern = Pattern.compile(EMAIL_ADDRESS_PATTERN, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

	public static boolean isIpAddr(String ip) {
		if (StringUtil.empty(ip)) {
			return false;
		}
		Pattern pattern = Pattern.compile(
				"\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	public static boolean isUrl(String url) {
		boolean flag = false;
		int counts = 0;
		if (StringUtil.empty(url)) {
			return flag;
		}
		while (counts < 2) {
			try {
				HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
				int state = connection.getResponseCode();
				if (state == 200) {
					flag = true;
				}
				break;
			} catch (Exception e) {
				counts++;
			}
		}
		return flag;
	}

	public static boolean isNumeric(String str) {
		if (StringUtil.empty(str)) {
			return false;
		}
		char first = str.charAt(0);
		int i = first == '-' ? 1 : 0;
		for (; i < str.length(); i++) {
			if (!isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isInteger(String str) {
		if (StringUtil.empty(str)) {
			return false;
		}
		char first = str.charAt(0);
		int i = first == '-' ? 1 : 0;
		for (; i < str.length(); i++) {
			if (!isDigit(str.charAt(i))) {
				return false;
			}
		}
		Long t = Long.parseLong(str);
		return t <= Integer.MAX_VALUE && t >= Integer.MIN_VALUE;
	}

	public static boolean isLong(String str) {
		if (StringUtil.empty(str)) {
			return false;
		}
		char first = str.charAt(0);
		char end = str.charAt(str.length() - 1);
		boolean j = end == 'l' || end == 'L';
		int i = first == '-' ? 1 : 0;
		int len = j ? str.length() - 1 : str.length();
		for (; i < len; i++) {
			if (!isDigit(str.charAt(i))) {
				return false;
			}
		}

		if (!j) {
			Long t = Long.parseLong(str);
			return t > Integer.MAX_VALUE || t < Integer.MIN_VALUE;
		} else {
			return true;
		}
	}

	public static boolean isFloat(String str) {
		if (StringUtil.empty(str)) {
			return false;
		}
		char end = str.charAt(str.length() - 1);
		if (!(end == 'f' || end == 'F')) {
			return false;
		}

		int point = 0;
		int i = str.charAt(0) == '-' ? 1 : 0;
		for (; i < str.length() - 1; i++) {
			char c = str.charAt(i);
			if (c == '.') {
				point++;
			} else if (isDigit(c) == false) {
				return false;
			}
		}
		return point == 1 || point == 0;
	}

	public static boolean isDouble(String str) {
		if (StringUtil.empty(str)) {
			return false;
		}

		int point = 0;
		int i = str.charAt(0) == '-' ? 1 : 0;
		for (; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '.') {
				point++;
			} else if (!isDigit(c)) {
				return false;
			}
		}
		return point == 1;
	}

	public static boolean isDigit(char ch) {
		return ch >= '0' && ch <= '9';
	}

}
