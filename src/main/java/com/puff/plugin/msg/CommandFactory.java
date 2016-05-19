package com.puff.plugin.msg;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
	private static Map<String, CommandHandler> map = new HashMap<String, CommandHandler>();

	public synchronized static void bindCommand(CommandHandler handler) {
		byte nameSpace = handler.getNameSpace();
		if (map.containsKey(String.valueOf(nameSpace))) {
			throw new IllegalArgumentException("the unique nameSpace :" + nameSpace + " is already exist , please use other nameSpace");
		}
		map.put(String.valueOf(nameSpace), handler);
	}

	public static CommandHandler getCommand(String nameSpace) {
		return map.get(nameSpace);
	}

}
