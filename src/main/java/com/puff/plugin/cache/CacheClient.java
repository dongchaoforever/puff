package com.puff.plugin.cache;

import java.io.Serializable;
import java.util.List;

/**
 * cache 客户端
 * @author dongchao
 *
 */
public class CacheClient {

	private CacheManager cacheManager;
	private CacheManager firstCacheManager;// 一级缓存
	private CacheManager secondCacheManager;// 二级缓存

	protected CacheClient setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
		return this;
	}

	protected CacheClient setFirstCacheManager(CacheManager cacheManager) {
		this.firstCacheManager = cacheManager;
		return this;
	}

	protected CacheClient setSecondCacheManager(CacheManager cacheManager) {
		this.secondCacheManager = cacheManager;
		return this;
	}

	private static class Inner {
		private static final CacheClient INSTANCE = new CacheClient();
	}

	public final static CacheClient getCacheClient() {
		return Inner.INSTANCE;
	}

	public final static CacheClient getInstance() {
		return Inner.INSTANCE;
	}

	/**
	 * 获取缓存
	 * @param key
	 * @return
	 */
	public <T extends Serializable> T get(String cacheName, String key) {
		Cache cache = cacheManager.buildCache(cacheName);
		return cache.get(key);
	}

	/**
	 * 获取缓存(从一级缓存获取)
	 * @param key
	 * @return
	 */
	public <T extends Serializable> T getFirst(String cacheName, String key) {
		Cache cache = firstCacheManager.buildCache(cacheName);
		return cache.get(key);
	}

	/**
	 * 获取缓存(从二级缓存获取)
	 * @param key
	 * @return
	 */
	public <T extends Serializable> T getSecond(String cacheName, String key) {
		Cache cache = secondCacheManager.buildCache(cacheName);
		return cache.get(key);
	}

	/**
	 * 获取缓存
	 * @param key
	 * @return
	 */
	public <T extends Serializable> List<T> get(String cacheName, List<String> keys) {
		Cache cache = cacheManager.buildCache(cacheName);
		return cache.get(keys);
	}

	/**
	 * 获取缓存(从一级缓存获取)
	 * @param key
	 * @return
	 */
	public <T extends Serializable> List<T> getFirst(String cacheName, List<String> keys) {
		Cache cache = firstCacheManager.buildCache(cacheName);
		return cache.get(keys);
	}

	/**
	 * 获取缓存(从二级缓存获取)
	 * @param key
	 * @return
	 */
	public <T extends Serializable> List<T> getSecond(String cacheName, List<String> keys) {
		Cache cache = secondCacheManager.buildCache(cacheName);
		return cache.get(keys);
	}

	/**
	 * 获取缓存，取不到时调用CallBack接口获取数据
	 * @param key
	 * @param callBack
	 * @return
	 */
	public <T extends Serializable> T get(String cacheName, String key, CallBack<T> callBack) {
		Cache cache = cacheManager.buildCache(cacheName);
		return cache.get(key, callBack);
	}

	/**
	 * 获取缓存(从一级缓存获取)，取不到时调用CallBack接口获取数据
	 * @param key
	 * @param callBack
	 * @return
	 */
	public <T extends Serializable> T getFirst(String cacheName, String key, CallBack<T> callBack) {
		Cache cache = firstCacheManager.buildCache(cacheName);
		return cache.get(key, callBack);
	}

	/**
	 * 获取缓存(从二级缓存获取)，取不到时调用CallBack接口获取数据
	 * @param key
	 * @param callBack
	 * @return
	 */
	public <T extends Serializable> T getSecond(String cacheName, String key, CallBack<T> callBack) {
		Cache cache = secondCacheManager.buildCache(cacheName);
		return cache.get(key, callBack);
	}

	/**
	 * 获取缓存，取不到时调用CallBack接口获取数据 同时设置失效时间
	 * @param key
	 * @param callBack
	 * @return
	 */
	public <T extends Serializable> T get(String cacheName, String key, CallBack<T> callBack, int expire) {
		Cache cache = cacheManager.buildCache(cacheName);
		return cache.get(key, callBack, expire);
	}

	/**
	 * 获取缓存(从一级缓存获取)，取不到时调用CallBack接口获取数据 同时设置失效时间
	 * @param key
	 * @param callBack
	 * @return
	 */
	public <T extends Serializable> T getFirst(String cacheName, String key, CallBack<T> callBack, int expire) {
		Cache cache = firstCacheManager.buildCache(cacheName);
		return cache.get(key, callBack, expire);
	}

	/**
	 * 获取缓存(从二级缓存获取)，取不到时调用CallBack接口获取数据 同时设置失效时间
	 * @param key
	 * @param callBack
	 * @return
	 */
	public <T extends Serializable> T getSecond(String cacheName, String key, CallBack<T> callBack, int expire) {
		Cache cache = secondCacheManager.buildCache(cacheName);
		return cache.get(key, callBack, expire);
	}

	/**
	 * 储存缓存，key存在直接覆盖
	 * @param key
	 * @param value
	 */
	public void put(String cacheName, String key, Object value) {
		Cache cache = cacheManager.buildCache(cacheName);
		cache.put(key, value);
	}

	/**
	 * 储存缓存(直接储存在一级缓存)，key存在直接覆盖
	 * @param key
	 * @param value
	 */
	public void putFirst(String cacheName, String key, Object value) {
		Cache cache = firstCacheManager.buildCache(cacheName);
		cache.put(key, value);
	}

	/**
	 * 储存缓存(直接储存在二级缓存)，key存在直接覆盖
	 * @param key
	 * @param value
	 */
	public void putSecond(String cacheName, String key, Object value) {
		Cache cache = secondCacheManager.buildCache(cacheName);
		cache.put(key, value);
	}

	/**
	 * 储存缓存，key存在直接覆盖，同时设置超时时间
	 * @param key
	 * @param value
	 * @param expire
	 */
	public void put(String cacheName, String key, Object value, int expire) {
		Cache cache = cacheManager.buildCache(cacheName);
		cache.put(key, value, expire);
	}

	/**
	 * 储存缓存(直接储存在一级缓存)，key存在直接覆盖，同时设置超时时间
	 * @param key
	 * @param value
	 * @param expire
	 */
	public void putFirst(String cacheName, String key, Object value, int expire) {
		Cache cache = firstCacheManager.buildCache(cacheName);
		cache.put(key, value, expire);
	}

	/**
	 * 储存缓存(直接储存在二级缓存)，key存在直接覆盖，同时设置超时时间
	 * @param key
	 * @param value
	 * @param expire
	 */
	public void putSecond(String cacheName, String key, Object value, int expire) {
		Cache cache = secondCacheManager.buildCache(cacheName);
		cache.put(key, value, expire);
	}

	/**
	 * 更新缓存，保留缓存超时时间 比如a有效期10分钟 5分钟后更新a a的超时时间还是5分钟
	 * @param key
	 * @param value
	 */
	public void update(String cacheName, String key, Object value) {
		Cache cache = cacheManager.buildCache(cacheName);
		cache.update(key, value);
	}

	/**
	 * 更新缓存(直接操作一级缓存)，保留缓存超时时间 比如a有效期10分钟 5分钟后更新a a的超时时间还是5分钟
	 * @param key
	 * @param value
	 */
	public void updateFirst(String cacheName, String key, Object value) {
		Cache cache = firstCacheManager.buildCache(cacheName);
		cache.update(key, value);
	}

	/**
	 * 更新缓存(直接操作二级缓存)，保留缓存超时时间 比如a有效期10分钟 5分钟后更新a a的超时时间还是5分钟
	 * @param key
	 * @param value
	 */
	public void updateSecond(String cacheName, String key, Object value) {
		Cache cache = secondCacheManager.buildCache(cacheName);
		cache.update(key, value);
	}

	/**
	 * 获取缓存有效时间
	 * @param key
	 * @return
	 */
	public int ttl(String cacheName, String key) {
		Cache cache = cacheManager.buildCache(cacheName);
		return cache.ttl(key);
	}

	/**
	 * 获取缓存有效时间(直接操作一级缓存)
	 * @param key
	 * @return
	 */
	public int ttlFirst(String cacheName, String key) {
		Cache cache = firstCacheManager.buildCache(cacheName);
		return cache.ttl(key);
	}

	/**
	 * 获取缓存有效时间(直接操作二级缓存)
	 * @param key
	 * @return
	 */
	public int ttlSecond(String cacheName, String key) {
		Cache cache = secondCacheManager.buildCache(cacheName);
		return cache.ttl(key);
	}

	/**
	 * 获取 group所有key
	 * @return
	 */
	public List<String> keys(String cacheName) {
		Cache cache = cacheManager.buildCache(cacheName);
		return cache.keys();
	}

	/**
	 * 获取 group所有key(直接操作一级缓存)
	 * @return
	 */
	public List<String> keysFirst(String cacheName) {
		Cache cache = firstCacheManager.buildCache(cacheName);
		return cache.keys();
	}

	/**
	 * 获取 group所有key(直接操作二级缓存)
	 * @return
	 */
	public List<String> keysSecond(String cacheName) {
		Cache cache = secondCacheManager.buildCache(cacheName);
		return cache.keys();
	}

	/**
	 * 移除缓存
	 * @param key
	 */
	public void remove(String cacheName, String key) {
		Cache cache = cacheManager.buildCache(cacheName);
		cache.remove(key);
	}

	/**
	 * 移除缓存(直接操作一级缓存)
	 * @param key
	 */
	public void removeFirst(String cacheName, String key) {
		Cache cache = firstCacheManager.buildCache(cacheName);
		cache.remove(key);
	}

	/**
	 * 移除缓存(直接操作二级缓存)
	 * @param key
	 */
	public void removeSecond(String cacheName, String key) {
		Cache cache = secondCacheManager.buildCache(cacheName);
		cache.remove(key);
	}

	/**
	 * 批量移除
	 * @param keys
	 */
	public void remove(String cacheName, List<String> keys) {
		Cache cache = cacheManager.buildCache(cacheName);
		cache.remove(keys);
	}

	/**
	 * 批量移除(直接操作一级缓存)
	 * @param keys
	 */
	public void removeFirst(String cacheName, List<String> keys) {
		Cache cache = firstCacheManager.buildCache(cacheName);
		cache.remove(keys);
	}

	/**
	 * 批量移除(直接操作二级缓存)
	 * @param keys
	 */
	public void removeSecond(String cacheName, List<String> keys) {
		Cache cache = secondCacheManager.buildCache(cacheName);
		cache.remove(keys);
	}

	/**
	 * 清除cacheName内缓存
	 */
	public void clear(String cacheName) {
		Cache cache = cacheManager.buildCache(cacheName);
		cache.clear();
	}

	/**
	 * 清除cacheName内缓存(直接操作一级缓存)
	 */
	public void clearFirst(String cacheName) {
		Cache cache = firstCacheManager.buildCache(cacheName);
		cache.clear();
	}

	/**
	 * 清除cacheName内缓存(直接操作二级缓存)
	 */
	public void clearSecond(String cacheName) {
		Cache cache = secondCacheManager.buildCache(cacheName);
		cache.clear();
	}

}
