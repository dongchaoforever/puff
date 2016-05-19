package com.puff.plugin.redis;

import java.util.concurrent.ConcurrentHashMap;

import com.puff.framework.utils.StringUtil;

public class RedisSource {

	public static final String defaultRedisSource = "__PUFF_DEFAULT_REDIS_SOURCE___";

	static JedisClient mainSource;

	private static final ConcurrentHashMap<String, JedisClient> sourceMap = new ConcurrentHashMap<String, JedisClient>();

	public static synchronized void addSource(JedisClient source) {
		if (source == null)
			throw new IllegalArgumentException("source can not be null");
		if (sourceMap.containsKey(source.getName()))
			throw new IllegalArgumentException("source already exists");

		sourceMap.put(source.getName(), source);
		if (mainSource == null)
			mainSource = source;
	}

	public static boolean hasSource(String sourceName) {
		return sourceMap.containsKey(sourceName);
	}

	static JedisClient removeSource(String sourceName) {
		return sourceMap.remove(sourceName);
	}

	/**
	 * 提供一个设置设置主缓存 mainclient 的机会，否则第一个被初始化的 Cache 将成为 mainclient
	 */
	public static void setMainSource(String sourceName) {
		if (StringUtil.blank(sourceName))
			throw new IllegalArgumentException("sourceName can not be blank");
		sourceName = sourceName.trim();
		JedisClient source = sourceMap.get(sourceName);
		if (source == null)
			throw new IllegalArgumentException("the source not exists: " + sourceName);
		RedisSource.mainSource = source;
	}

	public static JedisClient use() {
		return mainSource;
	}

	public static JedisClient use(String clientName) {
		return sourceMap.get(clientName);
	}

}
