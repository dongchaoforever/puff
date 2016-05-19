package com.puff.plugin.cache.rediscluster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.puff.framework.utils.SerializeUtil;
import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.plugin.cache.Cache;
import com.puff.plugin.cache.CallBack;
import com.puff.plugin.redis.RedisSource;

import redis.clients.jedis.BinaryJedisCluster;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.SafeEncoder;

/**
 * redis集群缓存
 * @author dongchao
 *
 */
public class RedisClusterCache implements Cache {

	private final static Log log = LogFactory.get(RedisClusterCache.class);

	private String cacheName;
	private BinaryJedisCluster jedisCluster;

	private String getKeyName(String key) {
		return cacheName + ":" + key;
	}

	private byte[] getByteKeyName(String key) {
		return SafeEncoder.encode(getKeyName(key));
	}

	public RedisClusterCache(String cacheName, String sourceName) {
		this.cacheName = cacheName;
		this.jedisCluster = RedisSource.use(sourceName).getCluster();
	}

	@Override
	public boolean exist(String key) {
		return jedisCluster.exists(getByteKeyName(key));
	}

	@Override
	public <T extends Serializable> T get(String key) {
		byte[] bs = jedisCluster.get(getByteKeyName(key));
		return SerializeUtil.fstdeserialize(bs);
	}

	@Override
	public <T extends Serializable> List<T> get(List<String> keys) {
		List<T> list = null;
		if (keys != null && keys.size() > 0) {
			list = new ArrayList<T>(keys.size());
			for (String key : keys) {
				T result = get(key);
				list.add(result);
			}
		}
		return list;
	}

	@Override
	public <T extends Serializable> T get(String key, CallBack<T> callBack) {
		T result = null;
		boolean cacheError = false;
		try {
			result = get(key);
		} catch (Exception e) {
			cacheError = true;
			log.error("get from rediscluster error ", e);
		} finally {
			result = callBack.call(key);
			if (result != null && !cacheError) {
				put(key, result);
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
			log.error("get from rediscluster error ", e);
		} finally {
			result = callBack.call(key);
			if (result != null && !cacheError) {
				put(key, result, exp);
			}
		}
		return result;
	}

	@Override
	public void put(String key, Object value) {
		jedisCluster.set(getByteKeyName(key), SerializeUtil.fstserialize(value));
	}

	@Override
	public void put(String key, Object value, int expire) {
		jedisCluster.setex(getByteKeyName(key), expire, SerializeUtil.fstserialize(value));
	}

	@Override
	public void update(String key, Object value) {
		byte[] bkey = getByteKeyName(key);
		if (value == null) {
			jedisCluster.del(bkey);
		} else {
			Long ttl = jedisCluster.ttl(bkey);
			byte[] bvalue = SerializeUtil.fstserialize(value);
			if (ttl > 0) {
				jedisCluster.setex(bkey, ttl.intValue(), bvalue);
			} else {
				jedisCluster.set(bkey, bvalue);
			}
		}
	}

	@Override
	public int ttl(String key) {
		return jedisCluster.ttl(getByteKeyName(key)).intValue();
	}

	@Override
	public List<String> keys() {
		List<String> result = new ArrayList<String>();
		Map<String, JedisPool> nodes = jedisCluster.getClusterNodes();
		if (nodes != null) {
			Set<String> tmp = new HashSet<String>();
			for (Entry<String, JedisPool> entry : nodes.entrySet()) {
				JedisPool jedisPool = entry.getValue();
				Jedis jedis = jedisPool.getResource();
				try {
					Set<String> keys = jedis.keys(cacheName + ":*");
					if (keys != null) {
						for (String key : keys) {
							tmp.add(key.substring(cacheName.length() + 1));
						}
					}
				} finally {
					jedis.close();
				}
			}
			result.addAll(tmp);
		}
		return result;
	}

	@Override
	public void remove(String key) {
		jedisCluster.del(getByteKeyName(key));
	}

	@Override
	public void remove(List<String> keys) {
		if (keys != null && keys.size() > 0) {
			for (String key : keys) {
				remove(key);
			}
		}
	}

	@Override
	public void clear() {
		remove(keys());
	}

}
