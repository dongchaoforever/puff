package com.puff.jdbc.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

public class PageRecord<T> implements Serializable {

	private static final long serialVersionUID = -4890964905769110400L;

	private int page = 1;
	private int pageSize = 20;
	private int totalPage;
	private int totalCount;
	private Collection<T> dataList = Collections.emptyList();

	public PageRecord() {

	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page > 0 ? page : 1;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		if (totalCount % pageSize == 0) {
			totalPage = totalCount / pageSize;
		} else {
			totalPage = totalCount / pageSize + 1;
		}
		if (page > totalPage) {
			page = totalPage < 1 ? 1 : totalPage;
		}
	}

	public Collection<T> getDataList() {
		return dataList;
	}

	public void setDataList(Collection<T> dataList) {
		this.dataList = dataList;
	}

	@Override
	public String toString() {
		return "PageRecord [page=" + page + ", totalPage=" + totalPage + ", totalCount=" + totalCount + ", dataList=" + dataList + "]";
	}

}
