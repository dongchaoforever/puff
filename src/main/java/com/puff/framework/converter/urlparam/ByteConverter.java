package com.puff.framework.converter.urlparam;

/**
 * Convert String to Byte.
 * 
 */
public class ByteConverter extends Converter<Byte> {

	public Byte convert(String s) {
		return Byte.parseByte(s);
	}

	@Override
	public Byte defaultVal() {
		return 0;
	}

}
