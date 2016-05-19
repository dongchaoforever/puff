package com.puff.plugin.cache.redis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.puff.framework.utils.SerializeUtil;
import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.plugin.cache.Cache;
import com.puff.plugin.cache.CallBack;
import com.puff.plugin.redis.JedisClient;
import com.puff.plugin.redis.RedisSource;

import redis.clients.jedis.Jedis;

public class RedisCache implements Cache {
	private final static Log log = LogFactory.get(RedisCache.class);

	private String cacheName;
	private JedisClient client;

	public RedisCache(String cacheName, String sourceName) {
		this.cacheName = cacheName;
		this.client = RedisSource.use(sourceName);
	}

	private String getKeyName(String key) {
		return cacheName + ":" + key;
	}

	@Override
	public boolean exist(String key) {
		return client.exists(getKeyName(key));
	}

	@Override
	public <T extends Serializable> T get(String key) {
		return client.get(getKeyName(key));
	}

	@Override
	public <T extends Serializable> List<T> get(List<String> keys) {
		if (keys == null) {
			return Collections.emptyList();
		}
		Jedis jedis = client.getJedis();
		try {
			int size = keys.size();
			List<T> list = new ArrayList<T>(size);
			for (int i = 0; i < size; i++) {
				byte[] bkey = client.keyToBytes(getKeyName(keys.get(i)));
				T value = SerializeUtil.fstdeserialize(jedis.get(bkey));
				if (value != null) {
					list.add(value);
				}
			}
			return list;
		} finally {
			client.close(jedis);
		}
	}

	/**
	 * 有callBack时，忽略从缓存中获取时候的出错
	 */
	@Override
	public <T extends Serializable> T get(String key, CallBack<T> callBack) {
		T result = null;
		boolean cacheError = false;
		Jedis jedis = null;
		byte[] bkey = null;
		byte[] arr = null;
		try {
			jedis = client.getJedis();
			bkey = client.keyToBytes(getKeyName(key));
			arr = jedis.get(bkey);
		} catch (Exception e) {
			cacheError = true;
			log.error("get from redis error ", e);
		} finally {
			try {
				if (arr == null) {
					result = callBack.call(key);
					if (result != null && !cacheError) {
						jedis.set(bkey, SerializeUtil.fstserialize(result));
					}
				} else {
					result = SerializeUtil.fstdeserialize(arr);
				}
			} finally {
				client.close(jedis);
			}
		}
		return result;
	}

	/**
	 * 有callBack时，忽略从缓存中获取时候的出错
	 */
	@Override
	public <T extends Serializable> T get(String key, CallBack<T> callBack, int exp) {
		T result = null;
		boolean cacheError = false;
		Jedis jedis = null;
		byte[] bkey = null;
		byte[] arr = null;
		try {
			jedis = client.getJedis();
			bkey = client.keyToBytes(getKeyName(key));
			arr = jedis.get(bkey);
		} catch (Exception e) {
			cacheError = true;
			log.error("get from redis error ", e);
		} finally {
			try {
				if (arr == null) {
					result = callBack.call(key);
					if (result != null && !cacheError) {
						jedis.setex(bkey, exp, SerializeUtil.fstserialize(result));
					}
				} else {
					result = SerializeUtil.fstdeserialize(arr);
				}
			} finally {
				client.close(jedis);
			}
		}
		return result;
	}

	@Override
	public void put(String key, Object value) {
		client.set(getKeyName(key), value);
	}

	@Override
	public void put(String key, Object value, int expire) {
		client.setex(getKeyName(key), expire, value);
	}

	@Override
	public void update(String key, Object value) {
		Jedis jedis = client.getJedis();
		try {
			byte[] bkey = client.keyToBytes(getKeyName(key));
			if (value == null) {
				jedis.del(bkey);
			} else {
				Long ttl = jedis.ttl(bkey);
				byte[] bvalue = SerializeUtil.fstserialize(value);
				if (ttl > 0) {
					jedis.setex(bkey, ttl.intValue(), bvalue);
				} else {
					jedis.set(bkey, bvalue);
				}
			}
		} finally {
			client.close(jedis);
		}
	}

	@Override
	public int ttl(String key) {
		return client.ttl(getKeyName(key)).intValue();
	}

	@Override
	public List<String> keys() {
		Set<String> keys = client.keys(cacheName + ":*");
		List<String> list = new ArrayList<String>(keys.size());
		for (String key : keys) {
			list.add(key.substring(cacheName.length() + 1));
		}
		return list;
	}

	@Override
	public void remove(String key) {
		client.del(getKeyName(key));
	}

	@Override
	public void remove(List<String> keys) {
		if (keys != null) {
			int size = keys.size();
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < size; i++) {
				list.add(getKeyName(keys.get(i)));
			}
			client.del(list);
		}
	}

	@Override
	public void clear() {
		client.clear(cacheName);
	}

}
