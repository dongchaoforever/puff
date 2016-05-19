package com.puff.plugin.spring;

import org.springframework.context.ApplicationContext;

public class BeanContextHelper {
	protected static ApplicationContext ctx;

	private BeanContextHelper() {
	}

	public static Object getBean(String beanId) {
		return ctx.getBean(beanId);
	}
}