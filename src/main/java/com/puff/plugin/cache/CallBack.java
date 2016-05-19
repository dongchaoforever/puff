package com.puff.plugin.cache;

public interface CallBack<T> {

	public T call(String key);

}