package com.puff.framework.utils;

public class JdkUtil {
	public static final String JAVA_VERSION;

	public static final boolean IS_JAVA_5;
	public static final boolean IS_JAVA_6;
	public static final boolean IS_JAVA_7;
	public static final boolean IS_JAVA_8;
	public static final boolean IS_JAVA_9;

	public static final boolean IS_AT_LEAST_JAVA_5;
	public static final boolean IS_AT_LEAST_JAVA_6;
	public static final boolean IS_AT_LEAST_JAVA_7;
	public static final boolean IS_AT_LEAST_JAVA_8;
	public static final boolean IS_AT_LEAST_JAVA_9;

	static {
		JAVA_VERSION = System.getProperty("java.version");

		IS_JAVA_5 = JAVA_VERSION.startsWith("1.5.");
		IS_JAVA_6 = JAVA_VERSION.startsWith("1.6.");
		IS_JAVA_7 = JAVA_VERSION.startsWith("1.7.");
		IS_JAVA_8 = JAVA_VERSION.startsWith("1.8.");
		IS_JAVA_9 = JAVA_VERSION.startsWith("1.9.");

		IS_AT_LEAST_JAVA_9 = IS_JAVA_9;
		IS_AT_LEAST_JAVA_8 = IS_JAVA_8 || IS_AT_LEAST_JAVA_9;
		IS_AT_LEAST_JAVA_7 = IS_JAVA_7 || IS_AT_LEAST_JAVA_8;
		IS_AT_LEAST_JAVA_6 = IS_JAVA_6 || IS_AT_LEAST_JAVA_7;
		IS_AT_LEAST_JAVA_5 = IS_JAVA_5 || IS_AT_LEAST_JAVA_6;
	}
}
