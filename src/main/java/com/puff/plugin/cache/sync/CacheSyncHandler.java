package com.puff.plugin.cache.sync;

import java.io.Serializable;
import java.util.List;

import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.plugin.cache.CacheClient;
import com.puff.plugin.msg.Command;
import com.puff.plugin.msg.CommandHandler;

/**
 * 处理接收消息
 * @author dongchao
 *
 */
public class CacheSyncHandler implements CommandHandler {

	private final static Log log = LogFactory.get();
	private static final CacheClient cacheClient = CacheClient.getInstance();

	private static void onPut(String cacheName, String key) {
		Serializable value = cacheClient.getSecond(cacheName, key);
		cacheClient.putFirst(cacheName, key, value, cacheClient.ttlSecond(cacheName, key));
	}

	private static void onClear(String cacheName) {
		cacheClient.clearFirst(cacheName);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void onRemove(String cacheName, Object key) {
		if (key instanceof List) {
			cacheClient.removeFirst(cacheName, (List) key);
		} else {
			cacheClient.removeFirst(cacheName, (String) key);
		}
	}

	@Override
	public byte getNameSpace() {
		return Command.DEFAUTL_NS;
	}

	@Override
	public void handle(Command cmd) {
		switch (cmd.operator) {
		case Command.OPT_PUT:
			onPut(cmd.cacheName, String.valueOf(cmd.key));
			break;
		case Command.OPT_CLEAR:
			onClear(cmd.cacheName);
			break;
		case Command.OPT_REMOVE:
			onRemove(cmd.cacheName, cmd.key);
			break;
		default:
			log.warn("Unknown message type = " + cmd.operator);
		}
	}

}
