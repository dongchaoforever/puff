package com.puff.plugin.cache.ehcache;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.puff.framework.utils.PathUtil;
import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.plugin.cache.Cache;
import com.puff.plugin.cache.CacheManager;

public class EHCacheManager implements CacheManager {
	private final static Log log = LogFactory.get();
	private final static String CONFIG_XML = "/ehcache.xml";

	private net.sf.ehcache.CacheManager manager;
	private ConcurrentHashMap<String, EHCache> cacheManager;
	private final Lock lock = new ReentrantLock();

	public String cacheType() {
		return "EHCache";
	}

	public void start(Properties props) {
		if (manager != null) {
			return;
		}
		InputStream inputStream = EHCacheManager.class.getResourceAsStream(CONFIG_XML);
		if (inputStream == null) {
			log.warn("Can not find ehcache config file 'ehcache.xml' in classpath, use default config ...");
			inputStream = PathUtil.fromJar("resource/default.config/ehcache.xml");
		}
		manager = new net.sf.ehcache.CacheManager(inputStream);
		cacheManager = new ConcurrentHashMap<String, EHCache>();
	}

	public void stop() {
		if (manager != null) {
			manager.shutdown();
			manager = null;
		}
		cacheManager = null;
	}

	public Cache buildCache(String cacheName) {
		EHCache ehcache = cacheManager.get(cacheName);
		if (ehcache == null) {
			try {
				lock.lock();
				ehcache = cacheManager.get(cacheName);
				if (ehcache == null) {
					net.sf.ehcache.Cache cache = manager.getCache(cacheName);
					if (cache == null) {
						log.warn("Could not find config [" + cacheName + "], using defaults.");
						manager.addCache(cacheName);
						cache = manager.getCache(cacheName);
						log.warn("Started EHCache, cache name: " + cacheName);
					}
					ehcache = new EHCache(cache);
					cacheManager.put(cacheName, ehcache);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				lock.unlock();
			}
		}
		return ehcache;
	}

	public void destroy(String cacheName) {
		cacheManager.remove(cacheName);
	}

}
