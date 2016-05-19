package com.puff.framework.utils;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class IdentityUtil {

	private static long tmpID = 0;

	private static boolean tmpIDlocked = false;

	private static SecureRandom random = new SecureRandom();

	private static final Random r = new Random();
	private static final int workerId = r.nextInt(30) + 1;
	private static final int datacenterId = r.nextInt(30) + 1;
	private static final IdWorker idWorker = new IdWorker(workerId, datacenterId);

	public static long getUID() {
		long value = 0;
		while (true) {
			if (tmpIDlocked == false) {
				tmpIDlocked = true;
				value = Long.valueOf(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()).toString()) * 100;
				if (tmpID < value) {
					tmpID = value;
				} else {
					tmpID = tmpID + 1;
					value = tmpID;
				}
				tmpIDlocked = false;
				return value;
			}
		}
	}

	public static String uuid32() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static String uuid16() {
		return RandomUtil.randomAlphanumeric(16);
	}

	public static String getObjectId() {
		return ObjectId.get().toHexString();
	}

	public static IdWorker idWorker() {
		return idWorker;
	}

	/**
	 * 使用SecureRandom随机生成Int.
	 */
	public static int randomInt() {
		return Math.abs(random.nextInt());
	}

	/**
	 * 使用SecureRandom随机生成Long.
	 */
	public static long randomLong() {
		return Math.abs(random.nextLong());
	}
}
