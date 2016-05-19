package com.puff.plugin.session;

public enum SessionConfig {
	INSTANCE;

	public static final String DEFAULT_SESSIONID = "__PUFF_SESSIONID_";

	private String cookieDomain;

	private String sessionId;

	private String cookiePath;

	private int sessionTimeout;

	private DataEngine dataEngine;

	public String getCookieDomain() {
		return cookieDomain;
	}

	public void setCookieDomain(String cookieDomain) {
		this.cookieDomain = cookieDomain;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getCookiePath() {
		return cookiePath;
	}

	public void setCookiePath(String cookiePath) {
		this.cookiePath = cookiePath;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public DataEngine getDataEngine() {
		return dataEngine;
	}

	public void setDataEngine(DataEngine dataEngine) {
		this.dataEngine = dataEngine;
	}

}
