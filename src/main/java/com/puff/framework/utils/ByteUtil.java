package com.puff.framework.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * 一些字节处理的方便方法。
 * 
 */
public class ByteUtil {

	/**
	 * 构造新字节时需要与的值表
	 */
	private static final byte[] BUILD_BYTE_TABLE = new byte[] { (byte) 128, (byte) 64, (byte) 32, (byte) 16, (byte) 8, (byte) 4, (byte) 2, (byte) 1 };

	private ByteUtil() {
	}

	/**
	 * short转换到字节数组
	 * 
	 * @param number
	 *            需要转换的数据。
	 * @return 转换后的字节数组。
	 */
	public static byte[] shortToByte(short number) {
		byte[] b = new byte[2];
		for (int i = 1; i >= 0; i--) {
			b[i] = (byte) (number % 256);
			number >>= 8;
		}
		return b;
	}

	/**
	 * 字节到short转换
	 * 
	 * @param b
	 *            short的字节数组
	 * @return short数值。
	 */
	public static short byteToShort(byte[] b) {
		return (short) ((((b[0] & 0xff) << 8) | b[1] & 0xff));
	}

	/**
	 * 整型转换到字节数组
	 * 
	 * @param number
	 *            整形数据。
	 * @return 整形数据的字节数组。
	 */
	public static byte[] intToByte(int number) {
		byte[] b = new byte[4];
		for (int i = 3; i >= 0; i--) {
			b[i] = (byte) (number % 256);
			number >>= 8;
		}
		return b;
	}

	/**
	 * 字节数组到整型转换
	 * 
	 * @param b
	 *            整形数据的字节数组。
	 * @return 字节数组转换成的整形数据。
	 */
	public static int byteToInt(byte[] b) {
		return ((((b[0] & 0xff) << 24) | ((b[1] & 0xff) << 16) | ((b[2] & 0xff) << 8) | (b[3] & 0xff)));
	}

	/**
	 * long转换到字节数组
	 * 
	 * @param number
	 *            长整形数据。
	 * @return 长整形转换成的字节数组。
	 */
	public static byte[] longToByte(long number) {
		byte[] b = new byte[8];
		for (int i = 7; i >= 0; i--) {
			b[i] = (byte) (number % 256);
			number >>= 8;
		}
		return b;
	}

	/**
	 * 字节数组到整型的转换
	 * 
	 * @param b
	 *            长整形字节数组。
	 * @return 长整形数据。
	 */
	public static long byteToLong(byte[] b) {
		return ((((long) b[0] & 0xff) << 56) | (((long) b[1] & 0xff) << 48) | (((long) b[2] & 0xff) << 40) | (((long) b[3] & 0xff) << 32) | (((long) b[4] & 0xff) << 24)
				| (((long) b[5] & 0xff) << 16) | (((long) b[6] & 0xff) << 8) | ((long) b[7] & 0xff));
	}

	/**
	 * double转换到字节数组
	 * 
	 * @param d
	 *            双精度浮点。
	 * @return 双精度浮点的字节数组。
	 */
	public static byte[] doubleToByte(double d) {
		byte[] bytes = new byte[8];
		long l = Double.doubleToLongBits(d);
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = Long.valueOf(l).byteValue();
			l = l >> 8;
		}
		return bytes;
	}

	/**
	 * 字节数组到double转换
	 * 
	 * @param b
	 *            双精度浮点字节数组。
	 * @return 双精度浮点数据。
	 */
	public static double byteToDouble(byte[] b) {
		long l;
		l = b[0];
		l &= 0xff;
		l |= ((long) b[1] << 8);
		l &= 0xffff;
		l |= ((long) b[2] << 16);
		l &= 0xffffff;
		l |= ((long) b[3] << 24);
		l &= 0xffffffffl;
		l |= ((long) b[4] << 32);
		l &= 0xffffffffffl;

		l |= ((long) b[5] << 40);
		l &= 0xffffffffffffl;
		l |= ((long) b[6] << 48);
		l &= 0xffffffffffffffl;

		l |= ((long) b[7] << 56);

		return Double.longBitsToDouble(l);
	}

	/**
	 * float转换到字节数组
	 * 
	 * @param d
	 *            浮点型数据。
	 * @return 浮点型数据转换后的字节数组。
	 */
	public static byte[] floatToByte(float d) {
		byte[] bytes = new byte[4];
		int l = Float.floatToIntBits(d);
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = Integer.valueOf(l).byteValue();
			l = l >> 8;
		}
		return bytes;
	}

	/**
	 * 字节数组到float的转换
	 * 
	 * @param b
	 *            浮点型数据字节数组。
	 * @return 浮点型数据。
	 */
	public static float byteToFloat(byte[] b) {
		int l;
		l = b[0];
		l &= 0xff;
		l |= ((long) b[1] << 8);
		l &= 0xffff;
		l |= ((long) b[2] << 16);
		l &= 0xffffff;
		l |= ((long) b[3] << 24);
		l &= 0xffffffffl;

		return Float.intBitsToFloat(l);
	}

	/**
	 * 字符串到字节数组转换
	 * 
	 * @param s
	 *            字符串。
	 * @param charset
	 *            字符编码
	 * @return 字符串按相应字符编码编码后的字节数组。
	 */
	public static byte[] stringToByte(String s, Charset charset) {
		return s.getBytes(charset);
	}

	/**
	 * 字节数组带字符串的转换
	 * 
	 * @param b
	 *            字符串按指定编码转换的字节数组。
	 * @param charset
	 *            字符编码。
	 * @return 字符串。
	 */
	public static String byteToString(byte[] b, Charset charset) {
		return new String(b, charset);
	}

	/**
	 * 对象转换成字节数组。
	 * 
	 * @param obj
	 *            字节数组。
	 * @return 对象实例相应的序列化后的字节数组。
	 * @throws IOException
	 */
	public static byte[] objectToByte(Object obj) throws IOException {
		ByteArrayOutputStream buff = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(buff);
		out.writeObject(obj);
		try {
			return buff.toByteArray();
		} finally {
			out.close();
		}
	}

	/**
	 * 序死化字节数组转换成实际对象。
	 * 
	 * @param b
	 *            字节数组。
	 * @return 对象。
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object byteToObject(byte[] b) throws IOException, ClassNotFoundException {
		ByteArrayInputStream buff = new ByteArrayInputStream(b);
		ObjectInputStream in = new ObjectInputStream(buff);
		Object obj = in.readObject();
		try {
			return obj;
		} finally {
			in.close();
		}
	}

	/**
	 * 比较两个字节的每一个bit位是否相等.
	 * 
	 * @param a
	 *            比较的字节.
	 * @param b
	 *            比较的字节
	 * @return ture 两个字节每一位都相等,false有至少一位不相等.
	 */
	public static boolean equalsBit(byte a, byte b) {
		return Arrays.equals(byteToBitArray(a), byteToBitArray(b));
	}

	/**
	 * 比较两个数组中的每一个字节,两个字节必须二进制字节码每一位都相同才表示两个 byte相同.
	 * 
	 * @param a
	 *            比较的字节数组.
	 * @param b
	 *            被比较的字节数.
	 * @return ture每一个元素的每一位两个数组都是相等的,false至少有一位不相等.
	 */
	public static boolean equalsBit(byte[] a, byte[] b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}

		int length = a.length;
		if (b.length != length) {
			return false;
		}

		for (int count = 0; count < a.length; count++) {
			if (!equalsBit(a[count], b[count])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 返回某个字节的bit组成的字符串.
	 * 
	 * @param b
	 *            字节.
	 * @return Bit位组成的字符串.
	 */
	public static String bitString(byte b) {
		StringBuilder buff = new StringBuilder();
		boolean[] array = byteToBitArray(b);
		for (int i = 0; i < array.length; i++) {
			buff.append(array[i] ? 1 : 0);
		}
		return buff.toString();
	}

	/**
	 * 计算出给定byte中的每一位,并以一个布尔数组返回. true表示为1,false表示为0.
	 * 
	 * @param b
	 *            字节.
	 * @return 指定字节的每一位bit组成的数组.
	 */
	public static boolean[] byteToBitArray(byte b) {
		boolean[] buff = new boolean[8];
		int index = 0;
		for (int i = 7; i >= 0; i--) {
			buff[index++] = ((b >>> i) & 1) == 1;
		}
		return buff;
	}

	/**
	 * 返回指定字节中指定bit位,true为1,false为0. 指定的位从0-7,超出将抛出数据越界异常.
	 *
	 * @param b
	 *            需要判断的字节.
	 * @param index
	 *            字节中指定位.
	 * @return 指定位的值.
	 */
	public static boolean byteBitValue(byte b, int index) {
		return byteToBitArray(b)[index];
	}

	/**
	 * 根据布尔数组表示的二进制构造一个新的字节.
	 * 
	 * @param values
	 *            布尔数组,其中true表示为1,false表示为0.
	 * @return 构造的新字节.
	 */
	public static byte buildNewByte(boolean[] values) {
		byte b = 0;
		for (int i = 0; i < 8; i++) {
			if (values[i]) {
				b |= BUILD_BYTE_TABLE[i];
			}
		}
		return b;
	}

	/**
	 * 将指定字节中的某个bit位替换成指定的值,true代表1,false代表0.
	 * 
	 * @param b
	 *            需要被替换的字节.
	 * @param index
	 *            位的序号,从0开始.超过7将抛出越界异常.
	 * @param newValue
	 *            新的值.
	 * @return 替换好某个位值的新字节.
	 */
	public static byte changeByteBitValue(byte b, int index, boolean newValue) {
		boolean[] bitValues = byteToBitArray(b);
		bitValues[index] = newValue;
		return buildNewByte(bitValues);
	}

	/**
	 * 将指定的IP地址转换成字节表示方式. IP数组的每一个数字都不能大于255,否则将抛出IllegalArgumentException异常.
	 *
	 * @param ipNums
	 *            IP地址数组.
	 * @return IP地址字节表示方式.
	 */
	public static byte[] ipAddressBytes(String address) {
		if (address == null || address.length() < 0 || address.length() > 15) {
			throw new IllegalArgumentException("Invalid IP address.");
		}

		final int ipSize = 4;// 最大IP位数
		final char ipSpace = '.';// IP数字的分隔符
		int[] ipNums = new int[ipSize];
		StringBuilder number = new StringBuilder();// 当前操作的数字
		StringBuilder buff = new StringBuilder(address);
		int point = 0;// 当前操作的数字下标,最大到3.
		char currentChar;
		for (int i = 0; i < buff.length(); i++) {
			currentChar = buff.charAt(i);
			if (ipSpace == currentChar) {
				// 当前位置等于最大于序号后,还有字符没有处理表示这是一个错误的IP.
				if (point == ipSize - 1 && buff.length() - (i + 1) > 0) {
					throw new IllegalArgumentException("Invalid IP address.");
				}
				ipNums[point++] = Integer.parseInt(number.toString());
				number.delete(0, number.length());
			} else {
				number.append(currentChar);
			}
		}
		ipNums[point] = Integer.parseInt(number.toString());

		byte[] ipBuff = new byte[ipSize];
		int pointNum = 0;
		for (int i = 0; i < 4; i++) {
			pointNum = Math.abs(ipNums[i]);
			if (pointNum > 255) {
				throw new IllegalArgumentException("Invalid IP address.");
			}
			ipBuff[i] = (byte) (pointNum & 0xff);
		}

		return ipBuff;
	}
}
