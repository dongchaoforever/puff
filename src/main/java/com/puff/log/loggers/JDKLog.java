package com.puff.log.loggers;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JDKLog extends AbstractLog {
	private Logger logger;

	private int errorCount;
	private int warnCount;
	private int infoCount;
	private int debugCount;

	private String loggerName;

	public JDKLog(String name) {
		this.loggerName = name;
		logger = Logger.getLogger(name);
	}

	public boolean isDebugEnabled() {
		return logger.isLoggable(Level.FINE);
	}

	private void log(Level level, String msg, Throwable ex) {
		logger.logp(level, loggerName, Thread.currentThread().getStackTrace()[1].getMethodName(), msg, ex);

	}

	public void debug(Object message, Object... args) {
		debugCount++;
		log(Level.FINE, format(message, args), null);
	}

	public void debug(Object message, Throwable t, Object... args) {
		debugCount++;
		log(Level.FINE, format(message, args), t);
	}

	public void info(Object message, Object... args) {
		log(Level.INFO, format(message, args), null);
		infoCount++;
	}

	public void info(Object message, Throwable t, Object... args) {
		log(Level.INFO, format(message, args), t);
		infoCount++;
	}

	public void warn(Object message, Object... args) {
		log(Level.WARNING, format(message, args), null);
		warnCount++;
	}

	public void warn(Object message, Throwable t, Object... args) {
		log(Level.WARNING, format(message, args), t);
		warnCount++;
	}

	public void error(Object message, Object... args) {
		log(Level.SEVERE, format(message, args), null);
		errorCount++;
	}

	public void error(Object message, Throwable t, Object... args) {
		log(Level.SEVERE, format(message, args), t);
		errorCount++;
	}

	@Override
	public int getWarnCount() {
		return warnCount;
	}

	@Override
	public int getErrorCount() {
		return errorCount;
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
		return logger.isLoggable(Level.INFO);
	}

	@Override
	public int getInfoCount() {
		return infoCount;
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isLoggable(Level.WARNING);
	}

	public int getDebugCount() {
		return debugCount;
	}

}
