package com.puff.log.loggers;

public class NoLog extends AbstractLog {

	private int infoCount;
	private int errorCount;
	private int warnCount;
	private int debugCount;
	private String loggerName;

	public NoLog(String loggerName) {
		this.loggerName = loggerName;
	}

	public String getLoggerName() {
		return this.loggerName;
	}

	public boolean isDebugEnabled() {
		return false;
	}

	public int getErrorCount() {
		return errorCount;
	}

	@Override
	public int getWarnCount() {
		return warnCount;
	}

	@Override
	public void resetStat() {
		errorCount = 0;
		warnCount = 0;
		infoCount = 0;
		debugCount = 0;
	}

	@Override
	public boolean isInfoEnabled() {
		return false;
	}

	@Override
	public boolean isWarnEnabled() {
		return false;
	}

	public int getInfoCount() {
		return infoCount;
	}

	public int getDebugCount() {
		return debugCount;
	}

	@Override
	public void debug(Object message, Object... args) {
		debugCount++;
	}

	@Override
	public void debug(Object message, Throwable t, Object... args) {
		debugCount++;
	}

	@Override
	public void info(Object message, Object... args) {
		infoCount++;
	}

	@Override
	public void info(Object message, Throwable t, Object... args) {
		infoCount++;
	}

	@Override
	public void warn(Object message, Object... args) {
		warnCount++;
	}

	@Override
	public void warn(Object message, Throwable t, Object... args) {
		warnCount++;
	}

	@Override
	public void error(Object message, Object... args) {
		errorCount++;
		String s = format(message, args);
		if (s != null) {
			System.err.println(loggerName + " : " + s);
		}
	}

	@Override
	public void error(Object message, Throwable t, Object... args) {
		error(message, args);
		if (t != null) {
			t.printStackTrace();
		}
	}

}
