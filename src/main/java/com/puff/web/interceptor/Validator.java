package com.puff.web.interceptor;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.puff.framework.utils.StringUtil;
import com.puff.web.mvc.DispatcherExecutor;
import com.puff.web.mvc.PuffContext;
import com.puff.web.view.View;

public abstract class Validator implements Interceptor {

	private class ValidateMap<K, V> extends HashMap<K, V> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4471699664524908257L;

		@Override
		public V put(K key, V value) {
			if (!super.containsKey(key)) {
				return super.put(key, value);
			}
			return value;
		}
	}

	private DispatcherExecutor excutor;
	private boolean error = false;
	private boolean first = true;
	private boolean isShort = false;
	private Map<String, String> msg;
	private Map<String, Map<String, Object>> returnMap = new HashMap<String, Map<String, Object>>();

	private static final String EMAIL_ADDRESS_PATTERN = "\\b(^['_A-Za-z0-9-]+(\\.['_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";
	private static final String MOBILE_ADDRESS_PATTERN = "^(((13[0-9]{1})|(14[0-9]{1})|(15[0-9]{1})|(17[0-9]{1})|(18[0-9]{1}))+\\d{8})$";

	public final void intercept(DispatcherExecutor excutor) {
		Validator validator = null;
		try {
			validator = getClass().newInstance();
			validator.excutor = excutor;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		try {
			validator.isShort = validator.isShort();
			validator.validate(excutor);
		} catch (Exception e) {
		}
		if (validator.error) {
			View view = validator.handleError(excutor);
			if (view == null) {
				return;
			}
			view.put("errMsg", validator.msg);
			if (!PuffContext.ajax()) {
				keepParam(view);
			}
			excutor.setResult(view);
		} else {
			excutor.execute();
		}
	}

	protected void keepParam(String paramName) {
		PuffContext.setAttribute(paramName, PuffContext.getParameter(paramName));
	}

	private void keepParam(View view) {
		Map<String, String[]> map = PuffContext.getParameterMap();
		for (Entry<String, String[]> e : map.entrySet()) {
			String[] values = e.getValue();
			if (values != null) {
				String key = e.getKey();
				String[] array = key.split("\\.");
				if (array.length == 2) {
					String prefix = array[0];
					Map<String, Object> retMap = getReturnMap(prefix);
					retMap.put(array[1], values.length == 1 ? values[0] : values);
					view.put(prefix, retMap);
				} else {
					view.put(key, values.length == 1 ? values[0] : values);
				}
			}
		}
	}

	protected abstract boolean isShort();

	protected abstract void validate(DispatcherExecutor excutor);

	protected abstract View handleError(DispatcherExecutor excutor);

	private final Map<String, Object> getReturnMap(String name) {
		Map<String, Object> map = returnMap.get(name);
		if (map == null) {
			map = new HashMap<String, Object>();
			returnMap.put(name, map);
		}
		return map;
	}

	protected void addError(String errKey, String errMsg) {
		error = true;
		if (msg == null) {
			msg = new ValidateMap<String, String>();
		}
		if (!isShort) {
			msg.put(errKey, errMsg);
		} else if (first) {
			msg.put(errKey, errMsg);
			first = false;
		}
	}

	protected String getExecutorKey() {
		return excutor.getExecutorKey();
	}

	protected void required(String field, String errMsg) {
		required(field, field, errMsg);
	}

	protected void required(String field, String errKey, String errMsg) {
		String value = PuffContext.getParameter(field);
		if (StringUtil.empty(value)) {
			addError(errKey, errMsg);
		}
	}

	protected void integer(String field, int min, int max, String errMsg) {
		integer(field, min, max, field, errMsg);
	}

	protected void integer(String field, int min, int max, String errKey, String errMsg) {
		try {
			int value = PuffContext.getIntParam(field);
			if (value < min || value > max) {
				addError(errKey, errMsg);
			}
		} catch (Exception e) {
			addError(errKey, errMsg);
		}
	}

	private static final String datePattern = "yyyy-MM-dd";
	private static final String TEL_ADDRESS_PATTERN = "^\\d{3,4}-?\\d{7,9}$";

	protected void date(String field, Date min, Date max, String errMsg) {
		date(field, field, field, errMsg);
	}

	protected void date(String field, Date min, Date max, String errKey, String errMsg) {
		try {
			String value = PuffContext.getParameter(field);
			Date temp = new SimpleDateFormat(datePattern).parse(value);
			if (temp.before(min) || temp.after(max)) {
				addError(errKey, errMsg);
			}
		} catch (Exception e) {
			addError(errKey, errMsg);
		}
	}

	protected void date(String field, String errMsg) {
		date(field, datePattern, field, errMsg);
	}

	protected void date(String field, String errKey, String errMsg) {
		date(field, datePattern, errKey, errMsg);
	}

	protected void date(String field, String datePattern, String errKey, String errMsg) {
		try {
			String value = PuffContext.getParameter(field);
			new SimpleDateFormat(datePattern).parse(value);
		} catch (ParseException e) {
			addError(errKey, errMsg);
		}
	}

	protected void date(String field, String datePattern, String min, String max, String errKey, String errMsg) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
			date(field, sdf.parse(min), sdf.parse(max), errKey, errMsg);
		} catch (ParseException e) {
			addError(errKey, errMsg);
		}
	}

	protected void date(String field, String min, String max, String errKey, String errMsg) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
			date(field, sdf.parse(min), sdf.parse(max), errKey, errMsg);
		} catch (ParseException e) {
			addError(errKey, errMsg);
		}
	}

	protected void equalFiled(String field_1, String field_2, String errKey, String errMsg) {
		String value_1 = PuffContext.getParameter(field_1);
		String value_2 = PuffContext.getParameter(field_2);
		equalString(value_1, value_2, errKey, errMsg);
	}

	protected void equalString(String s1, String s2, String errKey, String errMsg) {
		if (s1 == null || s2 == null || (!s1.equals(s2))) {
			addError(errKey, errMsg);
		}
	}

	protected void equalInteger(Integer i1, Integer i2, String errKey, String errMsg) {
		if (i1 == null || i2 == null || (i1.intValue() != i2.intValue())) {
			addError(errKey, errMsg);
		}
	}

	protected void email(String field, String errMsg) {
		validateRegex(field, EMAIL_ADDRESS_PATTERN, false, field, errMsg);
	}

	protected void email(String field, String errKey, String errMsg) {
		validateRegex(field, EMAIL_ADDRESS_PATTERN, false, errKey, errMsg);
	}

	protected void mobile(String field, String errMsg) {
		validateRegex(field, MOBILE_ADDRESS_PATTERN, false, errMsg);
	}

	protected void mobile(String field, String errKey, String errMsg) {
		validateRegex(field, MOBILE_ADDRESS_PATTERN, false, errKey, errMsg);
	}

	protected void tel(String field, String errMsg) {
		validateRegex(field, TEL_ADDRESS_PATTERN, false, errMsg);
	}

	protected void tel(String field, String errKey, String errMsg) {
		validateRegex(field, TEL_ADDRESS_PATTERN, false, errKey, errMsg);
	}

	protected void url(String field, String errMsg) {
		url(field, field, errMsg);
	}

	protected void url(String field, String errKey, String errMsg) {
		try {
			String value = PuffContext.getParameter(field);
			if (value.startsWith("https://")) {
				value = "http://" + value.substring(8);
			}
			new URL(value);
		} catch (MalformedURLException e) {
			addError(errKey, errMsg);
		}
	}

	protected void validateRegex(String field, String regExpression, boolean isCaseSensitive, String errKey, String errMsg) {
		Pattern pattern = isCaseSensitive ? Pattern.compile(regExpression) : Pattern.compile(regExpression, Pattern.CASE_INSENSITIVE);
		String value = PuffContext.getParameter(field);
		if (value == null) {
			addError(errKey, errMsg);
			return;
		}
		Matcher matcher = pattern.matcher(value);
		if (!matcher.matches()) {
			addError(errKey, errMsg);
		}
	}

	protected void validateRegex(String field, String regExpression, boolean isCaseSensitive, String errMsg) {
		validateRegex(field, regExpression, isCaseSensitive, field, errMsg);
	}

	protected void validateRegex(String field, String regExpression, String errMsg) {
		validateRegex(field, regExpression, true, field, errMsg);
	}

	protected void validateRegex(String field, String regExpression, String errKey, String errMsg) {
		validateRegex(field, regExpression, true, errKey, errMsg);
	}

	protected void string(String field, boolean notEmpty, int minLen, int maxLen, String errMsg) {
		string(field, notEmpty, minLen, maxLen, field, errMsg);
	}

	protected void string(String field, boolean notEmpty, int minLen, int maxLen, String errKey, String errMsg) {
		String value = PuffContext.getParameter(field);
		if (value == null || value.length() < minLen || value.length() > maxLen) {
			addError(errKey, errMsg);
		} else if (notEmpty && "".equals(value.trim())) {
			addError(errKey, errMsg);
		}
	}

	protected void string(String field, int minLen, int maxLen, String errMsg) {
		string(field, true, minLen, maxLen, field, errMsg);
	}

	protected void string(String field, int minLen, int maxLen, String errKey, String errMsg) {
		string(field, true, minLen, maxLen, errKey, errMsg);
	}

	protected void custom(CustomValidate validate, String field, String errMsg) {
		custom(validate, field, field, errMsg);
	}

	protected void custom(CustomValidate validate, String field, String errKey, String errMsg) {
		try {
			if (validate.error(PuffContext.getParameter(field))) {
				addError(errKey, errMsg);
			}
		} catch (Exception e) {
			addError(errKey, errMsg);
		}
	}

	protected boolean isToken(String errKey, String errMsg) {
		if (PuffContext.isRepeatSubmit()) {
			addError(errKey, errMsg);
			return true;
		} else {
			return false;
		}
	}

	public interface CustomValidate {
		public boolean error(String value);
	}
}
