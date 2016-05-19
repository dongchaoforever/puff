package com.puff.framework.converter.urlparam;

import com.puff.log.Log;
import com.puff.log.LogFactory;

/**
 * Convert String to any given type.
 * 
 * @author DC
 * 
 * @param <T>
 *            Generic type of converted result.
 */
public abstract class Converter<T> {
	private static Log log = LogFactory.get(Converter.class);

	public T getVal(String s) {
		try {
			return convert(s);
		} catch (Exception e) {
			log.error("Convert error return default value, case by " + e);
			return defaultVal();
		}
	}

	/**
	 * Convert a not-null String to specified object.
	 */
	abstract T convert(String s);

	abstract T defaultVal();

}
