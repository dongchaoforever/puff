package com.puff.framework.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PackageSearch {

	private static InnerClassLoader _INNER_CLASS_LOADER = new InnerClassLoader(new URL[] {}, PackageSearch.class.getClassLoader());

	/**
	 * @return 返回默认类加载器对象
	 */
	public static ClassLoader getDefaultClassLoader() {
		return _INNER_CLASS_LOADER;
	}

	/**
	 * 在参数packageNames指定的包路径内，查找实现了由clazz指定的接口、注解或抽象类的类对象集合
	 * 
	 * @param clazz
	 * @param packageNames
	 * @param callingClass
	 * @return
	 */
	public static <T> Collection<Class<T>> findClassByClazz(Class<T> clazz, String packageNames, Class<?> callingClass) {
		List<Class<T>> _returnValue = new ArrayList<Class<T>>();
		try {
			Iterator<URL> _urls = ResourceUtil.getResources(packageNames.replaceAll("\\.", "/"), callingClass, true);
			while (_urls.hasNext()) {
				URL _url = _urls.next();
				__doProcessURL(_url, _returnValue, clazz, packageNames, callingClass);
			}
		} catch (Exception e) {

		}
		return _returnValue;
	}

	/**
	 * 在参数packageNames指定的包路径内，查找实现了由clazz指定的接口、注解或抽象类的类对象集合
	 * 
	 * @param clazz
	 * @param packageNames
	 * @param callingClass
	 * @return
	 */
	public static <T> Collection<Class<T>> findClassByClazz(Class<T> clazz, Collection<String> packageNames, Class<?> callingClass) {
		List<Class<T>> _returnValue = new ArrayList<Class<T>>();
		try {
			for (String _packageName : packageNames) {
				Iterator<URL> _urls = ResourceUtil.getResources(_packageName.replaceAll("\\.", "/"), callingClass, true);
				while (_urls.hasNext()) {
					URL _url = _urls.next();
					__doProcessURL(_url, _returnValue, clazz, _packageName, callingClass);
				}
			}
		} catch (Exception e) {

		}
		return _returnValue;
	}

	private static <T> void __doProcessURL(URL _url, Collection<Class<T>> collections, Class<T> clazz, String _packageName, Class<?> callingClass) throws URISyntaxException,
			IOException {

		// JBoss 个狗日的
		if (_url.getProtocol().equalsIgnoreCase("vfs")) {
			Object content = _url.openConnection().getContent();
			try {
				Class<?> czz = Class.forName("org.jboss.vfs.VirtualFile");
				Method m = czz.getMethod("getPhysicalFile");
				File physicalFile = (File) m.invoke(content);
				File[] _files = physicalFile.listFiles();
				for (File _file : _files != null ? _files : new File[0]) {
					__doFindClassByClazz(collections, clazz, _packageName, _file, callingClass);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (_url.getProtocol().equalsIgnoreCase("file") || _url.getProtocol().equalsIgnoreCase("vfsfile")) {
			File[] _files = new File(_url.toURI()).listFiles();
			for (File _file : _files != null ? _files : new File[0]) {
				__doFindClassByClazz(collections, clazz, _packageName, _file, callingClass);
			}
		} else if (_url.getProtocol().equalsIgnoreCase("jar") || _url.getProtocol().equalsIgnoreCase("wsjar")) {
			JarFile _jarFileObj = ((JarURLConnection) _url.openConnection()).getJarFile();
			__doFindClassByJar(collections, clazz, _packageName, _jarFileObj, callingClass);
		} else if (_url.getProtocol().equalsIgnoreCase("zip") || _url.getProtocol().equalsIgnoreCase("vfszip")) {
			__doFindClassByZip(collections, clazz, _packageName, _url, callingClass);
		}
	}

	@SuppressWarnings("unchecked")
	protected static <T> void __doFindClassByZip(Collection<Class<T>> collections, Class<T> clazz, String packageName, URL zipUrl, Class<?> callingClass) {
		packageName = packageName.replaceAll("\\.", "/");
		ZipInputStream _zipStream = null;
		try {
			String _zipFilePath = zipUrl.toString();
			String protocol = zipUrl.getProtocol();
			if (_zipFilePath.indexOf('!') > 0) {
				_zipFilePath = StringUtil.substringBetween(zipUrl.toString(), protocol + ":", "!");
			} else {
				_zipFilePath = StringUtil.substringAfter(zipUrl.toString(), protocol + ":");
			}
			_zipStream = new ZipInputStream(new FileInputStream(new File(_zipFilePath)));
			ZipEntry _zipEntry = null;
			while (null != (_zipEntry = _zipStream.getNextEntry())) {
				if (!_zipEntry.isDirectory()) {
					String zipName = _zipEntry.getName();
					if (zipName.indexOf(packageName) != -1 && zipName.endsWith(".class") && zipName.indexOf('$') < 0) {
						Class<?> _class = __doProcessEntry(zipUrl, _zipEntry);
						if (_class != null) {
							if (clazz.isAnnotation()) {
								if (isAnnotationOf(_class, (Class<Annotation>) clazz)) {
									collections.add((Class<T>) _class);
								}
							} else if (clazz.isInterface()) {
								if (isInterfaceOf(_class, clazz)) {
									collections.add((Class<T>) _class);
								}
							} else if (isSubclassOf(_class, clazz)) {
								collections.add((Class<T>) _class);
							}
						}
					}
				}
				_zipStream.closeEntry();
			}
		} catch (Exception e) {
		} finally {
			if (_zipStream != null) {
				try {
					_zipStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private static Class<?> __doProcessEntry(URL zipUrl, ZipEntry entry) {
		if (entry.getName().endsWith(".class")) {
			try {
				_INNER_CLASS_LOADER.addURL(zipUrl); // ~~是否需要进行去得判断呢？
				String _className = entry.getName().replace("/", ".");
				_className = _className.substring(0, _className.length() - 6);
				return Class.forName(_className, true, _INNER_CLASS_LOADER);
			} catch (Throwable e) {
			}
		}
		return null;
	}

	/**
	 * 获取文件路径中所有的类（所有以.class结尾，且不包含'$'内部类的）文件并将文件路径转换成Java类引用路径
	 * 
	 * @param collections
	 * @param clazz
	 * @param packageName
	 * @param packageFile
	 * @param callingClass
	 */
	@SuppressWarnings("unchecked")
	protected static <T> void __doFindClassByClazz(Collection<Class<T>> collections, Class<T> clazz, String packageName, File packageFile, Class<?> callingClass) {
		if (packageFile.isFile()) {
			try {
				if (packageFile.getName().endsWith(".class") && packageFile.getName().indexOf('$') < 0) {
					Class<?> _class = ResourceUtil.loadClass(packageName + "." + packageFile.getName().replace(".class", ""), callingClass);
					if (_class != null) {
						if (clazz.isAnnotation()) {
							if (isAnnotationOf(_class, (Class<Annotation>) clazz)) {
								collections.add((Class<T>) _class);
							}
						} else if (clazz.isInterface()) {
							if (isInterfaceOf(_class, clazz)) {
								collections.add((Class<T>) _class);
							}
						} else if (isSubclassOf(_class, clazz)) {
							collections.add((Class<T>) _class);
						}
					}
				}
			} catch (Exception e) {
			}
		} else {
			File[] _tmpfiles = packageFile.listFiles();
			for (File _tmpFile : _tmpfiles != null ? _tmpfiles : new File[0]) {
				__doFindClassByClazz(collections, clazz, packageName + "." + packageFile.getName(), _tmpFile, callingClass);
			}
		}
	}

	/**
	 * 获取 Jar 包中所有的类（所有以.class结尾，且不包含'$'内部类的）文件并将文件路径转换成Java类引用路径
	 * 
	 * @param collections
	 * @param clazz
	 * @param packageName
	 * @param jarFile
	 * @param callingClass
	 */
	@SuppressWarnings("unchecked")
	protected static <T> void __doFindClassByJar(Collection<Class<T>> collections, Class<T> clazz, String packageName, JarFile jarFile, Class<?> callingClass) {
		Enumeration<JarEntry> _entriesEnum = jarFile.entries();
		for (; _entriesEnum.hasMoreElements();) {
			JarEntry _entry = _entriesEnum.nextElement();
			// 替换文件名中所有的 '/' 为 '.'，并且只存放.class结尾的类名称，剔除所有包含'$'的内部类名称

			String _className = _entry.getName().replaceAll("/", ".");
			if (_className.endsWith(".class") && _className.indexOf('$') < 0) {
				if (_className.startsWith(packageName)) {
					Class<?> _class = null;
					try {
						_class = ResourceUtil.loadClass(_className.substring(0, _className.lastIndexOf('.')), callingClass);
						if (_class != null) {
							if (clazz.isAnnotation()) {
								if (isAnnotationOf(_class, (Class<Annotation>) clazz)) {
									collections.add((Class<T>) _class);
								}
							} else if (clazz.isInterface()) {
								if (isInterfaceOf(_class, clazz)) {
									collections.add((Class<T>) _class);
								}
							} else if (isSubclassOf(_class, clazz)) {
								collections.add((Class<T>) _class);
							}
						}
					} catch (Exception e) {
					}
				}
			}
		}
	}

	static class InnerClassLoader extends URLClassLoader {

		public InnerClassLoader(URL[] urls, ClassLoader parent) {
			super(urls, parent);
		}

		@Override
		public void addURL(URL url) {
			super.addURL(url);
		}

	}

	/**
	 * 获得指定名称、限定接口的实现类
	 * 
	 * @param <T>
	 * @param className
	 *            实现类名
	 * @param interfaceClass
	 *            限制接口名
	 * @param callingClass
	 * @return 如果可以得到并且限定于指定实现，那么返回实例，否则为空
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T impl(String className, Class<T> interfaceClass, Class<?> callingClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (StringUtil.notEmpty(className)) {
			Class<?> implClass = ResourceUtil.loadClass(className, callingClass);
			if (interfaceClass == null || interfaceClass.isAssignableFrom(implClass)) {
				return (T) implClass.newInstance();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T impl(Class<?> implClass, Class<T> interfaceClass) throws InstantiationException, IllegalAccessException {
		if (implClass != null) {
			if (interfaceClass == null || interfaceClass.isAssignableFrom(implClass)) {
				return (T) implClass.newInstance();
			}
		}
		return null;
	}

	/**
	 * 判断类clazz是否是superClass类的子类对象
	 * 
	 * @param clazz
	 * @param superClass
	 * @return
	 */
	public static boolean isSubclassOf(Class<?> clazz, Class<?> superClass) {
		boolean _flag = false;
		do {
			Class<?> cc = clazz.getSuperclass();
			if (cc != null) {
				if (cc.equals(superClass)) {
					_flag = true;
					break;
				} else {
					clazz = clazz.getSuperclass();
				}
			} else {
				break;
			}
		} while ((clazz != null && clazz != Object.class));
		return _flag;
	}

	/**
	 * @param clazz
	 *            目标对象
	 * @param interfaceClass
	 *            接口类型
	 * @return 判断clazz类中是否实现了interfaceClass接口
	 */
	public static boolean isInterfaceOf(Class<?> clazz, Class<?> interfaceClass) {
		boolean _flag = false;
		do {
			for (Class<?> cc : clazz.getInterfaces()) {
				if (cc.equals(interfaceClass)) {
					_flag = true;
				}
			}
			clazz = clazz.getSuperclass();
		} while (!_flag && (clazz != null && clazz != Object.class));
		return _flag;
	}

	/**
	 * @param target
	 *            目标对象，即可以是Field对象、Method对象或是Class对象
	 * @param annotationClass
	 *            注解类对象
	 * @return 判断target对象是否存在annotationClass注解
	 */
	public static boolean isAnnotationOf(Object target, Class<? extends Annotation> annotationClass) {
		if (target instanceof Field) {
			if (((Field) target).isAnnotationPresent(annotationClass)) {
				return true;
			}
		} else if (target instanceof Method) {
			if (((Method) target).isAnnotationPresent(annotationClass)) {
				return true;
			}
		} else if (target instanceof Class) {
			if (((Class<?>) target).isAnnotationPresent(annotationClass)) {
				return true;
			}
		}
		return false;
	}

}