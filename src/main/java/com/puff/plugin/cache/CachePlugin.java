package com.puff.plugin.cache;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.puff.framework.utils.StringUtil;
import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.plugin.Plugin;
import com.puff.plugin.cache.ehcache.EHCacheManager;
import com.puff.plugin.cache.multilevel.MultiLevelCacheManager;
import com.puff.plugin.cache.redis.RedisCacheManager;
import com.puff.plugin.cache.rediscluster.RedisClusterCacheManager;
import com.puff.plugin.cache.sync.CacheSync;
import com.puff.plugin.cache.sync.CacheSyncHandler;
import com.puff.plugin.cache.sync.JGroupsCacheSync;
import com.puff.plugin.cache.sync.RedisCacheSync;
import com.puff.plugin.msg.CommandFactory;
import com.puff.plugin.redis.RedisSource;

/**
 * cache 客户端
 * @author dongchao
 *
 */
public class CachePlugin implements Plugin {

	private final static Log log = LogFactory.get();
	private CacheManager cacheManager;
	private CacheManager firstCacheManager;// 一级缓存
	private CacheManager secondCacheManager;// 二级缓存
	private MultiLevelCacheManager multiCacheManager;// 多级缓存
	private final List<String> cacheType = Arrays.asList("ehcache", "redis", "rediscluster");// 支持缓存类型
	private final List<String> secondCacheType = Arrays.asList("redis", "rediscluster");// 二级缓存支持类型
	private final List<String> cacheSyncType = Arrays.asList("jgroups", "redis");
	private CacheSync cacheSync;

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public CacheManager getFirstCacheManager() {
		return firstCacheManager;
	}

	public CacheManager getSecondCacheManager() {
		return secondCacheManager;
	}

	public MultiLevelCacheManager getMultiCacheManager() {
		return multiCacheManager;
	}

	public CacheSync getCacheSync() {
		return cacheSync;
	}

	private CacheManager getCacheManager(String value) {
		if ("ehcache".equalsIgnoreCase(value))
			return new EHCacheManager();
		if ("redis".equalsIgnoreCase(value))
			return new RedisCacheManager();
		if ("rediscluster".equalsIgnoreCase(value))
			return new RedisClusterCacheManager();
		return new EHCacheManager();
	}

	@Override
	public void init(Properties prop) {
		String cache = StringUtil.empty(prop.getProperty("cache.manager"), prop.getProperty("cache.provider"));
		String l1_cache = StringUtil.empty(prop.getProperty("cache.level1.manager"), prop.getProperty("cache.L1.provider"));
		String l2_cache = StringUtil.empty(prop.getProperty("cache.level2.manager"), prop.getProperty("cache.L2.provider"));

		if (StringUtil.allEmpty(cache, l1_cache, l2_cache)) {
			String err = " Cache Manager can not be null...please check your config', Currently only supported " + cacheType.toString();
			log.error(err);
			throw new IllegalArgumentException(err);
		}
		boolean singleCache = false;
		if (StringUtil.notEmpty(cache)) {
			if (!cacheType.contains(cache)) {
				String err = "Unknown cache Manager '" + cache + "', Currently only supported " + cacheType.toString();
				log.error(err);
				throw new IllegalArgumentException(err);
			}
			singleCache = true;
		}
		boolean cache_1 = false;
		if (StringUtil.notEmpty(l1_cache)) {
			if (!cacheType.contains(l1_cache)) {
				String err = "Unknown cache Manager '" + l1_cache + "', Currently only supported " + cacheType.toString();
				log.error(err);
				throw new IllegalArgumentException(err);
			}
			cache_1 = true;
		}
		boolean cache_2 = false;
		if (StringUtil.notEmpty(l2_cache)) {
			if (!cacheType.contains(l2_cache)) {
				String err = "Unknown cache Manager '" + l2_cache + "', Currently only supported " + cacheType.toString();
				log.error(err);
				throw new IllegalArgumentException(err);
			}
			if (cache_1) {
				if (!secondCacheType.contains(l2_cache)) {
					String err = "Unknown level2 cache Manager '" + l2_cache + "',Level2 cache currently only supported 'redis, rediscluster'";
					log.error(err);
					throw new IllegalArgumentException(err);
				}
			}
			cache_2 = true;
		}
		if (cache_1 && cache_2) {
			if (l1_cache.equals(l2_cache)) {
				String err = "Level1 cache can not as same as the level2 cache ";
				log.error(err);
				throw new IllegalArgumentException(err);
			}
			firstCacheManager = getCacheManager(l1_cache);
			log.info("Using L1 CacheManager : " + firstCacheManager.getClass().getName());
			secondCacheManager = getCacheManager(l2_cache);
			log.info("Using L2 CacheManager : " + secondCacheManager.getClass().getName());
			multiCacheManager = new MultiLevelCacheManager(firstCacheManager, secondCacheManager);
			multiCacheManager.start(prop);
			String cacheSyncStr = StringUtil.empty(prop.getProperty("cache.sync"), prop.getProperty("cache.broadcast.channel"));
			if (StringUtil.notBlank(cacheSyncStr)) {
				cacheSyncStr = cacheSyncStr.toLowerCase();
				if (cacheSyncType.contains(cacheSyncStr)) {
					if ("redis".equals(cacheSyncStr)) {
						String sourceName = prop.getProperty("redis.clientName", RedisSource.defaultRedisSource);
						cacheSync = new RedisCacheSync(sourceName);
					}
					if ("jgroups".equals(cacheSyncStr)) {
						cacheSync = new JGroupsCacheSync();
					}
					multiCacheManager.setCacheSync(cacheSync.init());
				}
			}
			cacheManager = multiCacheManager;
			CommandFactory.bindCommand(new CacheSyncHandler());
		} else {// single cache

			if (singleCache) {
				cacheManager = getCacheManager(cache);
			} else if (cache_1) {
				cacheManager = getCacheManager(l1_cache);
			} else if (cache_2) {
				cacheManager = getCacheManager(l2_cache);
			}
			cacheManager.start(prop);
			firstCacheManager = cacheManager;
			secondCacheManager = cacheManager;
			log.info("Using CacheManager : " + cacheManager.getClass().getName());
		}
		CacheClient client = CacheClient.getInstance();
		client.setFirstCacheManager(firstCacheManager).setSecondCacheManager(secondCacheManager).setCacheManager(cacheManager);
	}

	@Override
	public boolean start() {
		return true;
	}

	@Override
	public boolean stop() {
		if (multiCacheManager != null) {
			multiCacheManager.stop();
		} else {
			if (cacheManager != null) {
				cacheManager.stop();
			}
			if (firstCacheManager != null) {
				firstCacheManager.stop();
			}
			if (secondCacheManager != null) {
				secondCacheManager.stop();
			}
		}
		return true;
	}

}
