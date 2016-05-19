package com.puff.jdbc.core;

public class ColumnInfo {
	private String name;

	private int idx;

	private int type;

	public ColumnInfo(String name, int idx, int type) {
		super();
		this.name = name;
		this.idx = idx;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
