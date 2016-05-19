package com.puff.framework.converter.urlparam;

/**
 * Convert String to Short.
 * 
 */
public class ShortConverter extends Converter<Short> {

    public Short convert(String s) {
        return Short.parseShort(s);
    }

	@Override
	public Short defaultVal() {
		return 0;
	}

}
