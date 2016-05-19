package com.puff.framework.converter.urlparam;

import com.puff.framework.utils.StringUtil;

/**
 * Convert String to Character.
 */
public class CharacterConverter extends Converter<Character> {

	public Character convert(String s) {
		if (StringUtil.empty(s)) {
			throw new IllegalArgumentException("Cannot convert empty string to char.");
		}
		return s.charAt(0);
	}

	@Override
	public Character defaultVal() {
		return 0;
	}

}
