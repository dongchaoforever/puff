package com.puff.framework.converter.urlparam;

/**
 * Convert String to Boolean.
 * 
 */
public class BooleanConverter extends Converter<Boolean> {

    public Boolean convert(String s) {
        return Boolean.parseBoolean(s);
    }

	@Override
	public Boolean defaultVal() {
		return Boolean.FALSE;
	}

}
