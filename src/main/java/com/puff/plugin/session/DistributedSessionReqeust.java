package com.puff.plugin.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.puff.framework.utils.IdentityUtil;
import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.web.mvc.HttpCookie;

/**
 * 
 * @author DC
 *
 */
public class DistributedSessionReqeust extends HttpServletRequestWrapper {

	private static final Log LOG = LogFactory.get(DistributedSessionReqeust.class);
	private static final int COOKIE_TIMELIVE = -1;
	private DistributedSession session;
	private HttpServletRequest request;
	private HttpServletResponse response;

	public DistributedSessionReqeust(HttpServletRequest request, HttpServletResponse response) {
		super(request);
		this.request = request;
		this.response = response;
	}

	@Override
	public HttpSession getSession() {
		return doGetSession(true);
	}

	@Override
	public HttpSession getSession(boolean create) {
		return doGetSession(create);
	}

	private HttpSession doGetSession(boolean create) {
		if (session == null) {
			Cookie cookie = null;
			Cookie[] cookies = super.getCookies();
			if (cookies != null) {
				for (Cookie c : cookies) {
					if (c.getName().equals(SessionConfig.INSTANCE.getSessionId())) {
						cookie = c;
						break;
					}
				}
			}
			if (cookie != null) {
				LOG.debug("Find Session ID from cookie [{0}]", cookie.getValue());
				session = buildSession(cookie.getValue(), false);
			} else {
				session = buildSession(create);
			}
		}
		return session;
	}

	private DistributedSession buildSession(String sessionId, boolean cookie) {
		DistributedSession session = new DistributedSession(sessionId);
		if (cookie) {
			HttpCookie.addCookie(request, response, SessionConfig.INSTANCE.getSessionId(), sessionId, SessionConfig.INSTANCE.getCookieDomain(),
					SessionConfig.INSTANCE.getCookiePath(), COOKIE_TIMELIVE);
		}
		return session;
	}

	private DistributedSession buildSession(boolean create) {
		if (create) {
			DistributedSession session = buildSession(IdentityUtil.uuid32().toUpperCase(), true);
			LOG.debug("Build new session[{0}].", session.getId());
			return session;
		}
		return null;
	}
}
