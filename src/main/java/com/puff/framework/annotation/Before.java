package com.puff.framework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.puff.web.interceptor.Interceptor;

@Retention(RetentionPolicy.RUNTIME)
public @interface Before {

	Class<? extends Interceptor>[] value();

	public boolean singleton() default true;

	FilterType type() default FilterType.TARGET;

	String[] method() default "";

}
