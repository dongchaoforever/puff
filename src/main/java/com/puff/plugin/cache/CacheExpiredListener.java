package com.puff.plugin.cache;

/**
 * 侦听缓存中的某个记录超时
 * 
 */
public interface CacheExpiredListener {

	/**
	 * 当缓存中的某个对象超时被清除的时候触发
	 * 
	 * @param region
	 * @param key
	 */
	public void notifyElementExpired(String region, Object key);

}
