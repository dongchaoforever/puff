package com.puff.ioc;

import com.puff.jdbc.tx.proxy.CglibProxy;
import com.puff.jdbc.tx.proxy.JDKProxy;
import com.puff.jdbc.tx.proxy.TXProxy;

public class TXProxyFactory {
	private static boolean cglibSupport = true;

	static {
		try {
			Class.forName("net.sf.cglib.proxy.MethodInterceptor");
		} catch (Exception e) {
			cglibSupport = false;
		}
	}

	public static TXProxy getProxy(Class<?> clazz) {
		TXProxy proxy = null;
		if (cglibSupport) {
			proxy = new CglibProxy(clazz);
		} else {
			proxy = new JDKProxy(clazz);
		}
		return proxy;
	}

}
