package com.puff.plugin.cache.sync;

import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.plugin.msg.Command;
import com.puff.plugin.redis.JedisClient;
import com.puff.plugin.redis.PuffJedisPubSub;
import com.puff.plugin.redis.RedisSource;

/**
 * 缓存多播通道
 * 
 */
public class RedisCacheSync implements CacheSync {
	private final static Log log = LogFactory.get();
	private final JedisClient jedisClient;
	private final static String channel = "MutliCache-Redis";

	public RedisCacheSync(String clientName) {
		jedisClient = RedisSource.use(clientName);
	}

	@Override
	public CacheSync init() {
		long ct = System.currentTimeMillis();
		Thread thread_subscribe = new Thread(new Runnable() {
			private final PuffJedisPubSub jedisPubSub = new PuffJedisPubSub();

			@Override
			public void run() {
				try {
					jedisClient.puffsubscribe(jedisPubSub, channel);
				} catch (Exception e) {
				}
			}
		});
		thread_subscribe.setName("Puff-Redis-Subscribe");
		thread_subscribe.setDaemon(true);
		thread_subscribe.setPriority(5);
		thread_subscribe.start();
		log.info("Connected to RedisPubSub:" + channel + ", time " + (System.currentTimeMillis() - ct) + " ms.");
		return this;
	}

	@Override
	public void sendCommand(Command command) {
		jedisClient.puffpublish(channel, command.toBuffers());
	}

}
