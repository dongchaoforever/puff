package com.puff.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.puff.jdbc.core.FieldProcessor;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Column {
	public String value() default "";

	public boolean alias() default false;

	public String function() default "";

	public Class<? extends FieldProcessor> processor() default FieldProcessor.class;

}