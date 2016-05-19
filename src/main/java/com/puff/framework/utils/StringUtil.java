package com.puff.framework.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StringUtil {

	/**
	 * 首字母变小写
	 */
	public static String firstCharToLowerCase(String str) {
		char firstChar = str.charAt(0);
		if (firstChar >= 'A' && firstChar <= 'Z') {
			char[] arr = str.toCharArray();
			arr[0] += ('a' - 'A');
			return new String(arr);
		}
		return str;
	}

	/**
	 * 首字母变大写
	 */
	public static String firstCharToUpperCase(String str) {
		char firstChar = str.charAt(0);
		if (firstChar >= 'a' && firstChar <= 'z') {
			char[] arr = str.toCharArray();
			arr[0] -= ('a' - 'A');
			return new String(arr);
		}
		return str;
	}

	public static boolean empty(final String str) {
		return (str == null) || (str.length() == 0);
	}

	public static boolean notEmpty(final String str) {
		return !empty(str);
	}

	public static boolean blank(final String str) {
		int strLen;
		if ((str == null) || ((strLen = str.length()) == 0))
			return true;
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean notBlank(final String str) {
		return !blank(str);
	}

	public static boolean allEmpty(String... strings) {
		if (strings == null) {
			return true;
		}
		for (String str : strings) {
			if (notEmpty(str)) {
				return false;
			}
		}
		return true;
	}

	public static boolean hasEmpty(String... strings) {
		if (strings == null) {
			return true;
		}
		for (String str : strings) {
			if (empty(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * checkValue为 null 或者为 "" 时返回 defaultValue
	 */
	public static String empty(String checkValue, String defaultValue) {
		return empty(checkValue) ? defaultValue : checkValue;
	}

	/**
	 * 字符串不为 null 而且不为 "" 并且等于other
	 */
	public static boolean notEmptyAndEqOther(String str, String other) {
		if (empty(str)) {
			return false;
		}
		return str.equals(other);
	}

	/**
	 * 字符串不为 null 而且不为 "" 并且不等于other
	 */
	public static boolean notEmptyAndNotEqOther(String str, String... other) {
		if (empty(str)) {
			return false;
		}
		for (int i = 0; i < other.length; i++) {
			if (str.equals(other[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 字符串不等于other
	 */
	public static boolean notEqOther(String str, String... other) {
		for (int i = 0; i < other.length; i++) {
			if (other[i].equals(str)) {
				return false;
			}
		}
		return true;
	}

	public static boolean notEmpty(String... strings) {
		if (strings == null) {
			return false;
		}
		for (String str : strings) {
			if (str == null || "".equals(str.trim())) {
				return false;
			}
		}
		return true;
	}

	public static boolean equals(String value, String equals) {
		if (allEmpty(value, equals)) {
			return true;
		}
		return value.equals(equals);
	}

	public static boolean notEquals(String value, String equals) {
		return !equals(value, equals);
	}

	public static String[] split(String content, String separatorChars) {
		return splitWorker(content, separatorChars, -1, false);
	}

	public static String[] split(String str, String separatorChars, int max) {
		return splitWorker(str, separatorChars, max, false);
	}

	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
		// Performance tuned for 2.0 (JDK1.4)
		// Direct code is quicker than StringTokenizer.
		// Also, StringTokenizer uses isSpace() not isWhitespace()

		if (str == null) {
			return null;
		}
		int len = str.length();
		if (len == 0) {
			return EMPTY_STRING_ARRAY;
		}
		List<String> list = new ArrayList<String>();
		int sizePlus1 = 1;
		int i = 0, start = 0;
		boolean match = false;
		boolean lastMatch = false;
		if (separatorChars == null) {
			// Null separator means use whitespace
			while (i < len) {
				if (Character.isWhitespace(str.charAt(i))) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		} else if (separatorChars.length() == 1) {
			// Optimise 1 character case
			char sep = separatorChars.charAt(0);
			while (i < len) {
				if (str.charAt(i) == sep) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		} else {
			// standard case
			while (i < len) {
				if (separatorChars.indexOf(str.charAt(i)) >= 0) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		}
		if (match || (preserveAllTokens && lastMatch)) {
			list.add(str.substring(start, i));
		}
		return (String[]) list.toArray(EMPTY_STRING_ARRAY);
	}

	public static String escapeXML(String str) {
		if (str == null)
			return "";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); ++i) {
			char c = str.charAt(i);
			switch (c) {
			case '\u00FF':
			case '\u0024':
				break;
			case '&':
				sb.append("&amp;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '\"':
				sb.append("&quot;");
				break;
			case '\'':
				sb.append("&apos;");
				break;
			default:
				if (c >= '\u0000' && c <= '\u001F')
					break;
				if (c >= '\uE000' && c <= '\uF8FF')
					break;
				if (c >= '\uFFF0' && c <= '\uFFFF')
					break;
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * 将字符串中特定模式的字符转换成map中对应的值
	 *
	 * @param s
	 *            需要转换的字符串
	 * @param map
	 *            转换所需的键值对集合
	 * @return 转换后的字符串
	 */
	public static String replace(String s, Map<String, Object> map) {
		StringBuilder ret = new StringBuilder((int) (s.length() * 1.5));
		int cursor = 0;
		for (int start, end; (start = s.indexOf("${", cursor)) != -1 && (end = s.indexOf("}", start)) != -1;) {
			ret.append(s.substring(cursor, start)).append(map.get(s.substring(start + 2, end)));
			cursor = end + 1;
		}
		ret.append(s.substring(cursor, s.length()));
		return ret.toString();
	}

	public static String replace(String s, Object... objs) {
		if (objs == null || objs.length == 0)
			return s;
		if (s.indexOf("{}") == -1)
			return s;
		StringBuilder ret = new StringBuilder((int) (s.length() * 1.5));
		int cursor = 0;
		int index = 0;
		for (int start; (start = s.indexOf("{}", cursor)) != -1;) {
			ret.append(s.substring(cursor, start));
			if (index < objs.length)
				ret.append(objs[index]);
			else
				ret.append("{}");
			cursor = start + 2;
			index++;
		}
		ret.append(s.substring(cursor, s.length()));
		return ret.toString();
	}

	/**
	 * 字符串格式化工具,参数必须以{0}之类的样式标示出来.大括号中的数字从0开始。
	 * 
	 * @param source
	 *            源字符串
	 * @param params
	 *            需要替换的参数列表,写入时会调用每个参数的toString().
	 * @return 替换完成的字符串。如果原始字符串为空或者参数为空那么将直接返回原始字符串。
	 */
	public static String replaceArgs(String source, Object... params) {
		if (params == null || params.length == 0 || source == null || source.isEmpty()) {
			return source;
		}
		StringBuilder buff = new StringBuilder(source);
		StringBuilder temp = new StringBuilder();
		int startIndex = 0;
		int endIndex = 0;
		String param = null;
		for (int count = 0; count < params.length; count++) {
			if (params[count] == null) {
				param = null;
			} else {
				param = params[count].toString();
			}

			temp.delete(0, temp.length());
			temp.append("{");
			temp.append(count);
			temp.append("}");
			while (true) {
				startIndex = buff.indexOf(temp.toString(), endIndex);
				if (startIndex == -1) {
					break;
				}
				endIndex = startIndex + temp.length();

				buff.replace(startIndex, endIndex, param == null ? "" : param);
			}
			startIndex = 0;
			endIndex = 0;
		}
		return buff.toString();
	}

	public static String substringBefore(final String s, final String separator) {
		if (empty(s) || separator == null) {
			return s;
		}
		if (separator.isEmpty()) {
			return "";
		}
		final int pos = s.indexOf(separator);
		if (pos < 0) {
			return s;
		}
		return s.substring(0, pos);
	}

	public static String substringBetween(final String str, final String open, final String close) {
		if (str == null || open == null || close == null) {
			return null;
		}
		final int start = str.indexOf(open);
		if (start != -1) {
			final int end = str.indexOf(close, start + open.length());
			if (end != -1) {
				return str.substring(start + open.length(), end);
			}
		}
		return null;
	}

	public static String substringAfter(final String str, final String separator) {
		if (empty(str)) {
			return str;
		}
		if (separator == null) {
			return "";
		}
		final int pos = str.indexOf(separator);
		if (pos == -1) {
			return "";
		}
		return str.substring(pos + separator.length());
	}

}
