package com.puff.plugin.cache.sync;

import java.net.URL;
import java.util.List;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.plugin.msg.Command;
import com.puff.plugin.msg.CommandFactory;
import com.puff.plugin.msg.CommandHandler;

public class JGroupsCacheSync extends ReceiverAdapter implements CacheSync {

	private final static Log log = LogFactory.get();
	private static final String channelName = "MutliCache-Channel";
	private static final String configXml = "/network.xml";
	private JChannel channel;

	public JGroupsCacheSync() {
		super();
	}

	@Override
	public CacheSync init() {
		System.setProperty("java.net.preferIPv4Stack", "true");
		try {
			long start = System.currentTimeMillis();
			URL xml = JGroupsCacheSync.class.getResource(configXml);
			if (xml == null) {
				xml = getClass().getClassLoader().getParent().getResource(configXml);
			}
			channel = new JChannel(xml);
			channel.setReceiver(this);
			channel.connect(channelName);
			log.info("Connected to JGroups channel: " + channelName + " success. time " + (System.currentTimeMillis() - start) + " ms.");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	@Override
	public void sendCommand(Command command) {
		try {
			channel.send(new Message().setBuffer(command.toBuffers()));
		} catch (Exception e) {
			log.error("send command fail ...", e);
		}
	}

	/**
	 * 接收消息
	 */
	@Override
	public void receive(Message msg) {
		byte[] buffers = msg.getBuffer();
		// 忽略消息
		if (buffers == null || buffers.length < 1) {
			log.warn("Message is empty.");
			return;
		}
		if (!msg.getSrc().equals(channel.getAddress())) {
			try {
				Command cmd = Command.parse(buffers);
				if (cmd != null) {
					CommandHandler handler = CommandFactory.getCommand(String.valueOf(cmd.nameSpace));
					if (handler != null) {
						handler.handle(cmd);
					}
				}
			} catch (Exception e) {
				log.error("Handel received msg error...", e);
			}
		}
	}

	/**
	 * 组中成员变化时
	 */
	@Override
	public void viewAccepted(View view) {
		List<Address> addrs = view.getMembers();
		int size = addrs.size();
		StringBuilder sb = new StringBuilder("Group Members Changed, Size[" + size + "]. List : ");
		for (int i = 0; i < size; i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append(addrs.get(i).toString());
		}
		log.info(sb.toString());
	}

}
