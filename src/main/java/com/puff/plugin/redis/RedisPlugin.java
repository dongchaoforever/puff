package com.puff.plugin.redis;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.puff.framework.utils.StringUtil;
import com.puff.plugin.Plugin;

import redis.clients.jedis.BinaryJedisCluster;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * RedisPlugin. RedisPlugin 支持多个 Redis 服务端，只需要创建多个 RedisPlugin 对象 对应这多个不同的 Redis
 * 服务端即可。 也支持多个 RedisPlugin 对象对应同一 Redis 服务的不同 database
 */
public class RedisPlugin implements Plugin {

	private String sourceName;
	private String cluster;
	private Integer maxRedirections;
	private String host;
	private Integer port;
	private Integer timeout;
	private String password;
	private Integer database;

	private JedisPoolConfig jedisPoolConfig;

	public String getSourceName() {
		return sourceName;
	}

	public String getCluster() {
		return cluster;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public String getPassword() {
		return password;
	}

	public Integer getDatabase() {
		return database;
	}

	@Override
	public void init(Properties prop) {
		Properties props = getProviderProperties(prop);
		sourceName = getProperty(props, "sourceName", RedisSource.defaultRedisSource);
		cluster = getProperty(props, "cluster", "");
		maxRedirections = getProperty(props, "maxRedirections", 10);
		host = getProperty(props, "host", "127.0.0.1");
		port = getProperty(props, "port", 6379);
		timeout = getProperty(props, "timeout", 2000);
		password = getProperty(props, "password", null);
		database = getProperty(props, "database", 1);
		jedisPoolConfig = new JedisPoolConfig();
		boolean testWhileIdle = getProperty(props, "testWhileIdle", false);
		jedisPoolConfig.setTestWhileIdle(testWhileIdle);
		long minEvictableIdleTimeMillis = getProperty(props, "minEvictableIdleTimeMillis", 1000);
		jedisPoolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		long timeBetweenEvictionRunsMillis = getProperty(props, "timeBetweenEvictionRunsMillis", 10);
		jedisPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		int numTestsPerEvictionRun = getProperty(props, "numTestsPerEvictionRun", 10);
		jedisPoolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);

		jedisPoolConfig.setLifo(getProperty(props, "lifo", false));
		jedisPoolConfig.setMaxTotal(getProperty(props, "maxTotal", 500));
		jedisPoolConfig.setMinIdle(getProperty(props, "minIdle", 20));
		jedisPoolConfig.setMaxIdle(getProperty(props, "maxIdle", 200));
		jedisPoolConfig.setMaxWaitMillis(getProperty(props, "maxWaitMillis", 3000));

		jedisPoolConfig.setTestWhileIdle(getProperty(props, "testWhileIdle", false));
		jedisPoolConfig.setTestOnBorrow(getProperty(props, "testOnBorrow", true));
		jedisPoolConfig.setTestOnReturn(getProperty(props, "testOnReturn", false));
		jedisPoolConfig.setTestOnCreate(getProperty(props, "testOnCreate", false));

	}

	public boolean start() {
		if (StringUtil.notBlank(cluster)) {
			String[] arr = cluster.split(",");
			if (arr != null) {
				Set<HostAndPort> set = new HashSet<HostAndPort>();
				for (String server : arr) {
					String[] tmp = server.split(":");
					if (tmp.length >= 2) {
						set.add(new HostAndPort(tmp[0], Integer.parseInt(tmp[1])));
					}
				}
				BinaryJedisCluster jc = new BinaryJedisCluster(set, timeout, maxRedirections, jedisPoolConfig);
				JedisClient source = new JedisClient(sourceName, null, jc);
				RedisSource.addSource(source);
			}
		} else {
			JedisPool jedisPool;
			if (port != null && timeout != null && password != null && database != null && database != -1 && sourceName != null)
				jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database, sourceName);
			else if (port != null && timeout != null && password != null && database != null && database != -1)
				jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database);
			else if (port != null && timeout != null && password != null)
				jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
			else if (port != null && timeout != null)
				jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout);
			else if (port != null)
				jedisPool = new JedisPool(jedisPoolConfig, host, port);
			else
				jedisPool = new JedisPool(jedisPoolConfig, host);

			JedisClient source = new JedisClient(sourceName, jedisPool, null);
			RedisSource.addSource(source);
		}

		return true;
	}

	public boolean stop() {
		JedisClient client = RedisSource.removeSource(sourceName);
		if (client == RedisSource.mainSource)
			RedisSource.mainSource = null;
		client.getJedisPool().destroy();
		return true;
	}

	/**
	 * 当RedisPlugin 提供的设置属性仍然无法满足需求时，通过此方法获取到 JedisPoolConfig 对象，可对 redis
	 * 进行更加细致的配置
	 * 
	 * <pre>
	 * 例如：
	 * redisPlugin.getJedisPoolConfig().setMaxTotal(100);
	 * </pre>
	 */
	public JedisPoolConfig getJedisPoolConfig() {
		return jedisPoolConfig;
	}

	private final Properties getProviderProperties(Properties props) {
		Properties new_props = new Properties();
		Enumeration<Object> keys = props.keys();
		String prefix = "redis.";
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith(prefix)) {
				new_props.setProperty(key.substring(prefix.length()), props.getProperty(key));
			}
		}
		return new_props;
	}

	private String getProperty(Properties props, String key, String defaultValue) {
		String value = props.getProperty(key, defaultValue);
		return value != null ? value.trim() : null;
	}

	private int getProperty(Properties props, String key, int defaultValue) {
		try {
			return Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)).trim());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private boolean getProperty(Properties props, String key, boolean defaultValue) {
		return "true".equalsIgnoreCase(props.getProperty(key, String.valueOf(defaultValue)).trim());
	}

}
