package com.puff.web.i18n;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.web.mvc.PuffContext;

public class Lang {

	private static final Log log = LogFactory.get(Lang.class);
	private static ThreadLocal<Locale> LOCAL = new ThreadLocal<Locale>();

	public static Locale get() {
		Locale locale = LOCAL.get();
		HttpServletRequest req = PuffContext.getRequest();
		if (locale == null) {
			if (req == null) {
				setDefaultLocale();
			} else {
				HttpServletResponse res = PuffContext.getResponse();
				resolve(req, res);
			}
			locale = LOCAL.get();
		}
		return locale;
	}

	private static void resolve(HttpServletRequest req, HttpServletResponse res) {
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("_PUFF_LOCAL".equals(cookie.getName())) {
					String loc = cookie.getValue();
					if (loc == null) {
						continue;
					}
					if (loc.indexOf("_") > 0) {
						String[] locs = loc.split("_");
						if (set(new Locale(locs[0], locs[1]))) {
							return;
						}
					}

					if (set(new Locale(loc))) {
						return;
					}
				}
			}
		}
		Locale locale = req.getLocale();
		set(locale);
		res.addCookie(new Cookie("_PUFF_LOCAL", locale.toString()));
	}

	public static boolean set(Locale locale) {
		if (I18N.INSTANCE.contains(locale)) {
			log.debug("Locale is set -> " + locale.toString());
			LOCAL.set(locale);
			return true;
		}
		log.warn("Locale " + locale + " is not defined in your puff.xml > i18n");
		return false;
	}

	public static void clear() {
		LOCAL.remove();
	}

	public static void change(Locale locale) {
		if (get() == null) {
			if (set(locale)) {
				PuffContext.getResponse().addCookie(new Cookie("_PUFF_LOCAL", locale.toString()));
			}
		} else {
			if (!get().equals(locale)) {
				if (set(locale)) {
					PuffContext.getResponse().addCookie(new Cookie("_PUFF_LOCAL", locale.toString()));
				}
			}
		}
	}

	private static void setDefaultLocale() {
		List<com.puff.web.i18n.Locale> locales = I18N.INSTANCE.getLocale();
		if (locales == null || locales.isEmpty()) {
			set(Locale.getDefault());
		} else {
			set(new Locale(locales.get(0).getLanguage(), locales.get(0).getCountry()));
		}
	}
}
