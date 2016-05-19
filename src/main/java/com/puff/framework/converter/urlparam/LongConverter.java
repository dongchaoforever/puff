package com.puff.framework.converter.urlparam;

/**
 * Convert String to Long.
 * 
 */
public class LongConverter extends Converter<Long> {

    public Long convert(String s) {
        return Long.parseLong(s);
    }

	@Override
	public Long defaultVal() {
		return 0l;
	}

}
