package com.puff.log.loggers;

import com.puff.framework.utils.StringUtil;
import com.puff.log.Log;

public abstract class AbstractLog implements Log {

	protected static String format(Object message, Object... args) {
		if (message == null) {
			return null;
		}
		if (args == null || args.length == 0) {
			return message.toString();
		} else {
			return StringUtil.replaceArgs(message.toString(), args);
		}
	}
}
