package com.puff.plugin.useragent;

public enum Application {

	HOTMAIL(Manufacturer.MICROSOFT, 1, "Windows Live Hotmail", new String[] { "mail.live.com", "hotmail.msn" }, ApplicationType.WEBMAIL),

	GMAIL(Manufacturer.GOOGLE, 5, "Gmail", new String[] { "mail.google.com" }, ApplicationType.WEBMAIL),

	YAHOO_MAIL(Manufacturer.YAHOO, 10, "Yahoo Mail", new String[] { "mail.yahoo.com" }, ApplicationType.WEBMAIL),

	COMPUSERVE(Manufacturer.COMPUSERVE, 20, "Compuserve", new String[] { "csmail.compuserve.com" }, ApplicationType.WEBMAIL),

	AOL_WEBMAIL(Manufacturer.AOL, 30, "AOL webmail", new String[] { "webmail.aol.com" }, ApplicationType.WEBMAIL),

	MOBILEME(Manufacturer.APPLE, 40, "MobileMe", new String[] { "www.me.com" }, ApplicationType.WEBMAIL),

	MAIL_COM(Manufacturer.MMC, 50, "Mail.com", new String[] { ".mail.com" }, ApplicationType.WEBMAIL),

	HORDE(Manufacturer.OTHER, 50, "horde", new String[] { "horde" }, ApplicationType.WEBMAIL),

	OTHER_WEBMAIL(Manufacturer.OTHER, 60, "Other webmail client", new String[] { "webmail", "webemail" }, ApplicationType.WEBMAIL),

	UNKNOWN(Manufacturer.OTHER, 0, "Unknown", new String[0], ApplicationType.UNKNOWN);

	private final short id;
	private final String name;
	private final String[] aliases;
	private final ApplicationType applicationType;
	private final Manufacturer manufacturer;

	private Application(Manufacturer manufacturer, int versionId, String name, String[] aliases, ApplicationType applicationType) {
		this.id = ((short) ((manufacturer.getId() << 8) + (byte) versionId));
		this.name = name;
		this.aliases = aliases;
		this.applicationType = applicationType;
		this.manufacturer = manufacturer;
	}

	public short getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public ApplicationType getApplicationType() {

		return this.applicationType;
	}

	public Manufacturer getManufacturer() {

		return this.manufacturer;
	}

	public boolean isInReferrerString(String referrerString) {

		for (String alias : this.aliases) {
			if (referrerString.toLowerCase().indexOf(alias.toLowerCase()) != -1)
				return true;
		}
		return false;
	}

	public static Application parseReferrerString(String referrerString) {

		if ((referrerString != null) && (referrerString.length() > 1)) {
			for (Application applicationInList : values()) {
				if (applicationInList.isInReferrerString(referrerString))
					return applicationInList;
			}
		}
		return UNKNOWN;
	}

	public static Application valueOf(short id) {

		for (Application application : values()) {
			if (application.getId() == id) {
				return application;
			}
		}

		throw new IllegalArgumentException("No enum const for id " + id);
	}
}
