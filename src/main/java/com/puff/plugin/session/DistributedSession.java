package com.puff.plugin.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import com.puff.core.Puff;
import com.puff.exception.SerializableException;
import com.puff.framework.utils.StringUtil;
import com.puff.log.Log;
import com.puff.log.LogFactory;

/**
 * 
 * 
 * 一个HttpSession的实现，实际的属性会储存在指定的缓存实现中。
 *
 * 以下方法没有实现。调用将抛出UnsupportedOperationException异常。 public HttpSessionContext
 * getSessionContext();
 *
 *
 */
@SuppressWarnings("deprecation")
public class DistributedSession implements HttpSession {
	private static final Log LOG = LogFactory.get(DistributedSession.class);
	private static final DataEngine DATA_ENGINE = SessionConfig.INSTANCE.getDataEngine();
	private String id;
	private String region_key;
	private int maxInactiveInterval = SessionConfig.INSTANCE.getSessionTimeout();
	private SessionInfo sessionInfo;
	private boolean invalid = false;

	public DistributedSession(String id) {
		this.id = id;
		this.region_key = SessionConfig.INSTANCE.getSessionId() + ":S:" + id;
		if (sessionInfo == null) {
			sessionInfo = (SessionInfo) DATA_ENGINE.get(region_key);
			if (sessionInfo == null) {
				LOG.warn("Session ID[{0}] is not found.", id);
				initSessionInfo(true);
			} else {
				if (sessionInfo.isNew()) {
					sessionInfo.setNew(false);
				}
			}
		}
		DATA_ENGINE.put(region_key, sessionInfo, maxInactiveInterval);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public long getCreationTime() {
		return sessionInfo.getCreateTime();
	}

	@Override
	public long getLastAccessedTime() {
		long lastAccessTime = sessionInfo.getLastAccessTime();
		access();
		return lastAccessTime;
	}

	public void access() {
		sessionInfo.setLastAccessTime(Calendar.getInstance().getTimeInMillis());
		DATA_ENGINE.put(region_key, sessionInfo, maxInactiveInterval);
	}

	@Override
	public ServletContext getServletContext() {
		return Puff.getServletContext();
	}

	@Override
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	@Override
	public Object getAttribute(String attributeName) {
		checkSessionInvalild();
		return sessionInfo.getAttribute(attributeName);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		checkSessionInvalild();
		Set<String> attributeNameSet = sessionInfo.getAttributeNames();
		return new Enumerator(attributeNameSet);
	}

	@Override
	public void setAttribute(String attributeName, Object attributeValue) {
		checkSessionInvalild();
		checkSerializable(attributeValue);
		sessionInfo.putAttribute(attributeName, (Serializable) attributeValue);
		DATA_ENGINE.put(region_key, sessionInfo, maxInactiveInterval);
	}

	@Override
	public void removeAttribute(String attributeName) {
		checkSessionInvalild();
		sessionInfo.removeAttribute(attributeName);
		sessionInfo.setLastAccessTime(System.currentTimeMillis());
		DATA_ENGINE.put(region_key, sessionInfo, maxInactiveInterval);
	}

	@Override
	public void invalidate() {
		LOG.debug("Session {0}, to fail.", id);
		DATA_ENGINE.remove(region_key);
		invalid = true;
	}

	public boolean isInvalid() {
		if (invalid) {
			return invalid;
		} else {
			if (maxInactiveInterval <= 0) {
				invalid = false;
				LOG.debug("Session [{0}] non-perishable.", id);
			} else {
				long invalidMillis = maxInactiveInterval * 1000;
				long lastAccessTime = getLastAccessedTime();
				long now = Calendar.getInstance().getTimeInMillis();
				invalid = (now - lastAccessTime) > invalidMillis;
				LOG.debug("Session {0}, last access time {1}, {2} the current time. Whether the failure of [{3}].", id, lastAccessTime, now, invalid);
			}
			return invalid;
		}
	}

	@Override
	public boolean isNew() {
		checkSessionInvalild();
		return sessionInfo.isNew();
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DistributedSession other = (DistributedSession) obj;
		if ((id == null) ? (other.getId() != null) : !id.equals(other.getId())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String message = "Session id is {0},detail is {1}.";
		return StringUtil.replaceArgs(message, getId(), sessionInfo);
	}

	@Override
	public HttpSessionContext getSessionContext() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void removeValue(String name) {
		removeAttribute(name);
	}

	@Override
	public void putValue(String name, Object value) {
		setAttribute(name, value);
	}

	@Override
	public String[] getValueNames() {
		Enumeration<String> attributeNames = getAttributeNames();
		List<String> attributeNameList = new ArrayList<String>();
		while (attributeNames.hasMoreElements()) {
			attributeNameList.add((String) attributeNames.nextElement());
		}
		return attributeNameList.toArray(new String[0]);
	}

	@Override
	public Object getValue(String name) {
		return getAttribute(name);
	}

	/**
	 * 初始化一个新的Session基本信息。
	 */
	private void initSessionInfo(boolean isNew) {
		Calendar now = Calendar.getInstance();
		sessionInfo = new SessionInfo();
		sessionInfo.setCreateTime(now.getTimeInMillis());
		sessionInfo.setLastAccessTime(now.getTimeInMillis());
		sessionInfo.setNew(isNew);
		LOG.info("Structural properties of container Map[{0}].", sessionInfo);
	}

	private void checkSerializable(Object value) throws SerializableException {
		if (value == null) {
			return;
		}
		if (!Serializable.class.isInstance(value)) {
			throw new SerializableException(value.getClass().getName());
		}
	}

	/**
	 * 判断当前Session是否已经失效.
	 * 
	 * @throws IllegalStateException
	 *             Session已经失效的异常.
	 */
	private void checkSessionInvalild() {
		if (invalid) {
			throw new IllegalStateException("Session is invalid.");
		}
	}

	private static class Enumerator implements Enumeration<String> {
		private Iterator<String> iter;

		public Enumerator(Set<String> attributeNames) {
			this.iter = attributeNames.iterator();
		}

		@Override
		public boolean hasMoreElements() {
			return iter.hasNext();
		}

		@Override
		public String nextElement() {
			return iter.next();
		}
	}

}
