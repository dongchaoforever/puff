package com.puff.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.puff.core.Puff;
import com.puff.log.Log;
import com.puff.log.LogFactory;

public class PuffListener implements ServletContextListener {
	private static final Log LOG = LogFactory.get();

	public void contextInitialized(ServletContextEvent event) {
		LOG.debug("contextInitialized......");
		Puff.start(event.getServletContext());
	}

	public void contextDestroyed(ServletContextEvent event) {
		LOG.debug("contextDestroyed.......");
	}
}