package com.puff.plugin.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClusterInfoCache;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class PuffJedisClusterConnectionHandler {
	protected final JedisClusterInfoCache cache;

	public PuffJedisClusterConnectionHandler(Set<HostAndPort> nodes, final GenericObjectPoolConfig poolConfig, int timeout) {
		this(nodes, poolConfig, timeout, timeout);
	}

	public PuffJedisClusterConnectionHandler(Set<HostAndPort> nodes, final GenericObjectPoolConfig poolConfig, int connectionTimeout, int soTimeout) {
		this.cache = new JedisClusterInfoCache(poolConfig, connectionTimeout, soTimeout);
		initializeSlotsCache(nodes, poolConfig);
	}

	@SuppressWarnings("deprecation")
	public Jedis getConnection() {
		// In antirez's redis-rb-cluster implementation,
		// getRandomConnection always return valid connection (able to
		// ping-pong)
		// or exception if all connections are invalid

		List<JedisPool> pools = getShuffledNodesPool();

		for (JedisPool pool : pools) {
			Jedis jedis = null;
			try {
				jedis = pool.getResource();

				if (jedis == null) {
					continue;
				}

				String result = jedis.ping();

				if (result.equalsIgnoreCase("pong"))
					return jedis;

				pool.returnBrokenResource(jedis);
			} catch (JedisConnectionException ex) {
				if (jedis != null) {
					pool.returnBrokenResource(jedis);
				}
			}
		}

		throw new JedisConnectionException("no reachable node in cluster");
	}

	public Jedis getConnectionFromSlot(int slot) {
		JedisPool connectionPool = cache.getSlotPool(slot);
		if (connectionPool != null) {
			// It can't guaranteed to get valid connection because of node
			// assignment
			return connectionPool.getResource();
		} else {
			return getConnection();
		}
	}

	private List<JedisPool> getShuffledNodesPool() {
		List<JedisPool> pools = new ArrayList<JedisPool>();
		pools.addAll(cache.getNodes().values());
		Collections.shuffle(pools);
		return pools;
	}

	private void initializeSlotsCache(Set<HostAndPort> startNodes, GenericObjectPoolConfig poolConfig) {
		for (HostAndPort hostAndPort : startNodes) {
			Jedis jedis = new Jedis(hostAndPort.getHost(), hostAndPort.getPort());
			try {
				cache.discoverClusterNodesAndSlots(jedis);
				break;
			} catch (JedisConnectionException e) {
				// try next nodes
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}
		for (HostAndPort node : startNodes) {
			cache.setNodeIfNotExist(node);
		}
	}

	public void renewSlotCache() {
		for (JedisPool jp : cache.getNodes().values()) {
			Jedis jedis = null;
			try {
				jedis = jp.getResource();
				cache.discoverClusterSlots(jedis);
				break;
			} catch (JedisConnectionException e) {
				// try next nodes
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}
	}

}
