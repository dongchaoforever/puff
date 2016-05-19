package com.puff.plugin.redis;

import com.puff.web.interceptor.Interceptor;
import com.puff.web.mvc.DispatcherExecutor;

import redis.clients.jedis.Jedis;

public class RedisInterceptor implements Interceptor {
	/**
	 * 
	 * 通过继承 RedisInterceptor 类并覆盖此方法，可以指定
	 * 
	 * 当前线程所使用的 cache
	 * 
	 */
	protected JedisClient getClient() {
		return RedisSource.use();
	}

	@Override
	public void intercept(DispatcherExecutor executor) {

		JedisClient client = getClient();
		Jedis jedis = client.getThreadLocalJedis();
		if (jedis != null) {
			executor.execute();
			return;
		}
		try {
			jedis = client.getJedis();
			client.setThreadLocalJedis(jedis);
			executor.execute();
		} finally {
			client.removeThreadLocalJedis();
			jedis.close();
		}
	}

}
