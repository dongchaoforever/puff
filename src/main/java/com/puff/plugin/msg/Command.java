package com.puff.plugin.msg;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import com.puff.framework.utils.SerializeUtil;
import com.puff.log.Log;
import com.puff.log.LogFactory;

/**
 * 命令消息封装
 * 格式：
 * 第1个字节为nameSpace，长度1 [NS]
 * 第2个字节为operator，长度1 [OPT]
 * 第3、4个字节为cacheName长度，长度2 [R_LEN]
 * 第5、N 为 cacheName 值，长度为 [R_LEN]
 * 第N+1、N+2 为 key 长度，长度2 [K_LEN]
 * 第N+3、M为 key值，长度为 [K_LEN]
 * 
 * @author dongchao
 */
public class Command {

	private final static Log log = LogFactory.get();

	public final static byte DEFAUTL_NS = 0x00; // 默认命名空间
	public final static byte OPT_PUT = 0x01; // 新增缓存
	public final static byte OPT_REMOVE = 0x02; // 删除缓存
	public final static byte OPT_CLEAR = 0x03; // 清除缓存

	public final byte nameSpace;
	public final byte operator;
	public final String cacheName;
	public final Object key;
	public final String src;

	private static String charSet = "UTF-8";
	private static final String UUID = String.valueOf(System.currentTimeMillis() + new Random().nextInt(Integer.MAX_VALUE));
	private static byte[] srcBuffers;

	static {
		try {
			srcBuffers = UUID.getBytes(charSet);
		} catch (Exception e) {
		}
	}

	public Command(byte nameSpace, byte o, String cacheName, Object k) {
		this.nameSpace = nameSpace;
		this.operator = o;
		this.cacheName = cacheName;
		this.key = k;
		this.src = UUID;
	}

	public Command(byte nameSpace, byte o, String r, Object k, String src) {
		this.nameSpace = nameSpace;
		this.operator = o;
		this.cacheName = r;
		this.key = k;
		this.src = src;
	}

	public byte[] toBuffers() {
		byte[] keyBuffers = SerializeUtil.serialize(key);
		int r_len;
		try {
			byte[] bCacheName = cacheName.getBytes(charSet);
			r_len = bCacheName.length;
			int k_len = keyBuffers.length;
			int src_len = srcBuffers.length;
			byte[] buffers = new byte[6 + r_len + k_len + src_len];
			int idx = 0;
			buffers[idx] = nameSpace;
			buffers[++idx] = operator;
			buffers[++idx] = (byte) (r_len >> 8);
			buffers[++idx] = (byte) (r_len & 0xFF);
			System.arraycopy(bCacheName, 0, buffers, ++idx, r_len);
			idx += r_len;
			buffers[idx++] = (byte) (k_len >> 8);
			buffers[idx++] = (byte) (k_len & 0xFF);
			System.arraycopy(keyBuffers, 0, buffers, idx, k_len);
			System.arraycopy(srcBuffers, 0, buffers, idx + k_len, src_len);
			return buffers;
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	public boolean msgFromSlef() {
		return UUID.equals(src);
	}

	public static Command parse(byte[] buffers) {
		Command cmd = null;
		try {
			int idx = 0;
			byte nameSpace = buffers[idx];
			byte opt = buffers[++idx];
			int r_len = buffers[++idx] << 8;
			r_len += buffers[++idx];
			if (r_len > 0) {
				String cacheName = new String(buffers, ++idx, r_len, charSet);
				String src = new String(buffers, buffers.length - 13, 13, charSet);
				idx += r_len;
				int k_len = buffers[idx++] << 8;
				k_len += buffers[idx++];
				if (k_len > 0) {
					byte[] keyBuffers = new byte[k_len];
					System.arraycopy(buffers, idx, keyBuffers, 0, k_len);
					Object key = SerializeUtil.deserialize(keyBuffers);
					cmd = new Command(nameSpace, opt, cacheName, key, src);
				}
			}
		} catch (Exception e) {
			log.error("Unabled to parse received command.", e);
		}
		return cmd;
	}

	@Override
	public String toString() {
		return "Command [namespace=" + nameSpace + ", operator=" + operator + ", cacheName=" + cacheName + ", key=" + key + "]";
	}

	public static void main(String[] args) {
		Command c = new Command((byte) 0x99, OPT_PUT, "person", "name");

		byte[] buffers = c.toBuffers();

		Command command = Command.parse(buffers);

		System.out.println(command.msgFromSlef());

	}

	public static Command put(String cacheName, String key) {
		return new Command(DEFAUTL_NS, OPT_PUT, cacheName, key);
	}

	public static Command remove(String cacheName, Object key) {
		return new Command(DEFAUTL_NS, OPT_REMOVE, cacheName, key);
	}

	public static Command clear(String cacheName) {
		return new Command(DEFAUTL_NS, OPT_CLEAR, cacheName, "");
	}

}
