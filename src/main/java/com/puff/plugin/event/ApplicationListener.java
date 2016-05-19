package com.puff.plugin.event;

/**
 * Interface to be implemented by application event listeners. Based on the
 * standard {@code java.util.EventListener} interface for the Observer design
 * pattern.
 *
 * <p>
 * As of Spring 3.0, an ApplicationListener can generically declare the event
 * type that it is interested in. When registered with a Spring
 * ApplicationContext, events will be filtered accordingly, with the listener
 * getting invoked for matching event objects only.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @param <E>
 *            the specific ApplicationEvent subclass to listen to
 * @param <T>
 */
public interface ApplicationListener<E extends ApplicationEvent> {

	/**
	 * Handle an application event.
	 * 
	 * @param event
	 *            the event to respond to
	 */
	void onApplicationEvent(E event);

}