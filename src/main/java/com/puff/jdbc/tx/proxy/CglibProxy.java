package com.puff.jdbc.tx.proxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.puff.exception.ExceptionUtil;
import com.puff.framework.annotation.Transaction;
import com.puff.jdbc.core.DataBase;
import com.puff.jdbc.core.DbManager;

/**
 * 事务拦截器
 * 
 * @author dongchao
 * @since 0.1
 */
public class CglibProxy implements TXProxy, MethodInterceptor {

	private Class<?> clazz;

	@Override
	public Object getProxy() {
		return Enhancer.create(clazz, this);
	}

	public CglibProxy(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		Object result = null;
		if (method.isAnnotationPresent(Transaction.class)) {
			if (DataBase.inTransaction()) {
				return proxy.invokeSuper(obj, args);
			}
			try {
				Transaction tran = method.getAnnotation(Transaction.class);
				DataBase.setThreadLocalConnection(DbManager.getConnection(tran.value()));
				DataBase.beginTransaction();
				result = proxy.invokeSuper(obj, args);
				DataBase.commitTransaction();
			} catch (Exception ex) {
				DataBase.rollbackTransaction();
				ExceptionUtil.throwRuntime(ex);
			} finally {
				DataBase.removeThreadLocalConnection();
			}
		} else {
			result = proxy.invokeSuper(obj, args);
		}
		return result;
	}

}
