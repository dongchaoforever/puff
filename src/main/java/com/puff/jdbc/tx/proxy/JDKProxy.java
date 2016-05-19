package com.puff.jdbc.tx.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.puff.exception.ExceptionUtil;
import com.puff.framework.annotation.Transaction;
import com.puff.framework.utils.ClassUtil;
import com.puff.jdbc.core.DataBase;
import com.puff.jdbc.core.DbManager;

public class JDKProxy implements TXProxy, InvocationHandler {

	private Object target;

	public JDKProxy(Object target) {
		super();
		this.target = target;
	}

	public JDKProxy(Class<?> target) {
		super();
		this.target = ClassUtil.newInstance(target.getName());
	}

	public Object getProxy() {
		return Proxy.newProxyInstance(this.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;
		method.setAccessible(true);
		method = target.getClass().getMethod(method.getName(), method.getParameterTypes());
		if (method.isAnnotationPresent(Transaction.class)) {
			if (DataBase.inTransaction()) {
				return method.invoke(target, args);
			}
			try {
				Transaction tran = method.getAnnotation(Transaction.class);
				DataBase.setThreadLocalConnection(DbManager.getConnection(tran.value()));
				DataBase.beginTransaction();
				result = method.invoke(target, args);
				DataBase.commitTransaction();
			} catch (Exception ex) {
				DataBase.rollbackTransaction();
				ExceptionUtil.throwRuntime(ex);
			} finally {
				DataBase.removeThreadLocalConnection();
			}
		} else {
			result = method.invoke(target, args);
		}
		return result;
	}

}
