package com.puff.framework.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ListUtil {

	public static final String default_split = ",";

	public static <E> boolean empty(Collection<E> collection) {
		return collection == null || collection.size() < 1;
	}

	public static <E> boolean notEmpty(Collection<E> collection) {
		return collection != null && collection.size() > 0;
	}

	public static String list2Str(Collection<String> collection) {
		return list2Str(collection, default_split);
	}

	public static String list2Str(Collection<String> collection, String split) {
		StringBuilder sb = new StringBuilder();
		if (collection != null) {
			int i = 0, size = collection.size();
			for (Iterator<String> iterator = collection.iterator(); iterator.hasNext();) {
				String str = iterator.next();
				sb.append(str);
				if (++i < size) {
					sb.append(split);
				}
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		List<String> list = Arrays.asList("a", "b", "c", "ds", "e");
		System.out.println(list2Str(list));
	}

}
