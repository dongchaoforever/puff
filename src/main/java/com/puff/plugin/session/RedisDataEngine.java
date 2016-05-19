package com.puff.plugin.session;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.puff.exception.CacheException;
import com.puff.framework.utils.DateUtil;
import com.puff.framework.utils.SerializeUtil;
import com.puff.plugin.redis.JedisClient;
import com.puff.plugin.redis.RedisSource;

import redis.clients.jedis.BinaryJedisCluster;
import redis.clients.jedis.Jedis;
import redis.clients.util.SafeEncoder;

public class RedisDataEngine implements DataEngine {

	private JedisClient redis;
	private BinaryJedisCluster jedisCluster;

	private static class Inner {
		private static final RedisDataEngine INSTANCE = new RedisDataEngine();
	}

	public final static RedisDataEngine getInstance() {
		return Inner.INSTANCE;
	}

	private RedisDataEngine() {
	}

	protected void initRedisSource(JedisClient source) {
		if (source == null) {
			source = RedisSource.use();
		}
		this.redis = source;
		this.jedisCluster = source.getCluster();
	}

	@Override
	public boolean containsKey(String key) {
		if (jedisCluster == null) {
			return redis.exists(key);
		} else {
			return jedisCluster.exists(SafeEncoder.encode(key));
		}
	}

	@Override
	public boolean put(String key, Object value) {
		if (jedisCluster == null) {
			redis.set(key, value);
		} else {
			jedisCluster.set(SafeEncoder.encode(key), SerializeUtil.fstserialize(value));
		}
		return true;
	}

	@Override
	public boolean put(String key, Object value, int expiredTime) {
		if (jedisCluster == null) {
			if (expiredTime <= 0) {
				redis.set(key, value);
			} else {
				redis.setex(key, expiredTime, value);
			}
		} else {
			if (expiredTime <= 0) {
				jedisCluster.set(SafeEncoder.encode(key), SerializeUtil.fstserialize(value));
			} else {
				jedisCluster.setex(SafeEncoder.encode(key), expiredTime, SerializeUtil.fstserialize(value));
			}
		}
		return true;
	}

	@Override
	public boolean put(String key, Object value, Date date) {
		if (jedisCluster == null) {
			Jedis jedis = null;
			try {
				jedis = redis.getJedis();
				if (value == null) {
					jedis.del(key.getBytes());
				} else {
					if (date == null) {
						jedis.set(key.getBytes(), SerializeUtil.serialize(value));
					} else {
						long timeInMillis = Math.abs(DateUtil.timeGap(date, Calendar.getInstance().getTime()).longValue());
						long expiredTime = timeInMillis / 1000;
						jedis.setex(key.getBytes(), (int) expiredTime, SerializeUtil.serialize(value));
					}
				}
			} catch (Exception e) {
				throw new CacheException(e);
			} finally {
				redis.close(jedis);
			}
		} else {
			byte[] bkey = SafeEncoder.encode(key);
			if (value == null) {
				jedisCluster.del(bkey);
			} else {
				if (date == null) {
					jedisCluster.set(bkey, SerializeUtil.serialize(value));
				} else {
					long timeInMillis = Math.abs(DateUtil.timeGap(date, Calendar.getInstance().getTime()).longValue());
					long expiredTime = timeInMillis / 1000;
					jedisCluster.setex(bkey, (int) expiredTime, SerializeUtil.serialize(value));
				}
			}
		}
		return true;
	}

	@Override
	public boolean add(String key, Object value) {
		return put(key, value);
	}

	@Override
	public boolean add(String key, Object value, int expiredTime) {
		return put(key, value, expiredTime);
	}

	@Override
	public Object get(String key) {
		if (jedisCluster == null) {
			return redis.get(key);
		} else {
			return SerializeUtil.fstdeserialize(jedisCluster.get(SafeEncoder.encode(key)));
		}
	}

	@Override
	public Map<String, Object> get(String[] keys) {
		if (keys == null || keys.length == 0) {
			return Collections.emptyMap();
		}
		if (jedisCluster == null) {
			Map<String, Object> map = new HashMap<String, Object>();
			Jedis jedis = null;
			try {
				jedis = redis.getJedis();
				for (String key : keys) {
					byte[] b = jedis.get(key.getBytes());
					if (b != null) {
						map.put(key, SerializeUtil.deserialize(b));
					}
				}
			} catch (Exception e) {
				throw new CacheException(e);
			} finally {
				redis.close(jedis);
			}
			return map;
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
			for (String key : keys) {
				byte[] b = jedisCluster.get(SafeEncoder.encode(key));
				if (b != null) {
					map.put(key, SerializeUtil.deserialize(b));
				}
			}
			return map;
		}
	}

	@Override
	public long incr(String key, long magnitude) {
		if (jedisCluster == null) {
			return redis.incrBy(key, magnitude);
		} else {
			return jedisCluster.incrBy(SafeEncoder.encode(key), magnitude);
		}
	}

	@Override
	public long decr(String key, long magnitude) {
		if (jedisCluster == null) {
			return redis.decrBy(key, magnitude);
		} else {
			return jedisCluster.decrBy(SafeEncoder.encode(key), magnitude);
		}
	}

	@Override
	public void remove(String key) {
		if (jedisCluster == null) {
			redis.del(key);
		} else {
			jedisCluster.del(SafeEncoder.encode(key));
		}
	}

}
