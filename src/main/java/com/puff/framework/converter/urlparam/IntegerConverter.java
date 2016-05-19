package com.puff.framework.converter.urlparam;

/**
 * Convert String to Integer.
 * 
 */
public class IntegerConverter extends Converter<Integer> {

    public Integer convert(String s) {
        return Integer.parseInt(s);
    }

	@Override
	public Integer defaultVal() {
		return 0;
	}

}
