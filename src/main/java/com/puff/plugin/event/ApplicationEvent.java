package com.puff.plugin.event;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class ApplicationEvent implements Serializable {

	/** use serialVersionUID from Spring 1.2 for interoperability */
	private static final long serialVersionUID = 7099057708183571937L;

	private transient Map source;

	public abstract String eventKey();

	public <T> ApplicationEvent addData(String key, T value) {
		source.put(key, value);
		return this;
	}

	public <T> T get(String key) {
		return (T) source.get(key);
	}

	/** System time when the event happened */
	private final long timestamp;

	/**
	 * Create a new ApplicationEvent.
	 * 
	 * @param source
	 *            the component that published the event (never {@code null})
	 */
	public ApplicationEvent() {
		this.source = new HashMap();
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * Return the system time in milliseconds when the event happened.
	 * 
	 * @return Return the system time in milliseconds
	 */
	public final long getTimestamp() {
		return this.timestamp;
	}

}