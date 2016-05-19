package com.puff.framework.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public enum PathUtil {
	INSTANCE;
	private static String webRootPath;

	@SuppressWarnings("rawtypes")
	public static String getPath(Class clazz) {
		String path = clazz.getResource("").getPath();
		return new File(path).getAbsolutePath();
	}

	public static String getPath(Object object) {
		String path = getPath(object.getClass());
		return new File(path).getAbsolutePath();
	}

	public static String getClassPath() {
		String path = PathUtil.class.getClassLoader().getResource("").getPath();
		return new File(path).getAbsolutePath();
	}

	public static String getPackagePath(Object object) {
		Package p = object.getClass().getPackage();
		return p != null ? p.getName().replaceAll("\\.", "/") : "";
	}

	public static String getWebRootPath() {
		return webRootPath == null ? detectWebRootPath() : webRootPath;
	}

	public static void setWebRootPath(String rootPath) {
		if (rootPath.endsWith(File.separator)) {
			rootPath = rootPath.substring(0, rootPath.length() - 1);
		}
		PathUtil.webRootPath = rootPath;
	}

	private static String detectWebRootPath() {
		try {
			String path = PathUtil.class.getResource("/").getFile();
			return new File(path).getParentFile().getParentFile().getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static InputStream fromClassPath(String fileName) {
		if (!fileName.startsWith("/"))
			fileName = "/" + fileName;
		return PathUtil.class.getResourceAsStream(fileName);
	}

	public static InputStream fromJar(String fileName) {
		return PathUtil.class.getClassLoader().getResourceAsStream(fileName);
	}

	public static String toAbsolutePath(String path) {
		if (StringUtil.empty(path)) {
			return null;
		}
		File file = new File(path);
		if ((!file.exists()) || (path.startsWith("classpath:"))) {
			file = getClasspathFile(path);
			return file.getAbsolutePath();
		}
		return path;
	}

	public static File getClasspathFile(String classpath) {
		String input = classpath;
		if (classpath.startsWith("classpath:")) {
			input = classpath.substring(10);
		}
		URL url = getDefaultClassLoader().getResource(input);
		if ((url == null) || (!url.getProtocol().equals("file"))) {
			return null;
		}
		return toFile(url);
	}

	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) {
		}
		if (cl == null) {
			cl = PathUtil.class.getClassLoader();
		}
		return cl;
	}

	public static File toFile(URL url) {
		if ((url == null) || (!url.getProtocol().equals("file"))) {
			return null;
		}
		String filename = url.getFile().replace('/', File.separatorChar);
		int pos = 0;
		while ((pos = filename.indexOf('%', pos)) >= 0) {
			if (pos + 2 < filename.length()) {
				String hexStr = filename.substring(pos + 1, pos + 3);
				char ch = (char) Integer.parseInt(hexStr, 16);
				filename = filename.substring(0, pos) + ch + filename.substring(pos + 3);
			}
		}

		return new File(filename);
	}

	public static void main(String[] args) {
		System.out.println(PathUtil.getClassPath());
		System.out.println(detectWebRootPath());
	}

}
