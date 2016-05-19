package com.puff.plugin.cache;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author dongchao
 *
 */
public interface Cache {

	/**
	 * key是否存在
	 * @param key
	 * @return
	 */
	public boolean exist(String key);

	/**
	 * 获取缓存
	 * @param key
	 * @return
	 */
	public <T extends Serializable> T get(String key);

	/**
	 * 获取缓存
	 * @param key
	 * @return
	 */
	public <T extends Serializable> List<T> get(List<String> keys);

	/**
	 * 获取缓存，取不到时调用CallBack接口获取数据
	 * @param key
	 * @param callBack
	 * @return
	 */
	public <T extends Serializable> T get(String key, CallBack<T> callBack);

	/**
	 * 获取缓存，取不到时调用CallBack接口获取数据 同时设置有效时间
	 * @param key
	 * @param callBack
	 * @return
	 */
	public <T extends Serializable> T get(String key, CallBack<T> callBack, int expire);

	/**
	 * 储存缓存，key存在直接覆盖
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value);

	/**
	 * 储存缓存，key存在直接覆盖，同时设置超时时间
	 * @param key
	 * @param value
	 * @param expire
	 */
	public void put(String key, Object value, int expire);

	/**
	 * 更新缓存，保留缓存超时时间 比如a有效期10分钟 5分钟后更新a a的超时时间还是5分钟
	 * @param key
	 * @param value
	 */
	public void update(String key, Object value);

	/**
	 * 获取缓存有效时间
	 * @param key
	 * @return
	 */
	public int ttl(String key);

	/**
	 * 获取 group所有key
	 * @return
	 */
	public List<String> keys();

	/**
	 * 移除缓存
	 * @param key
	 */
	public void remove(String key);

	/**
	 * 批量移除
	 * @param keys
	 */
	public void remove(List<String> keys);

	/**
	 * 清除cacheName内缓存
	 */
	public void clear();

}
