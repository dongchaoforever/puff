package com.puff.plugin.useragent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public enum OperatingSystem {

	WINDOWS(Manufacturer.MICROSOFT, null, 1, "Windows", new String[] { "Windows" }, new String[] { "Palm" }, DeviceType.COMPUTER, null), WINDOWS_7(Manufacturer.MICROSOFT, WINDOWS,
			21, "Windows 7", new String[] { "Windows NT 6.1" }, null, DeviceType.COMPUTER,
			null), WINDOWS_VISTA(Manufacturer.MICROSOFT, WINDOWS, 20, "Windows Vista", new String[] { "Windows NT 6" }, null, DeviceType.COMPUTER, null), WINDOWS_2000(
					Manufacturer.MICROSOFT, WINDOWS, 15, "Windows 2000", new String[] { "Windows NT 5.0" }, null, DeviceType.COMPUTER,
					null), WINDOWS_XP(Manufacturer.MICROSOFT, WINDOWS, 10, "Windows XP", new String[] { "Windows NT 5" }, null, DeviceType.COMPUTER, null), WINDOWS_MOBILE7(
							Manufacturer.MICROSOFT, WINDOWS, 51, "Windows Mobile 7", new String[] { "Windows Phone OS 7" }, null, DeviceType.MOBILE,
							null), WINDOWS_MOBILE(Manufacturer.MICROSOFT, WINDOWS, 50, "Windows Mobile", new String[] { "Windows CE" }, null, DeviceType.MOBILE, null), WINDOWS_98(
									Manufacturer.MICROSOFT, WINDOWS, 5, "Windows 98", new String[] { "Windows 98", "Win98" }, new String[] { "Palm" }, DeviceType.COMPUTER, null),

	ANDROID(Manufacturer.GOOGLE, null, 0, "Android", new String[] { "Android" }, null, DeviceType.MOBILE, null),

	ANDROID4(Manufacturer.GOOGLE, ANDROID, 4, "Android 4.x", new String[] { "Android 4" }, null, DeviceType.MOBILE, null), ANDROID3_TABLET(Manufacturer.GOOGLE, ANDROID, 30,
			"Android 3.x Tablet", new String[] { "Android 3" }, null, DeviceType.TABLET,
			null), ANDROID2(Manufacturer.GOOGLE, ANDROID, 2, "Android 2.x", new String[] { "Android 2" }, null, DeviceType.MOBILE, null), ANDROID2_TABLET(Manufacturer.GOOGLE,
					ANDROID2, 20, "Android 2.x Tablet", new String[] { "GT-P1000", "SCH-I800" }, null, DeviceType.TABLET,
					null), ANDROID1(Manufacturer.GOOGLE, ANDROID, 1, "Android 1.x", new String[] { "Android 1" }, null, DeviceType.MOBILE, null),

	WEBOS(Manufacturer.HP, null, 11, "WebOS", new String[] { "webOS" }, null, DeviceType.MOBILE, null), PALM(Manufacturer.HP, null, 10, "PalmOS", new String[] { "Palm" }, null,
			DeviceType.MOBILE, null),

	IOS(Manufacturer.APPLE, null, 2, "iOS", new String[] { "like Mac OS X" }, null, DeviceType.MOBILE, null), iOS5_IPHONE(Manufacturer.APPLE, IOS, 42, "iOS 5 (iPhone)",
			new String[] { "iPhone OS 5" }, null, DeviceType.MOBILE, null), iOS4_IPHONE(Manufacturer.APPLE, IOS, 41, "iOS 4 (iPhone)", new String[] { "iPhone OS 4" }, null,
					DeviceType.MOBILE, null), MAC_OS_X_IPAD(Manufacturer.APPLE, IOS, 50, "Mac OS X (iPad)", new String[] { "iPad" }, null, DeviceType.TABLET,
							null), MAC_OS_X_IPHONE(Manufacturer.APPLE, IOS, 40, "Mac OS X (iPhone)", new String[] { "iPhone" }, null, DeviceType.MOBILE,
									null), MAC_OS_X_IPOD(Manufacturer.APPLE, IOS, 30, "Mac OS X (iPod)", new String[] { "iPod" }, null, DeviceType.MOBILE, null),

	MAC_OS_X(Manufacturer.APPLE, null, 10, "Mac OS X", new String[] { "Mac OS X", "CFNetwork" }, null, DeviceType.COMPUTER, null),

	MAC_OS(Manufacturer.APPLE, null, 1, "Mac OS", new String[] { "Mac" }, null, DeviceType.COMPUTER, null),

	MAEMO(Manufacturer.NOKIA, null, 2, "Maemo", new String[] { "Maemo" }, null, DeviceType.MOBILE, null),

	BADA(Manufacturer.SAMSUNG, null, 2, "Bada", new String[] { "Bada" }, null, DeviceType.MOBILE, null),

	GOOGLE_TV(Manufacturer.GOOGLE, null, 100, "Android (Google TV)", new String[] { "GoogleTV" }, null, DeviceType.DMR, null),

	KINDLE(Manufacturer.AMAZON, null, 1, "Linux (Kindle)", new String[] { "Kindle" }, null, DeviceType.TABLET, null), KINDLE3(Manufacturer.AMAZON, KINDLE, 30, "Linux (Kindle 3)",
			new String[] { "Kindle/3" }, null, DeviceType.TABLET, null), KINDLE2(Manufacturer.AMAZON, KINDLE, 20, "Linux (Kindle 2)", new String[] { "Kindle/2" }, null,
					DeviceType.TABLET, null), LINUX(Manufacturer.OTHER, null, 2, "Linux", new String[] { "Linux", "CamelHttpStream" }, null, DeviceType.COMPUTER, null),

	SYMBIAN(Manufacturer.SYMBIAN, null, 1, "Symbian OS", new String[] { "Symbian", "Series60" }, null, DeviceType.MOBILE, null),

	SYMBIAN9(Manufacturer.SYMBIAN, SYMBIAN, 20, "Symbian OS 9.x", new String[] { "SymbianOS/9", "Series60/3" }, null, DeviceType.MOBILE, null),

	SYMBIAN8(Manufacturer.SYMBIAN, SYMBIAN, 15, "Symbian OS 8.x", new String[] { "SymbianOS/8", "Series60/2.6", "Series60/2.8" }, null, DeviceType.MOBILE, null),

	SYMBIAN7(Manufacturer.SYMBIAN, SYMBIAN, 10, "Symbian OS 7.x", new String[] { "SymbianOS/7" }, null, DeviceType.MOBILE, null),

	SYMBIAN6(Manufacturer.SYMBIAN, SYMBIAN, 5, "Symbian OS 6.x", new String[] { "SymbianOS/6" }, null, DeviceType.MOBILE, null),

	SERIES40(Manufacturer.NOKIA, null, 1, "Series 40", new String[] { "Nokia6300" }, null, DeviceType.MOBILE, null),

	SONY_ERICSSON(Manufacturer.SONY_ERICSSON, null, 1, "Sony Ericsson", new String[] { "SonyEricsson" }, null, DeviceType.MOBILE, null), SUN_OS(Manufacturer.SUN, null, 1, "SunOS",
			new String[] { "SunOS" }, null, DeviceType.COMPUTER,
			null), PSP(Manufacturer.SONY, null, 1, "Sony Playstation", new String[] { "Playstation" }, null, DeviceType.GAME_CONSOLE, null),

	WII(Manufacturer.NINTENDO, null, 1, "Nintendo Wii", new String[] { "Wii" }, null, DeviceType.GAME_CONSOLE, null),

	BLACKBERRY(Manufacturer.BLACKBERRY, null, 1, "BlackBerryOS", new String[] { "BlackBerry" }, null, DeviceType.MOBILE, null), BLACKBERRY7(Manufacturer.BLACKBERRY, BLACKBERRY, 7,
			"BlackBerry 7", new String[] { "Version/7" }, null, DeviceType.MOBILE,
			null), BLACKBERRY6(Manufacturer.BLACKBERRY, BLACKBERRY, 6, "BlackBerry 6", new String[] { "Version/6" }, null, DeviceType.MOBILE, null),

	BLACKBERRY_TABLET(Manufacturer.BLACKBERRY, null, 100, "BlackBerry Tablet OS", new String[] { "RIM Tablet OS" }, null, DeviceType.TABLET, null),

	ROKU(Manufacturer.ROKU, null, 1, "Roku OS", new String[] { "Roku" }, null, DeviceType.DMR, null), UNKNOWN(Manufacturer.OTHER, null, 1, "Unknown", new String[0], null,
			DeviceType.UNKNOWN, null);

	private final short id;
	private final String name;
	private final String[] aliases;
	private final String[] excludeList;
	private final Manufacturer manufacturer;
	private final DeviceType deviceType;
	private final OperatingSystem parent;
	private List<OperatingSystem> children;
	private OperatingSystem(Manufacturer manufacturer, OperatingSystem parent, int versionId, String name, String[] aliases, String[] exclude, DeviceType deviceType,
			String versionRegexString) {
		this.manufacturer = manufacturer;
		this.parent = parent;
		this.children = new ArrayList<OperatingSystem>();
		if (this.parent != null) {
			this.parent.children.add(this);
		}

		this.id = ((short) ((manufacturer.getId() << 8) + (byte) versionId));
		this.name = name;
		this.aliases = aliases;
		this.excludeList = exclude;
		this.deviceType = deviceType;
		if (versionRegexString != null)
			Pattern.compile(versionRegexString);
	}

	public short getId() {

		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public boolean isMobileDevice() {

		return this.deviceType.equals(DeviceType.MOBILE);
	}

	public DeviceType getDeviceType() {
		return this.deviceType;
	}

	public OperatingSystem getGroup() {

		if (this.parent != null) {
			return this.parent.getGroup();
		}
		return this;
	}

	public Manufacturer getManufacturer() {

		return this.manufacturer;
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

	private OperatingSystem checkUserAgent(String agentString) {
		if (isInUserAgentString(agentString)) {
			if (this.children.size() > 0) {
				for (OperatingSystem childOperatingSystem : this.children) {
					OperatingSystem match = childOperatingSystem.checkUserAgent(agentString);
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

	public static OperatingSystem parseUserAgentString(String agentString) {

		for (OperatingSystem operatingSystem : values()) {

			if (operatingSystem.parent == null) {
				OperatingSystem match = operatingSystem.checkUserAgent(agentString);
				if (match != null) {
					return match;
				}
			}
		}
		return UNKNOWN;
	}

	public static OperatingSystem valueOf(short id) {

		for (OperatingSystem operatingSystem : values()) {
			if (operatingSystem.getId() == id) {
				return operatingSystem;
			}
		}

		throw new IllegalArgumentException("No enum const for id " + id);
	}
}
