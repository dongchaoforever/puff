package com.puff.plugin.cache.sync;

import com.puff.plugin.msg.Command;

public interface CacheSync {

	/**
	 * 初始化
	 */
	public CacheSync init();

	/**
	 * 发送命令
	 * @param command
	 */
	public void sendCommand(Command command);

}
