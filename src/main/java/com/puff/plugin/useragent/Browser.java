package com.puff.plugin.useragent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Browser {

	OPERA(Manufacturer.OPERA, null, 1, "Opera", new String[] { "Opera" }, null, BrowserType.WEB_BROWSER, RenderingEngine.PRESTO, "Opera\\/(([\\d]+)\\.([\\w]+))"), //

	OPERA_MINI(Manufacturer.OPERA, OPERA, 20, "Opera Mini", new String[] { "Opera Mini" }, null, BrowserType.MOBILE_BROWSER, RenderingEngine.PRESTO, null), //

	OPERA10(Manufacturer.OPERA, OPERA, 10, "Opera 10", new String[] { "Opera/9.8" }, null, BrowserType.WEB_BROWSER, RenderingEngine.PRESTO, "Version\\/(([\\d]+)\\.([\\w]+))"), //

	OPERA9(Manufacturer.OPERA, OPERA, 5, "Opera 9", new String[] { "Opera/9" }, null, BrowserType.WEB_BROWSER, RenderingEngine.PRESTO, null), //

	KONQUEROR(Manufacturer.OTHER, null, 1, "Konqueror", new String[] { "Konqueror" }, null, BrowserType.WEB_BROWSER, RenderingEngine.KHTML,
			"Konqueror\\/(([0-9]+)\\.?([\\w]+)?(-[\\w]+)?)"), //

	OUTLOOK(Manufacturer.MICROSOFT, null, 100, "Outlook", new String[] { "MSOffice" }, null, BrowserType.EMAIL_CLIENT, RenderingEngine.WORD, "MSOffice (([0-9]+))"), //

	OUTLOOK2007(Manufacturer.MICROSOFT, OUTLOOK, 107, "Outlook 2007", new String[] { "MSOffice 12" }, null, BrowserType.EMAIL_CLIENT, RenderingEngine.WORD, null), //

	OUTLOOK2010(Manufacturer.MICROSOFT, OUTLOOK, 108, "Outlook 2010", new String[] { "MSOffice 14" }, null, BrowserType.EMAIL_CLIENT, RenderingEngine.WORD, null), //

	IE(Manufacturer.MICROSOFT, null, 1, "Internet Explorer", new String[] { "MSIE" }, null, BrowserType.WEB_BROWSER, RenderingEngine.TRIDENT, "MSIE (([\\d]+)\\.([\\w]+))"), //

	OUTLOOK_EXPRESS7(Manufacturer.MICROSOFT, IE, 110, "Windows Live Mail", new String[] { "Outlook-Express/7.0" }, null, BrowserType.EMAIL_CLIENT, RenderingEngine.TRIDENT, null), //

	IEMOBILE9(Manufacturer.MICROSOFT, IE, 123, "IE Mobile 9", new String[] { "IEMobile/9" }, null, BrowserType.MOBILE_BROWSER, RenderingEngine.TRIDENT, null), //

	IEMOBILE7(Manufacturer.MICROSOFT, IE, 121, "IE Mobile 7", new String[] { "IEMobile 7" }, null, BrowserType.MOBILE_BROWSER, RenderingEngine.TRIDENT, null), //

	IEMOBILE6(Manufacturer.MICROSOFT, IE, 120, "IE Mobile 6", new String[] { "IEMobile 6" }, null, BrowserType.MOBILE_BROWSER, RenderingEngine.TRIDENT, null), //

	IE10(Manufacturer.MICROSOFT, IE, 92, "Internet Explorer 10", new String[] { "MSIE 10" }, null, BrowserType.WEB_BROWSER, RenderingEngine.TRIDENT, null), //

	IE9(Manufacturer.MICROSOFT, IE, 90, "Internet Explorer 9", new String[] { "MSIE 9" }, null, BrowserType.WEB_BROWSER, RenderingEngine.TRIDENT, null), //

	IE8(Manufacturer.MICROSOFT, IE, 80, "Internet Explorer 8", new String[] { "MSIE 8" }, null, BrowserType.WEB_BROWSER, RenderingEngine.TRIDENT, null), //

	IE7(Manufacturer.MICROSOFT, IE, 70, "Internet Explorer 7", new String[] { "MSIE 7" }, null, BrowserType.WEB_BROWSER, RenderingEngine.TRIDENT, null), //

	IE6(Manufacturer.MICROSOFT, IE, 60, "Internet Explorer 6", new String[] { "MSIE 6" }, null, BrowserType.WEB_BROWSER, RenderingEngine.TRIDENT, null), //

	IE5_5(Manufacturer.MICROSOFT, IE, 55, "Internet Explorer 5.5", new String[] { "MSIE 5.5" }, null, BrowserType.WEB_BROWSER, RenderingEngine.TRIDENT, null), //

	IE5(Manufacturer.MICROSOFT, IE, 50, "Internet Explorer 5", new String[] { "MSIE 5" }, null, BrowserType.WEB_BROWSER, RenderingEngine.TRIDENT, null), //

	CHROME(Manufacturer.GOOGLE, null, 1, "Chrome", new String[] { "Chrome" }, null, BrowserType.WEB_BROWSER, RenderingEngine.WEBKIT,
			"Chrome\\/(([0-9]+)\\.?([\\w]+)?(\\.[\\w]+)?(\\.[\\w]+)?)"), //

	CHROME15(Manufacturer.GOOGLE, CHROME, 20, "Chrome 15", new String[] { "Chrome/15" }, null, BrowserType.WEB_BROWSER, RenderingEngine.WEBKIT, null), //

	CHROME14(Manufacturer.GOOGLE, CHROME, 19, "Chrome 14", new String[] { "Chrome/14" }, null, BrowserType.WEB_BROWSER, RenderingEngine.WEBKIT, null), //

	CHROME13(Manufacturer.GOOGLE, CHROME, 18, "Chrome 13", new String[] { "Chrome/13" }, null, BrowserType.WEB_BROWSER, RenderingEngine.WEBKIT, null), //

	CHROME12(Manufacturer.GOOGLE, CHROME, 17, "Chrome 12", new String[] { "Chrome/12" }, null, BrowserType.WEB_BROWSER, RenderingEngine.WEBKIT, null), //

	CHROME11(Manufacturer.GOOGLE, CHROME, 16, "Chrome 11", new String[] { "Chrome/11" }, null, BrowserType.WEB_BROWSER, RenderingEngine.WEBKIT, null), //

	CHROME10(Manufacturer.GOOGLE, CHROME, 15, "Chrome 10", new String[] { "Chrome/10" }, null, BrowserType.WEB_BROWSER, RenderingEngine.WEBKIT, null), //

	CHROME9(Manufacturer.GOOGLE, CHROME, 10, "Chrome 9", new String[] { "Chrome/9" }, null, BrowserType.WEB_BROWSER, RenderingEngine.WEBKIT, null), //

	CHROME8(Manufacturer.GOOGLE, CHROME, 5, "Chrome 8", new String[] { "Chrome/8" }, null, BrowserType.WEB_BROWSER, RenderingEngine.WEBKIT, null), //

	OMNIWEB(Manufacturer.OTHER, null, 2, "Omniweb", new String[] { "OmniWeb" }, null, BrowserType.WEB_BROWSER, RenderingEngine.WEBKIT, null), //

	SAFARI(Manufacturer.APPLE, null, 1, "Safari", new String[] { "Safari" }, null, BrowserType.WEB_BROWSER, RenderingEngine.WEBKIT,
			"Version\\/(([0-9]+)\\.?([\\w]+)?(\\.[\\w]+)?)"), //

	SAFARI5(Manufacturer.APPLE, SAFARI, 3, "Safari 5", new String[] { "Version/5" }, null, BrowserType.WEB_BROWSER, RenderingEngine.WEBKIT, null), //

	MOBILE_SAFARI(Manufacturer.APPLE, SAFARI, 2, "Mobile Safari", new String[] { "Mobile Safari", "Mobile/" }, null, BrowserType.MOBILE_BROWSER, RenderingEngine.WEBKIT, null), //

	SAFARI4(Manufacturer.APPLE, SAFARI, 4, "Safari 4", new String[] { "Version/4" }, null, BrowserType.WEB_BROWSER, RenderingEngine.WEBKIT, null), //

	DOLFIN2(Manufacturer.SAMSUNG, null, 1, "Samsung Dolphin 2", new String[] { "Dolfin/2" }, null, BrowserType.MOBILE_BROWSER, RenderingEngine.WEBKIT, null), //

	APPLE_MAIL(Manufacturer.APPLE, null, 50, "Apple Mail", new String[] { "AppleWebKit" }, null, BrowserType.EMAIL_CLIENT, RenderingEngine.WEBKIT, null), //

	LOTUS_NOTES(Manufacturer.OTHER, null, 3, "Lotus Notes", new String[] { "Lotus-Notes" }, null, BrowserType.EMAIL_CLIENT, RenderingEngine.OTHER,
			"Lotus-Notes\\/(([\\d]+)\\.([\\w]+))"), //

	THUNDERBIRD(Manufacturer.MOZILLA, null, 110, "Thunderbird", new String[] { "Thunderbird" }, null, BrowserType.EMAIL_CLIENT, RenderingEngine.GECKO,
			"Thunderbird\\/(([0-9]+)\\.?([\\w]+)?(\\.[\\w]+)?(\\.[\\w]+)?)"), //

	THUNDERBIRD8(Manufacturer.MOZILLA, THUNDERBIRD, 180, "Thunderbird 8", new String[] { "Thunderbird/8" }, null, BrowserType.EMAIL_CLIENT, RenderingEngine.GECKO, null), //

	THUNDERBIRD7(Manufacturer.MOZILLA, THUNDERBIRD, 170, "Thunderbird 7", new String[] { "Thunderbird/7" }, null, BrowserType.EMAIL_CLIENT, RenderingEngine.GECKO, null), //

	THUNDERBIRD6(Manufacturer.MOZILLA, THUNDERBIRD, 160, "Thunderbird 6", new String[] { "Thunderbird/6" }, null, BrowserType.EMAIL_CLIENT, RenderingEngine.GECKO, null), //

	THUNDERBIRD3(Manufacturer.MOZILLA, THUNDERBIRD, 130, "Thunderbird 3", new String[] { "Thunderbird/3" }, null, BrowserType.EMAIL_CLIENT, RenderingEngine.GECKO, null), //

	THUNDERBIRD2(Manufacturer.MOZILLA, THUNDERBIRD, 120, "Thunderbird 2", new String[] { "Thunderbird/2" }, null, BrowserType.EMAIL_CLIENT, RenderingEngine.GECKO, null), //

	CAMINO(Manufacturer.OTHER, null, 5, "Camino", new String[] { "Camino" }, null, BrowserType.WEB_BROWSER, RenderingEngine.GECKO, "Camino\\/(([0-9]+)\\.?([\\w]+)?(\\.[\\w]+)?)"), //

	CAMINO2(Manufacturer.OTHER, CAMINO, 17, "Camino 2", new String[] { "Camino/2" }, null, BrowserType.WEB_BROWSER, RenderingEngine.GECKO, null), //

	FLOCK(Manufacturer.OTHER, null, 4, "Flock", new String[] { "Flock" }, null, BrowserType.WEB_BROWSER, RenderingEngine.GECKO, "Flock\\/(([0-9]+)\\.?([\\w]+)?(\\.[\\w]+)?)"), //

	FIREFOX(Manufacturer.MOZILLA, null, 10, "Firefox", new String[] { "Firefox" }, null, BrowserType.WEB_BROWSER, RenderingEngine.GECKO,
			"Firefox\\/(([0-9]+)\\.?([\\w]+)?(\\.[\\w]+)?(\\.[\\w]+)?)"), //

	FIREFOX3MOBILE(Manufacturer.MOZILLA, FIREFOX, 31, "Firefox 3 Mobile", new String[] { "Firefox/3.5 Maemo" }, null, BrowserType.MOBILE_BROWSER, RenderingEngine.GECKO, null), //

	FIREFOX9(Manufacturer.MOZILLA, FIREFOX, 90, "Firefox 9", new String[] { "Firefox/9" }, null, BrowserType.WEB_BROWSER, RenderingEngine.GECKO, null), //

	FIREFOX8(Manufacturer.MOZILLA, FIREFOX, 80, "Firefox 8", new String[] { "Firefox/8" }, null, BrowserType.WEB_BROWSER, RenderingEngine.GECKO, null), //

	FIREFOX7(Manufacturer.MOZILLA, FIREFOX, 70, "Firefox 7", new String[] { "Firefox/7" }, null, BrowserType.WEB_BROWSER, RenderingEngine.GECKO, null), //

	FIREFOX6(Manufacturer.MOZILLA, FIREFOX, 60, "Firefox 6", new String[] { "Firefox/6" }, null, BrowserType.WEB_BROWSER, RenderingEngine.GECKO, null), //

	FIREFOX5(Manufacturer.MOZILLA, FIREFOX, 50, "Firefox 5", new String[] { "Firefox/5" }, null, BrowserType.WEB_BROWSER, RenderingEngine.GECKO, null), //

	FIREFOX4(Manufacturer.MOZILLA, FIREFOX, 40, "Firefox 4", new String[] { "Firefox/4" }, null, BrowserType.WEB_BROWSER, RenderingEngine.GECKO, null), //

	FIREFOX3(Manufacturer.MOZILLA, FIREFOX, 30, "Firefox 3", new String[] { "Firefox/3" }, null, BrowserType.WEB_BROWSER, RenderingEngine.GECKO, null), //

	FIREFOX2(Manufacturer.MOZILLA, FIREFOX, 20, "Firefox 2", new String[] { "Firefox/2" }, null, BrowserType.WEB_BROWSER, RenderingEngine.GECKO, null), //

	FIREFOX1_5(Manufacturer.MOZILLA, FIREFOX, 15, "Firefox 1.5", new String[] { "Firefox/1.5" }, null, BrowserType.WEB_BROWSER, RenderingEngine.GECKO, null), //

	SEAMONKEY(Manufacturer.OTHER, null, 15, "SeaMonkey", new String[] { "SeaMonkey" }, null, BrowserType.WEB_BROWSER, RenderingEngine.GECKO,
			"SeaMonkey\\/(([0-9]+)\\.?([\\w]+)?(\\.[\\w]+)?)"), //

	BOT(Manufacturer.OTHER, null, 12, "Robot/Spider", new String[] { "Googlebot", "bot", "spider", "crawler", "Feedfetcher", "Slurp", "Twiceler", "Nutch", "BecomeBot" }, null,
			BrowserType.ROBOT, RenderingEngine.OTHER, null), //

	MOZILLA(Manufacturer.MOZILLA, null, 1, "Mozilla", new String[] { "Mozilla", "Moozilla" }, null, BrowserType.WEB_BROWSER, RenderingEngine.OTHER, null), //

	CFNETWORK(Manufacturer.OTHER, null, 6, "CFNetwork", new String[] { "CFNetwork" }, null, BrowserType.UNKNOWN, RenderingEngine.OTHER, null), //

	EUDORA(Manufacturer.OTHER, null, 7, "Eudora", new String[] { "Eudora", "EUDORA" }, null, BrowserType.EMAIL_CLIENT, RenderingEngine.OTHER, null), //

	POCOMAIL(Manufacturer.OTHER, null, 8, "PocoMail", new String[] { "PocoMail" }, null, BrowserType.EMAIL_CLIENT, RenderingEngine.OTHER, null), //

	THEBAT(Manufacturer.OTHER, null, 9, "The Bat!", new String[] { "The Bat" }, null, BrowserType.EMAIL_CLIENT, RenderingEngine.OTHER, null), //

	NETFRONT(Manufacturer.OTHER, null, 10, "NetFront", new String[] { "NetFront" }, null, BrowserType.MOBILE_BROWSER, RenderingEngine.OTHER, null), //

	EVOLUTION(Manufacturer.OTHER, null, 11, "Evolution", new String[] { "CamelHttpStream" }, null, BrowserType.EMAIL_CLIENT, RenderingEngine.OTHER, null), //

	LYNX(Manufacturer.OTHER, null, 13, "Lynx", new String[] { "Lynx" }, null, BrowserType.TEXT_BROWSER, RenderingEngine.OTHER,
			"Lynx\\/(([0-9]+)\\.([\\d]+)\\.?([\\w-+]+)?\\.?([\\w-+]+)?)"), //

	DOWNLOAD(Manufacturer.OTHER, null, 16, "Downloading Tool", new String[] { "cURL", "wget" }, null, BrowserType.TEXT_BROWSER, RenderingEngine.OTHER, null), //

	UNKNOWN(Manufacturer.OTHER, null, 14, "Unknown", new String[0], null, BrowserType.UNKNOWN, RenderingEngine.OTHER, null);//

	private final short id;
	private final String name;
	private final String[] aliases;
	private final String[] excludeList;
	private final BrowserType browserType;
	private final Manufacturer manufacturer;
	private final RenderingEngine renderingEngine;
	private final Browser parent;
	private List<Browser> children;
	private Pattern versionRegEx;

	private Browser(Manufacturer manufacturer, Browser parent, int versionId, String name, String[] aliases, String[] exclude, BrowserType browserType,
			RenderingEngine renderingEngine, String versionRegexString) {
		this.id = ((short) ((manufacturer.getId() << 8) + (byte) versionId));
		this.name = name;
		this.parent = parent;
		this.children = new ArrayList<Browser>();
		if (this.parent != null) {
			this.parent.children.add(this);
		}
		this.aliases = aliases;
		this.excludeList = exclude;
		this.browserType = browserType;
		this.manufacturer = manufacturer;
		this.renderingEngine = renderingEngine;
		if (versionRegexString != null)
			this.versionRegEx = Pattern.compile(versionRegexString);
	}

	public short getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	private Pattern getVersionRegEx() {
		if (this.versionRegEx == null) {
			if (getGroup() != this) {
				return getGroup().getVersionRegEx();
			}
			return null;
		}
		return this.versionRegEx;
	}

	public Version getVersion(String userAgentString) {

		Pattern pattern = getVersionRegEx();
		if ((userAgentString != null) && (pattern != null)) {
			Matcher matcher = pattern.matcher(userAgentString);
			if (matcher.find()) {
				String fullVersionString = matcher.group(1);
				String majorVersion = matcher.group(2);
				String minorVersion = "0";
				if (matcher.groupCount() > 2)
					minorVersion = matcher.group(3);
				return new Version(fullVersionString, majorVersion, minorVersion);
			}
		}
		return null;
	}

	public BrowserType getBrowserType() {

		return this.browserType;
	}

	public Manufacturer getManufacturer() {

		return this.manufacturer;
	}

	public RenderingEngine getRenderingEngine() {

		return this.renderingEngine;
	}

	public Browser getGroup() {

		if (this.parent != null) {
			return this.parent.getGroup();
		}
		return this;
	}

	public boolean isInUserAgentString(String agentString) {

		for (String alias : this.aliases) {
			if (agentString.toLowerCase().indexOf(alias.toLowerCase()) != -1)
				return true;
		}
		return false;
	}

	private boolean containsExcludeToken(String agentString) {

		if (this.excludeList != null) {
			for (String exclude : this.excludeList) {
				if (agentString.toLowerCase().indexOf(exclude.toLowerCase()) != -1)
					return true;
			}
		}
		return false;
	}

	private Browser checkUserAgent(String agentString) {
		if (isInUserAgentString(agentString)) {
			if (this.children.size() > 0) {
				for (Browser childBrowser : this.children) {
					Browser match = childBrowser.checkUserAgent(agentString);
					if (match != null) {
						return match;
					}
				}
			}

			if (!containsExcludeToken(agentString)) {
				return this;
			}
		}

		return null;
	}

	public static Browser parseUserAgentString(String agentString) {

		for (Browser browser : values()) {
			if (browser.parent == null) {
				Browser match = browser.checkUserAgent(agentString);
				if (match != null) {
					return match;
				}
			}
		}
		return UNKNOWN;
	}

	public static Browser valueOf(short id) {

		for (Browser browser : values()) {
			if (browser.getId() == id) {
				return browser;
			}
		}

		throw new IllegalArgumentException("No enum const for id " + id);
	}
}
