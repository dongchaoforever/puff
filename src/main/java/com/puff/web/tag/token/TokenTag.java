package com.puff.web.tag.token;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.puff.framework.utils.IdentityUtil;

public class TokenTag extends SimpleTagSupport {
	public void doTag() throws JspException, IOException {
		JspContext context = super.getJspContext();
		String token = IdentityUtil.uuid32();
		context.setAttribute("server_token", token, PageContext.SESSION_SCOPE);
		String html = "<input type=\"hidden\" id=\"puff_client_token\" name=\"puff_client_token\" autocomplete=\"off\" value=\"" + token + "\" />";
		context.getOut().write(html);
	}
}