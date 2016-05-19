package com.puff.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

public class ExceptionUtil {
	public static void throwRunTimeWhen(boolean flag, String message, Object... args) {
		if (flag) {
			message = String.format(message, args);
			RuntimeException e = new RuntimeException(message);
			throw correctStackTrace(e);
		}
	}

	public static void throwRuntime(Throwable cause) {
		throw cause instanceof RuntimeException ? (RuntimeException) cause : new RuntimeException(cause);
	}

	private static RuntimeException correctStackTrace(RuntimeException e) {
		StackTraceElement[] s = e.getStackTrace();
		e.setStackTrace(Arrays.copyOfRange(s, 1, s.length));
		return e;
	}

	public static String getStackTrace(Throwable aThrowable) {
		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

}
