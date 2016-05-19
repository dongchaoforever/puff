package com.puff.framework.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

public class ResourceUtil {

	/**
	 * 
	 * @param resourceName
	 * @param callingClass
	 * @param aggregate
	 * @return
	 * @throws IOException
	 */
	public static Iterator<URL> getResources(String resourceName, Class<?> callingClass, boolean aggregate) throws IOException {
		AggregateIterator<URL> iterator = new AggregateIterator<URL>();
		iterator.addEnumeration(Thread.currentThread().getContextClassLoader().getResources(resourceName));
		if ((!iterator.hasNext()) || (aggregate)) {
			iterator.addEnumeration(PackageSearch.getDefaultClassLoader().getResources(resourceName));
		}
		if ((!iterator.hasNext()) || (aggregate)) {
			ClassLoader cl = callingClass.getClassLoader();
			if (cl != null) {
				iterator.addEnumeration(cl.getResources(resourceName));
			}
		}
		if ((!iterator.hasNext()) && (resourceName != null) && (((resourceName.length() == 0) || (resourceName.charAt(0) != '/')))) {
			return getResources('/' + resourceName, callingClass, aggregate);
		}
		return iterator;
	}

	/**
	 * 
	 * @param resourceName
	 * @param callingClass
	 * @return
	 */
	public static URL getResource(String resourceName, Class<?> callingClass) {
		URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
		if (url == null) {
			url = PackageSearch.getDefaultClassLoader().getResource(resourceName);
		}
		if (url == null) {
			url = callingClass.getResource(resourceName);
			if (url == null) {
				ClassLoader cl = callingClass.getClassLoader();
				if (cl != null) {
					url = cl.getResource(resourceName);
				}
			}
		}
		if ((url == null) && (resourceName != null) && (((resourceName.length() == 0) || (resourceName.charAt(0) != '/')))) {
			return getResource('/' + resourceName, callingClass);
		}
		return url;
	}

	/**
	 * 
	 * @param resourceName
	 * @param callingClass
	 * @return 以流的方式加载类资源，若加载失败则返回null
	 * @throws IOException
	 */
	public static InputStream getResourceAsStream(String resourceName, Class<?> callingClass) throws IOException {
		URL url = getResource(resourceName, callingClass);
		return (url != null) ? url.openStream() : null;
	}

	/**
	 * 
	 * @param className
	 * @param callingClass
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> loadClass(String className, Class<?> callingClass) throws ClassNotFoundException {
		Class<?> _targetClass = null;
		try {
			_targetClass = Thread.currentThread().getContextClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			try {
				_targetClass = Class.forName(className);
			} catch (ClassNotFoundException ex) {
				try {
					_targetClass = PackageSearch.getDefaultClassLoader().loadClass(className);
				} catch (ClassNotFoundException exc) {
					_targetClass = callingClass.getClassLoader().loadClass(className);
				}
			}
		}
		return _targetClass;
	}

	protected static class AggregateIterator<E> implements Iterator<E> {
		LinkedList<Enumeration<E>> enums;
		Enumeration<E> cur;
		E next;
		Set<E> loaded;

		protected AggregateIterator() {
			this.enums = new LinkedList<Enumeration<E>>();
			this.cur = null;
			this.next = null;
			this.loaded = new HashSet<E>();
		}

		public AggregateIterator<E> addEnumeration(Enumeration<E> e) {
			if (e.hasMoreElements()) {
				if (this.cur == null) {
					this.cur = e;
					this.next = e.nextElement();
					this.loaded.add(this.next);
				} else {
					this.enums.add(e);
				}
			}
			return this;
		}

		public boolean hasNext() {
			return this.next != null;
		}

		public E next() {
			if (this.next != null) {
				E prev = this.next;
				this.next = loadNext();
				return prev;
			}
			throw new NoSuchElementException();
		}

		private Enumeration<E> determineCurrentEnumeration() {
			if ((this.cur != null) && (!this.cur.hasMoreElements())) {
				if (this.enums.size() > 0)
					this.cur = this.enums.removeLast();
				else {
					this.cur = null;
				}
			}
			return this.cur;
		}

		private E loadNext() {
			if (determineCurrentEnumeration() != null) {
				E tmp = this.cur.nextElement();
				do {
					if (!this.loaded.contains(tmp))
						break;
					tmp = loadNext();
				} while (tmp != null);

				if (tmp != null) {
					this.loaded.add(tmp);
				}
				return tmp;
			}
			return null;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}