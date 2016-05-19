package com.puff.framework.converter.urlparam;

/**
 * Convert String to Float.
 * 
 */
public class FloatConverter extends Converter<Float> {

    public Float convert(String s) {
        return Float.parseFloat(s);
    }

	@Override
	public Float defaultVal() {
		return 0f;
	}

}
