package com.puff.web.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.puff.core.Puff;
import com.puff.exception.ViewException;
import com.puff.jdbc.core.PageRecord;
import com.puff.jdbc.core.Record;

public class JspView extends View {
	/**
	 * 
	 */
	private static final String filePath = Puff.getFileViewPath();
	private String view;
	private boolean supportRecord;

	public JspView(String view) {
		this.view = filePath + view;
	}

	public JspView(String view, boolean supportRecord) {
		this(view);
		this.supportRecord = supportRecord;
	}

	public JspView(String view, String name, Object value) {
		this(view);
		super.put(name, value);
	}

	public boolean isSupportRecord() {
		return supportRecord;
	}

	public void setSupportRecord(boolean supportRecord) {
		this.supportRecord = supportRecord;
	}

	@Override
	public final void view() {
		try {
			if (supportRecord) {
				handleRecord();
			}
			request.getRequestDispatcher(view).forward(request, response);
		} catch (Exception e) {
			throw new ViewException(e);
		}
	}

	private static int DEPTH = 8;

	@SuppressWarnings("unchecked")
	private void handleRecord() {
		for (Enumeration<String> attrs = request.getAttributeNames(); attrs.hasMoreElements();) {
			String key = attrs.nextElement();
			if (key.startsWith("org.apache.")) {
				continue;
			}
			Object value = request.getAttribute(key);
			request.setAttribute(key, handleObject(value, DEPTH));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object handleObject(Object value, int depth) {
		if (value == null || (depth--) <= 0) {
			return value;
		}
		if (value instanceof List) {
			return handleList((List) value, depth);
		} else if (value instanceof Record) {
			return handleMap(((Record) value).getRecord(), depth);
		} else if (value instanceof PageRecord) {
			return handlePage((PageRecord) value, depth);
		} else if (value instanceof Map) {
			return handleMap((Map) value, depth);
		} else if (value instanceof Object[]) {
			return handleArray((Object[]) value, depth);
		} else {
			return value;
		}
	}

	private Object handlePage(PageRecord<Record> record, int depth) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", handleList(record.getDataList(), depth));
		map.put("page", record.getPage());
		map.put("totalPage", record.getTotalPage());
		map.put("totalCount", record.getTotalCount());
		return map;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object handleArray(Object[] array, int depth) {
		if (array == null || array.length == 0) {
			return new ArrayList(0);
		}

		List result = new ArrayList(array.length);
		for (int i = 0; i < array.length; i++) {
			result.add(handleObject(array[i], depth));
		}
		return result;
	}

	private Object handleMap(Map<String, Object> map, int depth) {
		if (map == null || map.size() == 0) {
			return map;
		}
		Map<String, Object> result = map;
		for (Map.Entry<String, Object> e : result.entrySet()) {
			String key = e.getKey();
			Object value = e.getValue();
			value = handleObject(value, depth);
			result.put(key, value);
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection handleList(Collection list, int depth) {
		if (list == null || list.size() == 0) {
			return list;
		}
		List result = new ArrayList(list.size());
		for (Object value : list) {
			result.add(handleObject(value, depth));
		}
		return result;
	}

}
