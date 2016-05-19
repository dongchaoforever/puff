package com.puff.framework.utils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public final class VMUtil {

	public static String getProcessId() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		return StringUtil.substringBefore(name, "@");
	}

	public static String getThreadDump() {
		if (!JdkUtil.IS_AT_LEAST_JAVA_6) {
			return "Java AppVersionUtils must be equal or larger than 1.6";
		}
		String jstack = "../bin/jstack";
		if (SystemUtil.IS_OS_WINDOWS) {
			jstack = jstack + ".exe";
		}

		File jstackFile = new File(System.getProperty("java.home"), jstack);
		String command = jstackFile.getAbsolutePath() + " " + getProcessId();
		ShellUtil.Result result = ShellUtil.shell(command);
		if (result.success()) {
			return result.stdout();
		}
		throw new IllegalStateException(result.getException());
	}

	public static boolean detectDeadlock() {
		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		long[] threadIds = threadBean.findDeadlockedThreads();
		return (threadIds != null && threadIds.length > 0);
	}

	public static void main(String[] args) {
		System.out.println(getThreadDump());
	}
}
