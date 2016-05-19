package com.puff.framework.converter.urlparam;

/**
 * Convert String to Double.
 * 
 */
public class DoubleConverter extends Converter<Double> {

    public Double convert(String s) {
        return Double.parseDouble(s);
    }

	@Override
	public Double defaultVal() {
		return 0.0;
	}

}
