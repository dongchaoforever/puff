package com.puff.plugin;

import java.util.Properties;

/**
 * 
 * @author 董超 dongchaoforever@gmail.com
 * @date 2013-2-25 上午10:54:34
 * @Description: Puff plugin interface 
 * Plugin p = Puff.getPlugin(YouPlugin.class);
 */
public interface Plugin {

	void init(Properties prop);

	/**
	 * start plugin
	 * 
	 * @return
	 */
	boolean start();

	/**
	 * stop plugin
	 * 
	 * @return
	 */
	boolean stop();
}
