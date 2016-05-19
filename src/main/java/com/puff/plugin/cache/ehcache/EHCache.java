/**
 * Copyright (c) 2011-2015, 董超 (dongchaoforever@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.puff.plugin.cache.ehcache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.plugin.cache.Cache;
import com.puff.plugin.cache.CallBack;

import net.sf.ehcache.Element;

/**
 * EHCache实现
 * @author dongchao
 *
 */
public class EHCache implements Cache {
	private final static Log log = LogFactory.get(EHCache.class);

	private net.sf.ehcache.Cache cache;

	public EHCache(net.sf.ehcache.Cache cache) {
		this.cache = cache;
	}

	@Override
	public boolean exist(String key) {
		Element element = cache.get(key);
		return element != null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T get(String key) {
		Element element = cache.get(key);
		Object value = element != null ? element.getObjectValue() : null;
		return (T) value;
	}

	@Override
	public <T extends Serializable> List<T> get(List<String> keys) {
		if (keys != null) {
			int size = keys.size();
			List<T> list = new ArrayList<T>(size);
			for (int i = 0; i < size; i++) {
				T object = get(keys.get(i));
				if (object != null) {
					list.add(object);
				}
			}
			return list;
		}
		return Collections.emptyList();
	}

	/**
	 * 有callBack时，忽略从缓存中获取时候的出错
	 */
	@Override
	public <T extends Serializable> T get(String key, CallBack<T> callBack) {
		boolean cacheError = false;
		T result = null;
		try {
			result = get(key);
		} catch (Exception e) {
			cacheError = true;
			log.error("get from ehcache error", e);
		}
		if (result == null) {
			result = callBack.call(key);
			if (result != null && !cacheError) {
				put(key, (Serializable) result);
			}
		}
		return result;
	}

	@Override
	public <T extends Serializable> T get(String key, CallBack<T> callBack, int expire) {
		boolean cacheError = false;
		T result = null;
		try {
			result = get(key);
		} catch (Exception e) {
			cacheError = true;
			log.error("get from ehcache error", e);
		}
		if (result == null) {
			result = callBack.call(key);
			if (result != null && !cacheError) {
				put(key, (Serializable) result, expire);
			}
		}
		return result;
	}

	@Override
	public void put(String key, Object value) {
		Element element = new Element(key, value);
		cache.put(element);
	}

	@Override
	public void put(String key, Object value, int expire) {
		if (value != null) {
			Element element = new Element(key, value);
			if (expire > 0) {
				element.setTimeToLive(expire);
				element.setEternal(false);
			}
			cache.put(element);
		} else {
			cache.remove(key);
		}
	}

	@Override
	public void update(String key, Object value) {
		int ttl = ttl(key);
		if (ttl > 0) {
			put(key, value, ttl);
		} else {
			put(key, value);
		}
	}

	@Override
	public int ttl(String key) {
		Element element = cache.get(key);
		if (element == null) {
			return 0;
		}
		long time = element.getExpirationTime() - System.currentTimeMillis();
		return (int) (time / 1000);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> keys() {
		return cache.getKeys();
	}

	@Override
	public void remove(String key) {
		cache.remove(key);
	}

	@Override
	public void remove(List<String> keys) {
		cache.removeAll(keys);
	}

	@Override
	public void clear() {
		cache.removeAll();
	}

}
