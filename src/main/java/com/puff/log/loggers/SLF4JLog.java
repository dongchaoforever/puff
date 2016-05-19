package com.puff.log.loggers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

public class SLF4JLog extends AbstractLog {
	private static final String callerFQCN = SLF4JLog.class.getName();
	private static final Logger testLogger = LoggerFactory.getLogger(SLF4JLog.class);

	static {
		if (!(testLogger instanceof LocationAwareLogger)) {
			throw new UnsupportedOperationException(testLogger.getClass() + " is not a suitable logger");
		}
	}

	private int errorCount;
	private int warnCount;
	private int infoCount;
	private int debugCount;
	private LocationAwareLogger logger;

	public SLF4JLog(LocationAwareLogger log) {
		this.logger = log;
	}

	public SLF4JLog(String name) {
		logger = (LocationAwareLogger) LoggerFactory.getLogger(name);
	}

	private void log(int debugInt, String msg) {
		logger.log(null, callerFQCN, debugInt, msg, null, null);
	}

	private void log(int debugInt, String msg, Throwable t) {
		logger.log(null, callerFQCN, debugInt, msg, null, t);
	}

	@Override
	public void debug(Object message, Object... args) {
		log(LocationAwareLogger.DEBUG_INT, format(message, args));
		debugCount++;
	}

	@Override
	public void debug(Object message, Throwable t, Object... args) {
		log(LocationAwareLogger.DEBUG_INT, format(message, args), t);
		debugCount++;
	}

	@Override
	public void info(Object message, Object... args) {
		log(LocationAwareLogger.INFO_INT, format(message, args));
		infoCount++;
	}

	@Override
	public void info(Object message, Throwable t, Object... args) {
		log(LocationAwareLogger.INFO_INT, format(message, args), t);
		infoCount++;
	}

	@Override
	public void warn(Object message, Object... args) {
		log(LocationAwareLogger.WARN_INT, format(message, args));
		warnCount++;
	}

	@Override
	public void warn(Object message, Throwable t, Object... args) {
		log(LocationAwareLogger.WARN_INT, format(message, args), t);
		warnCount++;
	}

	@Override
	public void error(Object message, Object... args) {
		log(LocationAwareLogger.ERROR_INT, format(message, args));
		errorCount++;
	}

	@Override
	public void error(Object message, Throwable t, Object... args) {
		log(LocationAwareLogger.ERROR_INT, format(message, args), t);
		errorCount++;
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
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
