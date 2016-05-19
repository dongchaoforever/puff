package com.puff.framework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.puff.web.interceptor.Interceptor;

public interface Skip {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface ONE {
		Class<? extends Interceptor>value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface MORE {
		Class<? extends Interceptor>[]value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface ALL {

	}

}
