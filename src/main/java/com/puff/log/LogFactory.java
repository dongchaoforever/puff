package com.puff.log;

import java.lang.reflect.Constructor;

import com.puff.log.loggers.NoLog;

@SuppressWarnings("rawtypes")
public class LogFactory {

	private static Constructor logConstructor;

	static {
		tryImplementation("org.slf4j.Logger", "com.puff.log.loggers.SLF4JLog");
		tryImplementation("org.apache.log4j.Logger", "com.puff.log.loggers.Log4jLog");
		tryImplementation("java.util.logging.Logger", "com.puff.log.loggers.JDKLog");
		if (logConstructor == null) {
			try {
				logConstructor = NoLog.class.getConstructor(String.class);
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void tryImplementation(String testClassName, String implClassName) {
		if (logConstructor != null) {
			return;
		}
		try {
			Class.forName(testClassName);
			Class implClass = Class.forName(implClassName);
			logConstructor = implClass.getConstructor(new Class[] { String.class });

			Class<?> declareClass = logConstructor.getDeclaringClass();
			if (!Log.class.isAssignableFrom(declareClass)) {
				logConstructor = null;
			}

			try {
				if (null != logConstructor) {
					logConstructor.newInstance(LogFactory.class.getName());
				}
			} catch (Throwable t) {
				logConstructor = null;
			}

		} catch (Throwable t) {
			// skip
		}
	}

	public static Log get(Class clazz) {
		return get(clazz.getName());
	}

	public static Log get(String loggerName) {
		try {
			return (Log) logConstructor.newInstance(loggerName);
		} catch (Throwable t) {
			throw new RuntimeException("Error creating logger for logger '" + loggerName + "'.  Cause: " + t, t);
		}
	}

	@SuppressWarnings("unchecked")
	public static synchronized void selectLog4JLogging() {
		try {
			Class.forName("org.apache.log4j.Logger");
			Class implClass = Class.forName("com.puff.log.loggers.Log4jLog");
			logConstructor = implClass.getConstructor(new Class[] { String.class });
		} catch (Throwable t) {
			// ignore
		}
	}

	@SuppressWarnings("unchecked")
	public static synchronized void selectJavaLogging() {
		try {
			Class.forName("java.util.logging.Logger");
			Class implClass = Class.forName("com.puff.log.loggers.JDKLog");
			logConstructor = implClass.getConstructor(new Class[] { String.class });
		} catch (Throwable t) {
			// ignore
		}
	}

	public static Log get() {
		String className = Thread.currentThread().getStackTrace()[2].getClassName();
		return get(className);
	}
}
