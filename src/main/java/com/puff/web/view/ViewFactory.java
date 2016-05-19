package com.puff.web.view;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import com.puff.web.view.View.ContentType;

public class ViewFactory {

	public static JspView jsp(String view) {
		return new JspView(view);
	}

	public static JspView jsp(String view, boolean supportRecord) {
		return new JspView(view, supportRecord);
	}

	public static JspView jsp(String view, String name, Object value) {
		return new JspView(view, name, value);
	}

	public static ActionView action(String actionUrl) {
		return new ActionView(actionUrl);
	}

	public static ActionView action(String actionUrl, Map<String, String> param) {
		return new ActionView(actionUrl, param);
	}

	public static RedirectView redirect(String actionUrl) {
		return new RedirectView(actionUrl);
	}

	public static RedirectView redirect(String actionUrl, Map<String, String> param) {
		return new RedirectView(actionUrl, param);
	}

	public static JsonView json() {
		return new JsonView();
	}

	public static JsonView json(Object value) {
		return new JsonView(value);
	}

	public static JsonView json(String name, Object value) {
		return new JsonView(name, value);
	}

	public static FileView file(String fileName) {
		return new FileView(fileName);
	}

	public static FileView file(String fileName, InputStream in) {
		return new FileView(fileName, in);
	}

	public static FileView file(File file) {
		return new FileView(file);
	}

	public static View text(String text) {
		return new TextView(text);
	}

	public static HtmlView html(String htmlContent) {
		return new HtmlView(htmlContent);
	}

	public static TextView text(String text, ContentType contentType) {
		return new TextView(text, contentType);
	}

	public static TextView text(String text, String contentType) {
		return new TextView(text, contentType);
	}

	public static XmlView xml(String xmlStr) {
		return new XmlView(xmlStr);
	}

	public static View nullView() {
		return NullView.getInstance();
	}

	public static BeetlView beetl(String view) {
		return new BeetlView(view);
	}

	public static AuthCodeView authCode() {
		return new AuthCodeView();
	}

	public static AuthCodeView authCode(int len) {
		return new AuthCodeView().setSize(len);
	}

	public static AuthCodeView authCode(int len, String cookieName) {
		return new AuthCodeView().setSize(len).setCookieName(cookieName);
	}

	public static AuthCodeView authCode(String authCode) {
		return new AuthCodeView().setAuthCode(authCode);
	}

	public static AuthCodeView authCode(String authCode, String cookieName) {
		return new AuthCodeView().setAuthCode(authCode).setCookieName(cookieName);
	}

}
