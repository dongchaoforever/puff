package com.puff.web.mvc;

import java.util.List;

/**
 * DispatcherFactory.
 */
public class DispatcherFactory {

	private DispatcherFactory() {

	}

	/**
	 * Build Dispatcher chain
	 */
	public static Dispatcher getDispatcher(List<Dispatcher> handlerList, Dispatcher dispater) {
		Dispatcher result = dispater;
		if (handlerList != null) {
			for (int i = handlerList.size() - 1; i >= 0; i--) {
				Dispatcher temp = handlerList.get(i);
				temp.chain = result;
				result = temp;
			}
		}
		return result;
	}

	public static Dispatcher addDispatcher(Dispatcher oldDispater, Dispatcher dispater) {
		dispater.chain = oldDispater;
		return dispater;
	}
}
