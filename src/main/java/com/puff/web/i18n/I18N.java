package com.puff.web.i18n;

import java.util.ArrayList;
import java.util.List;

import com.puff.core.Puff;

public enum I18N {
	INSTANCE;
	private List<Locale> locale = new ArrayList<Locale>();

	public List<Locale> getLocale() {
		return locale;
	}

	public void setLocale(List<Locale> locale) {
		this.locale = locale;
	}

	public static I18N get() {
		return Puff.getLocales();
	}

	public boolean contains(java.util.Locale locale) {
		for (Locale l : this.locale) {
			if (l.getLanguage().equals(l.getLanguage())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param str
	 *            e.g en | zh_CN
	 * @return
	 */
	public Locale get(String str) {
		for (Locale l : locale) {
			if (str.indexOf("_") > 0 && str.equals(l.toString())) {
				return l;
			} else if (!str.contains("_") && str.equals(l.getLanguage())) {
				return l;
			}
		}
		return locale.get(0);
	}

	@Override
	public String toString() {
		return "I18N [locale=" + locale + "]";
	}
}
