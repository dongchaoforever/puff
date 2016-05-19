package com.puff.plugin.cache.multilevel;

import java.util.Properties;

import com.puff.plugin.cache.Cache;
import com.puff.plugin.cache.CacheManager;
import com.puff.plugin.cache.sync.CacheSync;

/**
 * 多级缓存管理
 * @author dongchao
 *
 */
public class MultiLevelCacheManager implements CacheManager {

	private CacheManager firstCacheManager;
	private CacheManager secondCacheManager;
	private CacheSync cacheSync;

	public MultiLevelCacheManager(CacheManager firstCacheManager, CacheManager secondCacheManager) {
		super();
		this.firstCacheManager = firstCacheManager;
		this.secondCacheManager = secondCacheManager;
	}

	public void setCacheSync(CacheSync cacheSync) {
		this.cacheSync = cacheSync;
	}

	@Override
	public String cacheType() {
		return "MultiLevel";
	}

	@Override
	public void start(Properties props) {
		firstCacheManager.start(props);
		secondCacheManager.start(props);
	}

	@Override
	public void stop() {
		firstCacheManager.stop();
		secondCacheManager.stop();
	}

	@Override
	public Cache buildCache(String cacheName) {
		return new MultiLevelCache(cacheName, firstCacheManager.buildCache(cacheName), secondCacheManager).setCacheSync(cacheSync);
	}

	@Override
	public void destroy(String cacheName) {
		firstCacheManager.destroy(cacheName);
		secondCacheManager.destroy(cacheName);
	}

}
