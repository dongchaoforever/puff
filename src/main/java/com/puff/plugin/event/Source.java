package com.puff.plugin.event;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Source {

	private final Map map;

	public Source() {
		this.map = new HashMap();
	}

	public <T> Source add(String key, T value) {
		map.put(key, value);
		return this;
	}

	public <T> T get(String key) {
		return (T) map.get(key);
	}

	@Override
	public String toString() {
		return "Source [" + map + "]";
	}

}
