package com.puff.plugin.spring;

import java.util.Properties;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.puff.plugin.Plugin;

public class SpringPlugin implements Plugin {

	@Override
	public void init(Properties prop) {
	}

	@Override
	public boolean start() {
		BeanContextHelper.ctx = new ClassPathXmlApplicationContext("spring.xml");
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}

}
