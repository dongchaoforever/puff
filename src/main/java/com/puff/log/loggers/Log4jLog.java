package com.puff.log.loggers;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Log4jLog extends AbstractLog {

	private static final String callerFQCN = Log4jLog.class.getName();

	private org.apache.log4j.Logger logger;

	private int errorCount;
	private int warnCount;
	private int infoCount;
	private int debugCount;

	public Log4jLog(Logger logger) {
		this.logger = logger;
	}

	public Log4jLog(String name) {
		logger = Logger.getLogger(name);
	}

	public Logger getLog() {
		return logger;
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public void debug(Object message, Object... args) {
		debugCount++;
		logger.log(callerFQCN, Level.DEBUG, format(message, args), null);

	}

	@Override
	public void debug(Object message, Throwable t, Object... args) {
		debugCount++;
		logger.log(callerFQCN, Level.DEBUG, format(message, args), t);
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public void info(Object message, Object... args) {
		infoCount++;
		logger.log(callerFQCN, Level.INFO, format(message, args), null);
	}

	@Override
	public void info(Object message, Throwable t, Object... args) {
		infoCount++;
		logger.log(callerFQCN, Level.INFO, format(message, args), t);
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isEnabledFor(Level.WARN);
	}

	@Override
	public void warn(Object message, Object... args) {
		logger.log(callerFQCN, Level.WARN, format(message, args), null);
		warnCount++;
	}

	@Override
	public void warn(Object message, Throwable t, Object... args) {
		logger.log(callerFQCN, Level.WARN, format(message, args), t);
		warnCount++;
	}

	@Override
	public void error(Object message, Object... args) {
		errorCount++;
		logger.log(callerFQCN, Level.ERROR, format(message, args), null);

	}

	@Override
	public void error(Object message, Throwable t, Object... args) {
		errorCount++;
		logger.log(callerFQCN, Level.ERROR, format(message, args), t);

	}

	@Override
	public int getErrorCount() {
		return errorCount;
	}

	@Override
	public int getWarnCount() {
		return warnCount;
	}

	@Override
	public int getInfoCount() {
		return infoCount;
	}

	@Override
	public int getDebugCount() {
		return debugCount;
	}

	@Override
	public void resetStat() {
		errorCount = 0;
		warnCount = 0;
		infoCount = 0;
		debugCount = 0;
	}

}
