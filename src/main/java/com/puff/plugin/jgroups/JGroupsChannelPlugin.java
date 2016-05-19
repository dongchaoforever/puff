package com.puff.plugin.jgroups;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.plugin.Plugin;
import com.puff.plugin.msg.Command;
import com.puff.plugin.msg.CommandFactory;
import com.puff.plugin.msg.CommandHandler;

public class JGroupsChannelPlugin extends ReceiverAdapter implements Plugin {

	private static final Log log = LogFactory.get();

	private String channelName;
	private String configXml = "/network.xml";
	private JChannel channel;

	@Override
	public void init(Properties prop) {
		channelName = prop.getProperty("channelName", "puff-jgroup-channel");
		configXml = prop.getProperty("configXml", "/network.xml");
	}

	@Override
	public boolean start() {
		System.setProperty("java.net.preferIPv4Stack", "true");
		try {
			long ct = System.currentTimeMillis();
			URL xml = JGroupsChannelPlugin.class.getResource(configXml);
			if (xml == null) {
				xml = getClass().getClassLoader().getParent().getResource(configXml);
			}
			channel = new JChannel(xml);
			channel.setReceiver(this);
			channel.connect(channelName);
			log.info("Connected to jgroup channel: " + channelName + " success. time " + (System.currentTimeMillis() - ct) + " ms.");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean stop() {
		if (channel != null) {
			channel.close();
		}
		return true;
	}

	public void bindCommandHandler(CommandHandler handler) {
		CommandFactory.bindCommand(handler);
	}

	/**
	 * 发送广播命令
	 * 
	 */
	public void sendCmd(Command cmd) {
		try {
			Message msg = new Message();
			msg.setBuffer(cmd.toBuffers());
			channel.send(msg);
		} catch (Exception e) {
			log.error("Can not send cmd ...", e);
		}
	}

	/**
	 * 消息接收
	 */
	@Override
	public void receive(Message msg) {
		byte[] buffers = msg.getBuffer();
		// 无效消息
		if (buffers == null || buffers.length < 1) {
			log.warn("Message is empty.");
			return;
		}
		// 不处理发送给自己的消息
		if (msg.getSrc().equals(channel.getAddress()))
			return;
		try {
			Command cmd = Command.parse(buffers);
			if (cmd != null) {
				CommandHandler handler = CommandFactory.getCommand(String.valueOf(cmd.nameSpace));
				if (handler != null) {
					handler.handle(cmd);
				}
			}
		} catch (Exception e) {
			log.error("Unable to handle received msg", e);
		}
	}

	/**
	 * 组中成员变化时
	 */
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
