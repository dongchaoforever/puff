package com.puff.web.view;

public class NullView extends View {

	/**
	 * 
	 */

	private static NullView NULL_VIEW;

	private NullView() {

	}

	public static NullView getInstance() {
		if (NULL_VIEW == null) {
			NULL_VIEW = new NullView();
		}
		return NULL_VIEW;
	}

	@Override
	public void view() {

	}

}
