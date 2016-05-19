package com.puff.web.view;

import java.io.OutputStream;

import javax.servlet.http.Cookie;

import com.puff.exception.ViewException;
import com.puff.framework.utils.AuthCodeUtil;
import com.puff.framework.utils.IOUtil;
import com.puff.framework.utils.Security;
import com.puff.framework.utils.StringUtil;

public class AuthCodeView extends View {

	/**
	 * 
	 */
	private String authCode;
	public static String defCookieName = "_PUFF_AUTHCODE";
	public String sessionKey;
	private int size = 4;

	public AuthCodeView setCookieName(String defCookieName) {
		if (StringUtil.notBlank(defCookieName)) {
			AuthCodeView.defCookieName = defCookieName;
		}
		return this;
	}

	public AuthCodeView setAuthCode(String authCode) {
		if (StringUtil.notBlank(authCode) && authCode.length() >= 4 && authCode.length() < 7) {
			this.authCode = authCode;
		}
		return this;
	}

	public AuthCodeView setSize(int size) {
		if (size > 4 && size < 7) {
			this.size = size;
		}
		return this;
	}

	@Override
	public void view() {
		OutputStream out = null;
		try {
			out = response.getOutputStream();
			authCode = StringUtil.empty(authCode, AuthCodeUtil.getRandom(size));
			Cookie cookie = new Cookie(defCookieName, Security.md5(authCode.toUpperCase()));
			cookie.setMaxAge(-1);
			cookie.setPath(request.getContextPath());
			response.addCookie(cookie);
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setContentType("image/png");
			AuthCodeUtil.draw(out, authCode);
		} catch (Exception e) {
			throw new ViewException(e);
		} finally {
			IOUtil.close(out);
		}
	}
}