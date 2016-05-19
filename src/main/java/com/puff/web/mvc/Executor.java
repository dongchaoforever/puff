package com.puff.web.mvc;

import java.lang.reflect.Method;

/**
 * Executor
 */
public final class Executor {

	public final String beanId;
	public final String controllerKey;
	public final String executorKey;
	public final String requestMethod;
	public final Class<?> returnType;
	public final Class<?>[] argTypes;
	public final String methodName;
	public final int methodIdx;
	public final String[] interceptors;
	public final boolean report;

	public Executor(String beanId, String controllerKey, String executorKey, String requestMethod, Method method, int methodIdx, String[] interceptors, boolean report) {
		this.beanId = beanId;
		this.controllerKey = controllerKey;
		this.executorKey = executorKey;
		this.requestMethod = requestMethod;
		this.returnType = method.getReturnType();
		this.argTypes = method.getParameterTypes();
		this.methodName = method.getName();
		this.interceptors = interceptors;
		this.report = report;
		this.methodIdx = methodIdx;
	}

	public Object execute(Object target, Object[] args) {
		return ExecutorMethod.invokeMethod(target, this.beanId, this.methodIdx, args);
	}

}
