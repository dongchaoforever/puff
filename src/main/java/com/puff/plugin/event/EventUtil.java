package com.puff.plugin.event;

import com.puff.log.Log;
import com.puff.log.LogFactory;

/**
 */
public class EventUtil {

	private static final Log log = LogFactory.get();

	private static EventHandler handler;

	static void init(EventHandler handler) {
		EventUtil.handler = handler;
	}

	/**
	 * 发布事件
	 * 
	 * @param <T>
	 * 
	 * @param event
	 *            zhe ApplicationEvent
	 */
	public static void postEvent(ApplicationEvent event) {
		try {
			handler.postEvent(event);
		} catch (Exception e) {
			log.error("Event Msg '{0}' Happend Error ", e, event.eventKey());
		}
	}
}
