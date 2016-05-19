package com.puff.web.tag.page;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class PageTag extends SimpleTagSupport {
	private String url;
	private int totalCount;
	private int totalPage;
	private int page;
	private int breakPage = 8;
	private boolean showCount = true;
	private boolean showJump = true;

	public void setUrl(String url) {
		this.url = url;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setBreakPage(int breakPage) {
		this.breakPage = breakPage;
	}

	public void setShowCount(boolean showCount) {
		this.showCount = showCount;
	}

	public void setShowJump(boolean showJump) {
		this.showJump = showJump;
	}

	public void doTag() throws JspException, IOException {
		if (totalCount > 0) {
			StringBuilder sb = new StringBuilder(600);
			sb.append("<div id=\"pagination\" class=\"page\">\n");
			if (showCount) {
				sb.append("<span class=\"text\">共<font color=\"#FF6600\">" + totalCount + "</font>条记录</span> ");
			}
			if (page > 1) {
				sb.append("<a href=\"" + url + "/" + (page - 1) + "\">&laquo;上一页</a>\n");
			}
			if (totalPage <= breakPage) {
				for (int i = 1; i <= totalPage; i++) {
					sb.append(createPage(i));
				}
			} else {
				if (page <= 4) {
					for (int i = 1; i <= page + 4; i++) {
						sb.append(createPage(i));
					}
					sb.append("<span class=\"text\">...</span>\n");
					sb.append(createPage(totalPage));
				} else if (page > 4 && page <= totalPage - 4) {
					sb.append(createPage(1));
					sb.append("<span class=\"text\">...</span>\n");
					for (int i = page - 2; i < page + 3; i++) {
						sb.append(createPage(i));
					}
					sb.append("<span class=\"text\">...</span>\n");
					sb.append(createPage(totalPage));
				} else {
					sb.append(createPage(1));
					sb.append("<span class=\"text\">...</span>\n");
					for (int i = page - 2; i <= totalPage; i++) {
						sb.append(createPage(i));
					}
				}
			}
			if (page < totalPage) {
				sb.append("<a href=\"" + url + "/" + (page + 1) + "\">下一页&raquo;</a>\n");
			}
			if (showJump && totalPage > breakPage) {
				sb.append("<span class=\"page-skip\"><input class=\"jumpto\" id=\"jump\" type=\"text\" size=\"3\" value=" + page + " /></span>\n");
				sb.append("<a href=\"javascript:;\" >GO</a>\n");
			}
			sb.append("</div>");
			JspContext context = super.getJspContext();
			JspWriter writer = context.getOut();
			writer.write(sb.toString());
		}
	}

	private String createPage(int num) {
		if (page == num) {
			return "<a class=\"current\" href=\"javascript:;\">" + num + "</a>\n";
		} else {
			return "<a href=\"" + url + "/" + num + "\">" + num + "</a>\n";
		}
	}
}
