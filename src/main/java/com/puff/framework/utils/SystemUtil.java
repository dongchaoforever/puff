package com.puff.framework.utils;

import java.io.File;

public class SystemUtil {

	public static final String JAVA_HOME = System.getProperty("java.home");
	public static final String JAVA_IO_TMPDIR = System.getProperty("java.io.tmpdir");
	public static final String OS_NAME = System.getProperty("os.name");
	public static final String USER_DIR = System.getProperty("user.dir");
	public static final String FILE_ENCODING = System.getProperty("file.encoding");
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String PATH_SEPARATOR = System.getProperty("path.separator");

	public static final boolean IS_OS_WINDOWS = (File.separatorChar == '\\');
	public static final boolean IS_OS_UNIX = (File.separatorChar == '/');
	/***** Java运行时环境信息 *****/
	// Java 运行时环境规范名称

	public final static String SPECIFICATION_NAME = System.getProperty("java.specification.name");
	// Java 运行时环境版本

	public final static String VERSION = System.getProperty("java.version");
	// Java 运行时环境规范版本

	public final static String SPECIFICATION_VERSION = System.getProperty("java.specification.version");
	// Java 运行时环境供应商

	public final static String VENDOR = System.getProperty("java.vendor");
	// Java 运行时环境规范供应商

	public final static String SPECIFICATION_VENDOR = System.getProperty("java.specification.vendor");
	// Java 供应商的 URL

	public final static String VENDOR_URL = System.getProperty("java.vendor.url");

	// 加载库时搜索的路径列表

	public final static String LIBRARY_PATH = System.getProperty("java.library.path");
	// 要使用的 JIT 编译器的名称

	public final static String COMPILER = System.getProperty("java.compiler");
	// 一个或多个扩展目录的路径

	public final static String EXT_DIRS = System.getProperty("java.ext.dirs");

	/***** Java虚拟机信息 *****/
	// Java 虚拟机实现名称

	public final static String VM_NAME = System.getProperty("java.vm.name");
	// Java 虚拟机规范名称

	public final static String VM_SPECIFICATION_NAME = System.getProperty("java.vm.specification.name");
	// Java 虚拟机实现版本

	public final static String VM_VERSION = System.getProperty("java.vm.version");
	// Java 虚拟机规范版本

	public final static String VM_SPECIFICATION_VERSION = System.getProperty("java.vm.specification.version");
	// Java 虚拟机实现供应商

	public final static String VM_VENDEOR = System.getProperty("java.vm.vendor");
	// Java 虚拟机规范供应商

	public final static String VM_SPECIFICATION_VENDOR = System.getProperty("java.vm.specification.vendor");

	/***** Java类信息 *****/
	// Java 类格式版本号

	public final static String CLASS_VERSION = System.getProperty("java.class.version");
	// A

	public final static String CLASS_PATH = System.getProperty("java.class.path");

	/***** OS信息 *****/
	// 操作系统的架构

	public final static String OS_ARCH = System.getProperty("os.arch");
	// 操作系统的版本

	public final static String OS_VERSION = System.getProperty("os.version");
	// 文件分隔符（在 UNIX 系统中是“/”）

	public final static String FILE_SEPRATOR = System.getProperty("file.separator");
	// 路径分隔符（在 UNIX 系统中是“:”）

	public final static String PATH_SEPRATOR = System.getProperty("path.separator");
	// 行分隔符（在 UNIX 系统中是“\n”）

	public final static String LINE_SEPRATOR = System.getProperty("line.separator");

}
