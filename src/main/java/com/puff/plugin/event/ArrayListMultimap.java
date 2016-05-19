package com.puff.plugin.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrayListMultimap<K, V> {

	private final Map<K, List<V>> map;

	public ArrayListMultimap() {
		map = new HashMap<K, List<V>>();
	}

	List<V> createlist() {
		return new ArrayList<V>();
	}

	/**
	 * put to ArrayListMultimap
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return boolean
	 */
	public boolean put(K key, V value) {
		List<V> list = map.get(key);
		if (list == null) {
			list = createlist();
			if (list.add(value)) {
				map.put(key, list);
				return true;
			} else {
				throw new AssertionError("New list violated the list spec");
			}
		} else if (list.add(value)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * get List by key
	 * 
	 * @param key
	 *            键
	 * @return List
	 */
	public List<V> get(K key) {
		return map.get(key);
	}

	/**
	 * clear ArrayListMultimap
	 */
	public void clear() {
		map.clear();
	}

}
