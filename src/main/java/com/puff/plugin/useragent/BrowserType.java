package com.puff.plugin.useragent;

public enum BrowserType {

	WEB_BROWSER("Browser"),

	MOBILE_BROWSER("Browser (mobile)"),

	TEXT_BROWSER("Browser (text only)"),

	EMAIL_CLIENT("Email Client"),

	ROBOT("Robot"),

	TOOL("Downloading tool"), UNKNOWN("unknown");

	private String name;

	private BrowserType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
