package com.puff.log;

public interface Log {
	boolean isDebugEnabled();

	public void debug(Object message, Object... args);

	public void debug(Object message, Throwable t, Object... args);

	boolean isInfoEnabled();

	public void info(Object message, Object... args);

	public void info(Object message, Throwable t, Object... args);

	boolean isWarnEnabled();

	public void warn(Object message, Object... args);

	public void warn(Object message, Throwable t, Object... args);

	public void error(Object message, Object... args);

	public void error(Object message, Throwable t, Object... args);

	int getErrorCount();

	int getWarnCount();

	int getInfoCount();

	int getDebugCount();

	void resetStat();

}
