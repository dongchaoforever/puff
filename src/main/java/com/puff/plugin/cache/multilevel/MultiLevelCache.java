package com.puff.plugin.cache.multilevel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.plugin.cache.Cache;
import com.puff.plugin.cache.CacheManager;
import com.puff.plugin.cache.CallBack;
import com.puff.plugin.cache.sync.CacheSync;
import com.puff.plugin.msg.Command;

import net.sf.ehcache.CacheException;

/**
 * 多级缓存
 * @author dongchao
 *
 */
public class MultiLevelCache implements Cache {
	private final static Log log = LogFactory.get(MultiLevelCache.class);

	private String cacheName;
	private Cache firstCache;
	private CacheManager secondCacheManager;
	private CacheSync cacheSync;

	public MultiLevelCache(String cacheName, Cache firstCache, CacheManager cacheManager) {
		super();
		this.cacheName = cacheName;
		this.firstCache = firstCache;
		this.secondCacheManager = cacheManager;
	}

	public MultiLevelCache setCacheSync(CacheSync cacheSync) {
		this.cacheSync = cacheSync;
		return this;
	}

	@Override
	public boolean exist(String key) {
		boolean exist = firstCache.exist(key);
		if (!exist) {
			Cache secondCache = secondCacheManager.buildCache(cacheName);
			exist = secondCache.exist(key);
		}
		return exist;
	}

	@Override
	public <T extends Serializable> T get(String key) {
		T result = firstCache.get(key);
		if (result == null) {
			Cache secondCache = secondCacheManager.buildCache(cacheName);
			result = secondCache.get(key);
			if (result != null) {
				int ttl = secondCache.ttl(key);
				if (ttl > 0) {
					firstCache.put(key, result, ttl);
					sendPutCmd(key);
				} else {
					firstCache.put(key, result);
				}
			}
		}
		return result;
	}

	@Override
	public <T extends Serializable> List<T> get(List<String> keys) {
		if (keys != null) {
			List<T> list = new ArrayList<T>(keys.size());
			for (String key : keys) {
				T value = get(key);
				list.add(value);
			}
			return list;
		}
		return null;
	}

	@Override
	public <T extends Serializable> T get(String key, CallBack<T> callBack) {
		T result = null;
		boolean cacheError = false;
		try {
			result = get(key);
		} catch (Exception e) {
			cacheError = true;
		}
		if (result == null) {
			result = callBack.call(key);
			if (result != null && !cacheError) {
				try {
					firstCache.put(key, result);
					Cache secondCache = secondCacheManager.buildCache(cacheName);
					secondCache.put(key, result);
				} catch (Exception e) {

				}
			}
		}
		return result;
	}

	@Override
	public <T extends Serializable> T get(String key, CallBack<T> callBack, int exp) {
		T result = null;
		boolean cacheError = false;
		try {
			result = get(key);
		} catch (Exception e) {
			cacheError = true;
		}
		if (result == null) {
			result = callBack.call(key);
			if (result != null && !cacheError) {
				try {
					firstCache.put(key, result, exp);
					Cache secondCache = secondCacheManager.buildCache(cacheName);
					secondCache.put(key, result, exp);
				} catch (Exception e) {

				}
			}
		}
		return result;
	}

	@Override
	public void put(String key, Object value) {
		boolean exist = firstCache.exist(key);
		firstCache.put(key, value);
		// level2挂掉，如果需要同步则抛异常，否则忽略
		try {
			Cache secondCache = secondCacheManager.buildCache(cacheName);
			secondCache.put(key, value);
		} catch (Exception e) {
			if (cacheSync != null) {
				throw new CacheException("put level2 cache error ", e);
			} else {
				log.warn("put level2 cache error ");
			}
		}
		// 一级缓存存在，通知群组内服务器清除一级缓存
		if (exist) {
			sendRemoveCmd(key);
		}
	}

	@Override
	public void put(String key, Object value, int expire) {
		firstCache.put(key, value, expire);
		// level2挂掉，如果需要同步则抛异常，否则忽略
		try {
			Cache secondCache = secondCacheManager.buildCache(cacheName);
			secondCache.put(key, value, expire);
		} catch (Exception e) {
			if (cacheSync != null) {
				throw new CacheException("put level2 cache error ", e);
			} else {
				log.warn("put level2 cache error ");
			}
		}
		// 通知群组内服务器更新1级缓存，避免集群内服务器缓存生命周期不一致导致不可预知的bug
		sendPutCmd(key);
	}

	@Override
	public void update(String key, Object value) {
		firstCache.update(key, value);
		// level2挂掉，如果需要同步则抛异常，否则忽略
		try {
			Cache secondCache = secondCacheManager.buildCache(cacheName);
			secondCache.update(key, value);
		} catch (Exception e) {
			if (cacheSync != null) {
				throw new CacheException("put level2 cache error ", e);
			} else {
				log.warn("put level2 cache error ");
			}
		}
		sendPutCmd(key);
	}

	@Override
	public int ttl(String key) {
		int first = firstCache.ttl(key);
		Cache secondCache = secondCacheManager.buildCache(cacheName);
		int second = secondCache.ttl(key);
		return first > second ? second : first;
	}

	@Override
	public List<String> keys() {
		List<String> keys = firstCache.keys();
		if (keys == null || keys.size() == 0) {
			Cache secondCache = secondCacheManager.buildCache(cacheName);
			keys = secondCache.keys();
		}
		return keys;
	}

	@Override
	public void remove(String key) {
		firstCache.remove(key);
		Cache secondCache = secondCacheManager.buildCache(cacheName);
		secondCache.remove(key);
		sendRemoveCmd(key);
	}

	@Override
	public void remove(List<String> keys) {
		firstCache.remove(keys);
		Cache secondCache = secondCacheManager.buildCache(cacheName);
		secondCache.remove(keys);
		sendRemoveCmd(keys);
	}

	@Override
	public void clear() {
		firstCache.clear();
		Cache secondCache = secondCacheManager.buildCache(cacheName);
		secondCache.clear();
		sendClearCmd();
	}

	/**
	 * 发送新增缓存命令
	 * @param key
	 */
	private void sendPutCmd(String key) {
		if (cacheSync != null) {
			Command c = Command.put(cacheName, key);
			cacheSync.sendCommand(c);
		}
	}

	/**
	 * 发送移除缓存命令
	 * @param key
	 */
	private void sendRemoveCmd(Object key) {
		if (cacheSync != null) {
			Command c = Command.remove(cacheName, key);
			cacheSync.sendCommand(c);
		}
	}

	/**
	 * 发送清空缓存命令
	 */
	private void sendClearCmd() {
		if (cacheSync != null) {
			Command c = Command.clear(cacheName);
			cacheSync.sendCommand(c);
		}
	}

}
