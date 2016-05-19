package com.puff.plugin.session;

import java.util.Properties;

import com.puff.core.Puff;
import com.puff.plugin.Plugin;
import com.puff.plugin.redis.RedisSource;
import com.puff.plugin.redis.RedisPlugin;

public class DistributedSessionPlugin implements Plugin {

	@Override
	public void init(Properties prop) {
		SessionConfig.INSTANCE.setCookieDomain(prop.getProperty("cookieDomain", ""));
		SessionConfig.INSTANCE.setCookiePath(prop.getProperty("cookiePath", "/"));
		SessionConfig.INSTANCE.setSessionId(prop.getProperty("sessionId", SessionConfig.DEFAULT_SESSIONID));
		SessionConfig.INSTANCE.setSessionTimeout(Integer.parseInt(prop.getProperty("sessionTimeout", "3600")));
		String sourceName = prop.getProperty("sourceName", RedisSource.defaultRedisSource);

		if (!RedisSource.hasSource(sourceName)) {
			Puff.startPlugin(RedisPlugin.class, prop);
		}
		RedisDataEngine dataEngine = RedisDataEngine.getInstance();
		dataEngine.initRedisSource(RedisSource.use(sourceName));
		SessionConfig.INSTANCE.setDataEngine(dataEngine);
	}

	@Override
	public boolean start() {
		Puff.addDispatcher(new DistributedSessionDispatcher());
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}

}
