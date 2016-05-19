package com.puff.core;

import java.util.HashMap;
import java.util.Map;

import com.puff.framework.parse.XNode;

public class Config {

	protected XNode node;

	protected Map<String, String> getInfo() {
		Map<String, String> constantMap = new HashMap<String, String>();
		if (node != null) {
			for (XNode child : node.getChildren()) {
				String name = child.getStringAttribute("name");
				String value = child.getStringAttribute("value");
				constantMap.put(name, value);
			}
		}
		return constantMap;
	}

	protected Map<String, String> getConstantsInfo(String key) {
		Map<String, String> constantMap = Puff.getConstantsMap();
		if (node != null) {
			XNode parnet = node.evalNode(key);
			if (parnet != null) {
				for (XNode child : parnet.getChildren()) {
					String name = child.getStringAttribute("name");
					String value = child.getStringAttribute("value");
					constantMap.put(name, value);
				}
			}
		}
		return constantMap;
	}

	protected Map<String, String> getInfo(String key) {
		Map<String, String> map = new HashMap<String, String>();
		if (node != null) {
			XNode parnet = node.evalNode(key);
			if (parnet != null) {
				for (XNode child : parnet.getChildren()) {
					String name = child.getStringAttribute("name");
					String value = child.getStringAttribute("value");
					map.put(name, value);
				}
			}
		}
		return map;
	}

}
