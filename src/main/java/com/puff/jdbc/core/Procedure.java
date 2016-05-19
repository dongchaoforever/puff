package com.puff.jdbc.core;

import java.util.ArrayList;
import java.util.List;

import com.puff.framework.utils.StringUtil;

public class Procedure {
	private String name;

	private List<Object> in;

	private List<ProcedureOut> out;

	public String getName() {
		if (StringUtil.notEmpty(name) && name.indexOf("\\{") != -1) {
			return name;
		}
		int size = 0;
		if (in != null) {
			size += in.size();
		}
		if (out != null) {
			size += out.size();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("{ call ").append(name).append("(");
		for (int i = 0; i < size; i++) {
			sb.append("?");
			if (i < size - 1) {
				sb.append(",");
			}
		}
		sb.append(") ");
		sb.append("} ");
		return sb.toString(); // {call PROC_XXX(?,?,?,?,?,?,?,?)}
	}

	public void setName(String name) {
		this.name = name;
	}

	public Procedure setIn(Object value) {
		if (in == null) {
			in = new ArrayList<Object>();
		}
		in.add(value);
		return this;
	}

	public Procedure setOut(String name, JdbcType jdbcType) {
		if (out == null) {
			out = new ArrayList<ProcedureOut>();
		}
		out.add(new ProcedureOut(name, jdbcType));
		return this;
	}

	public List<Object> getIn() {
		return in;
	}

	public List<ProcedureOut> getOut() {
		return out;
	}

}
