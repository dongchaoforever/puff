package com.puff.plugin.redis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.puff.web.mvc.Dispatcher;
import com.puff.web.mvc.ExecutorProvider;

import redis.clients.jedis.Jedis;

public class RedisDispatcher extends Dispatcher {

	@Override
	public void dispatching(String target, HttpServletRequest request, HttpServletResponse response, ExecutorProvider provider) {

		JedisClient source = RedisSource.use();
		Jedis jedis = source.getThreadLocalJedis();
		if (jedis != null) {
			chain.dispatching(target, request, response, provider);
			return;
		}
		try {
			jedis = source.getJedis();
			source.setThreadLocalJedis(jedis);
			chain.dispatching(target, request, response, provider);
		} finally {
			source.removeThreadLocalJedis();
			jedis.close();
		}
	}

}
