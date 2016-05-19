package com.puff.framework.annotation;

public @interface Token {
	String value() default "puff_client_token";
}
