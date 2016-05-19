package com.puff.plugin.session;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.puff.log.Log;
import com.puff.log.LogFactory;

public class SessionInfo implements Externalizable {

	private static final Log LOG = LogFactory.get(SessionInfo.class);
	private transient static final String KEY_SPLIT = ",";
	private transient static String[] TYPE_STRING = new String[0];

	private boolean isNew;
	private long lastAccessTime;
	private long createTime;

	private Map<String, Serializable> attribute;

	public SessionInfo() {
		attribute = new ConcurrentHashMap<String, Serializable>();
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(createTime);
		out.writeLong(lastAccessTime);
		out.writeBoolean(isNew);
		Set<String> keys = attribute.keySet();
		String[] seriaKeys = keys.toArray(TYPE_STRING);
		Serializable[] seriaValues = new Serializable[seriaKeys.length];
		StringBuilder seriaKeyStr = new StringBuilder();
		for (int count = 0; count < seriaKeys.length; count++) {
			seriaValues[count] = attribute.get(seriaKeys[count]);
			seriaKeyStr.append(seriaKeys[count]);
			seriaKeyStr.append(KEY_SPLIT);
		}
		out.writeObject(seriaKeyStr.toString());
		out.writeObject(seriaValues);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		createTime = in.readLong();
		lastAccessTime = in.readLong();
		isNew = in.readBoolean();
		String seriaKeyStr = (String) in.readObject();
		String[] seriaKeys = seriaKeyStr.split(KEY_SPLIT);
		seriaKeyStr = null;
		Serializable[] seriaValues = (Serializable[]) in.readObject();
		if (seriaKeys != null && seriaValues != null && seriaKeys.length == seriaValues.length) {
			for (int count = 0; count < seriaKeys.length; count++) {
				attribute.put(seriaKeys[count], seriaValues[count]);
			}
		} else {
			LOG.warn("Session attribute serialization fails.");
		}

	}

	public void putAttribute(String name, Serializable value) {
		attribute.put(name, value);
		LOG.debug("Attribute [name = {0}, value = {1}], into the Session.", name, value);
	}

	public Serializable removeAttribute(String name) {
		LOG.debug("From the Session {0} removed property.", name);
		return attribute.remove(name);
	}

	public Serializable getAttribute(String name) {
		Serializable value = attribute.get(name);
		LOG.debug("Session to obtain property from [name ={0},value={1}].", name, value);
		return value;
	}

	public Set<String> getAttributeNames() {
		return attribute.keySet();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + (int) (createTime ^ (createTime >>> 32));
		result = prime * result + (int) (lastAccessTime ^ (lastAccessTime >>> 32));
		result = prime * result + (isNew ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SessionInfo other = (SessionInfo) obj;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!attribute.equals(other.attribute))
			return false;
		if (createTime != other.createTime)
			return false;
		if (lastAccessTime != other.lastAccessTime)
			return false;
		if (isNew != other.isNew)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SessionInfo [isNew=" + isNew + ", lastAccessTime=" + lastAccessTime + ", createTime=" + createTime + ", attribute=" + attribute + "]";
	}

}
