package com.puff.plugin.event;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

/**
 * 事件实际处理的类
 */
public class EventHandler {

	private final ArrayListMultimap<String, ApplicationListener<ApplicationEvent>> map;
	private final ExecutorService pool;

	public EventHandler(ArrayListMultimap<String, ApplicationListener<ApplicationEvent>> map, ExecutorService pool) {
		super();
		this.map = map;
		this.pool = pool;
	}

	/**
	 * 执行发送消息
	 * 
	 * @param event
	 *            ApplicationEvent
	 */
	public void postEvent(final ApplicationEvent event) {
		Collection<ApplicationListener<ApplicationEvent>> listenerList = map.get(event.eventKey());
		if (listenerList != null) {
			for (final ApplicationListener<ApplicationEvent> listener : listenerList) {
				if (null != pool) {
					pool.execute(new Runnable() {
						@Override
						public void run() {
							try {
								listener.onApplicationEvent(event);
							} catch (Exception e) {
							}
						}
					});
				} else {
					try {
						listener.onApplicationEvent(event);
					} catch (Exception e) {
					}
				}
			}
		}
	}
}
