package com.puff.framework.utils;

/**
 * int 转 Integer装箱方法的优化,取消了{@link Integer#intValue()}原有正负数判断
 * 
 * @author dongchao
 *
 */
public class NumberUtil {
	static Integer[] INTEGER = new Integer[256];
	static {
		for (int i = 0; i < INTEGER.length; i++) {
			INTEGER[i] = new Integer(i);
		}
	}

	public static Integer valueOf(int i) {
		if (i >= 0 && i < INTEGER.length) {
			return INTEGER[i];
		} else {
			return new Integer(i);
		}
	}

}
