package com.puff.plugin.redis;

import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.plugin.msg.Command;
import com.puff.plugin.msg.CommandFactory;
import com.puff.plugin.msg.CommandHandler;

import redis.clients.jedis.BinaryJedisPubSub;

public class PuffJedisPubSub extends BinaryJedisPubSub {
	private final static Log log = LogFactory.get();

	/**
	 * 消息接收
	 * 
	 * @param channel 缓存 Channel
	 * @param message 接收到的消息
	 */
	@Override
	public void onMessage(byte[] channel, byte[] message) {
		// 无效消息
		if (message != null && message.length <= 0) {
			log.warn("Message is empty.");
			return;
		}
		try {
			Command cmd = Command.parse(message);
			if (cmd != null && !cmd.msgFromSlef()) {
				CommandHandler handler = CommandFactory.getCommand(String.valueOf(cmd.nameSpace));
				if (handler != null) {
					handler.handle(cmd);
				}
			}
		} catch (Exception e) {
			log.error("Unable to handle received msg", e);
		}
	}
}
