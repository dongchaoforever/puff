package com.puff.plugin.cache;

import java.util.Properties;

public interface CacheManager {


	/**
	 * cache类型
	 * @return
	 */
	public String cacheType();

	/**
	 * 开启缓存
	 * @param props
	 */
	public void start(Properties props);

	/**
	 * 停止缓存
	 */
	public void stop();

	/**
	 * 构建缓存
	 * @param regionName
	 * @return
	 */
	public Cache buildCache(String cacheName);

	/**
	 * 释放缓存
	 * @param name
	 */
	void destroy(String cacheName);

}