package com.puff.plugin.cache.rediscluster;

import java.util.Properties;

import com.puff.core.Puff;
import com.puff.plugin.cache.Cache;
import com.puff.plugin.cache.CacheManager;
import com.puff.plugin.redis.RedisPlugin;
import com.puff.plugin.redis.RedisSource;

public class RedisClusterCacheManager implements CacheManager {

	private RedisPlugin plugin;
	private String sourceName;

	public String cacheType() {
		return "RedisCluster";
	}

	public void start(Properties props) {
		synchronized (props) {
			sourceName = props.getProperty("redis.clientName", RedisSource.defaultRedisSource);
			if (!RedisSource.hasSource(sourceName)) {
				plugin = (RedisPlugin) Puff.startPlugin(RedisPlugin.class, props);
			} else {
				plugin = Puff.getPlugin(RedisPlugin.class);
			}
		}
	}

	public void stop() {
		if (plugin != null) {
			plugin.stop();
		}
	}

	public Cache buildCache(String cacheName) {
		return new RedisClusterCache(cacheName, sourceName);
	}

	public void destroy(String cacheName) {

	}

}
